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
import ru.home.security_bot.dao.BotStateEntity;
import ru.home.security_bot.dao.RecordDataEntity;
import ru.home.security_bot.dao.repository.BotStateRepository;
import ru.home.security_bot.dao.repository.RecordDataRepository;
import ru.home.security_bot.mapper.RecordDataMapper;
import ru.home.security_bot.model.RecordData;
import ru.home.security_bot.service.ReplyMessageService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FillingRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;
    private RecordDataRepository recordDataRepository;
    private BotStateRepository botStateRepository;
    private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public FillingRecordHandler(UserDataCache userDataCache, ReplyMessageService replyMessageService, RecordDataRepository recordDataRepository, BotStateRepository botStateRepository) {
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
        this.recordDataRepository = recordDataRepository;
        this.botStateRepository = botStateRepository;
    }

    @Override
    public SendMessage handle(Message message) {
        BotState botState = BotState.ASK_FLAT;
        BotStateEntity botStateEntity = botStateRepository.findByUserIdAndChatId(message.getFrom().getId(), message.getChatId());
        if (botStateEntity != null) {
            if (!botStateEntity.getBotState().equals("FILL_RECORD")) {
                botState = BotState.valueOf(botStateEntity.getBotState());
            }
        }

        return processUsersInput(message, botState);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILL_RECORD;
    }

    private SendMessage processUsersInput(Message message, BotState botState) {
        String userAnswer = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();

        RecordData recordData = userDataCache.getRecordData(userId);

        SendMessage sendMessage = null;

        switch (botState) {
            case ASK_FLAT:
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askFlat");
                botState = BotState.ASK_PHONE_NUMBER;
//                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PHONE_NUMBER);
                break;
            case ASK_PHONE_NUMBER:
                try {
                    sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askPhoneNumber");
                    int flatNumber = Integer.parseInt(userAnswer);
                    if (flatNumber > 0 && flatNumber < 2570) {
                        recordData.setFlatNumber(flatNumber);
                        botState = BotState.ASK_CAR_MARK;
//                        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_MARK);
                    } else {
                        sendMessage = new SendMessage(chatId, "Неверный номер квартиры! Введите заново : ");
                    }
                } catch (Exception e) {
                    sendMessage = new SendMessage(chatId, "Неверный номер квартиры! Введите заново : ");
                }
                break;
            case ASK_CAR_MARK:
                try {
                    Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(userAnswer, "RU");
                    if (phoneUtil.isValidNumber(phoneNumberProto)) {
                        sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarMark");
                        sendMessage.setReplyMarkup(getUnknownMark());
                        recordData.setPhoneNumber(userAnswer);
                        botState = BotState.ASK_CAR_NUMBER;
                        //userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_NUMBER);
                    } else {
                        sendMessage = new SendMessage(chatId, "Неверный номер телефона! Введите телефон заново : ");
                    }
                } catch (NumberParseException e) {
                    sendMessage = new SendMessage(chatId, "Неверный номер телефона! Введите телефон заново : ");
                }
                break;
            case ASK_CAR_NUMBER:
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarNUmber");
                recordData.setCarMark(userAnswer);
                botState = BotState.RECORD_DATA_FILLED;
                //userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
                break;
            case RECORD_DATA_FILLED:
                Pattern pattern = Pattern.compile("^[А-Я][0-9]{3}[А-Я]{2}[0-9]{2,3}$");
                Matcher matcher = pattern.matcher(userAnswer);
                if (matcher.matches()) {
                    recordData.setCarNumber(userAnswer);
                    botState = BotState.SHOW_MAIN_MENU;
                    //userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
                    sendMessage = new SendMessage(message.getChatId(), String.format("%s%n -------------------%nНомер квартиры: %s%nНомер телефона: %s%nМарка автомобиля: %s%nНомер автомобиля: %s%n",
                            "Данные по вашей заявке", recordData.getFlatNumber(), recordData.getPhoneNumber(), recordData.getCarMark(), recordData.getCarNumber()));
                } else {
                    sendMessage = new SendMessage(chatId, "Неверный номер автомобиля! Введите заново : ");
                }

                break;
        }
        saveBotState(userId, chatId, botState);
        RecordDataEntity recordDataEntity = RecordDataMapper.RECORD_DATA_MAPPER.recordDataToRecordEntity(recordData);
        recordDataEntity.setRecordDate(new Date(System.currentTimeMillis()));
        recordDataEntity.setUserId(userId);
        recordDataEntity.setChatId(chatId);
        recordDataRepository.save(recordDataEntity);
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

    private void saveBotState(int userId, Long chatId, BotState botState) {
        BotStateEntity botStateEntity = botStateRepository.findByUserIdAndChatId(userId, chatId);
        botStateEntity.setBotState(botState.getDescription());
        botStateRepository.save(botStateEntity);
    }
}
