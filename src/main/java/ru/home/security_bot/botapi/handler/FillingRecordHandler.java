package ru.home.security_bot.botapi.handler;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.botapi.InputMessageHandler;
import ru.home.security_bot.cache.UserDataCache;
import ru.home.security_bot.model.RecordData;
import ru.home.security_bot.service.ReplyMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FillingRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;
    private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public FillingRecordHandler(UserDataCache userDataCache, ReplyMessageService replyMessageService) {
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILL_RECORD)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_FLAT);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILL_RECORD;
    }

    private SendMessage processUsersInput(Message message) {
        String userAnswer = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();

        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        RecordData recordData = userDataCache.getRecordData(userId);

        SendMessage sendMessage = null;

        switch (botState) {
            case ASK_FLAT:
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askFlat");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PHONE_NUMBER);
                break;
            case ASK_PHONE_NUMBER:
                try {
                    sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askPhoneNumber");
                    int flatNumber = Integer.parseInt(userAnswer);
                    if (flatNumber > 0 && flatNumber < 2570) {
                        recordData.setFlatNumber(flatNumber);
                        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_MARK);
                    } else {
                        sendMessage = new SendMessage(chatId, "Неверный номер квартиры! Введите заново : ");
                        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PHONE_NUMBER);
                    }
                } catch (Exception e) {
                    sendMessage = new SendMessage(chatId, "Неверный номер квартиры! Введите заново : ");
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PHONE_NUMBER);
                }
                break;
            case ASK_CAR_MARK:
                try {
                    Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(userAnswer, "RU");
                    if (phoneUtil.isValidNumber(phoneNumberProto)) {
                        sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarMark");
                        sendMessage.setReplyMarkup(getUnknownMark());
                        recordData.setPhoneNumber(userAnswer);
                        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_NUMBER);
                    } else {
                        sendMessage = new SendMessage(chatId, "Неверный номер телефона! Введите телефон заново : ");
                        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_MARK);
                    }
                } catch (NumberParseException e) {
                    sendMessage = new SendMessage(chatId, "Неверный номер телефона! Введите телефон заново : ");
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_MARK);
                }
                break;
            case ASK_CAR_NUMBER:
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarNUmber");
                recordData.setCarMark(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
                break;
            case RECORD_DATA_FILLED:
                Pattern pattern = Pattern.compile("^[А-Я][0-9]{3}[А-Я]{2}[0-9]{2,3}$");
                Matcher matcher = pattern.matcher(userAnswer);
                if (matcher.matches()) {
                    recordData.setCarNumber(userAnswer);
                    userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
                    sendMessage = new SendMessage(message.getChatId(), String.format("%s%n -------------------%nНомер квартиры: %s%nНомер телефона: %s%nМарка автомобиля: %s%nНомер автомобиля: %s%n",
                            "Данные по вашей заявке", recordData.getFlatNumber(), recordData.getPhoneNumber(), recordData.getCarMark(), recordData.getCarNumber()));
                } else {
                    sendMessage = new SendMessage(chatId, "Неверный номер автомобиля! Введите заново : ");
                    userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
                }

                break;
        }
        userDataCache.saveRecordData(userId, recordData);

        return sendMessage;
    }

    private InlineKeyboardMarkup getUnknownMark() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton().setText("Не знаю марку");

        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("UnknownMark");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonGenderMan);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
