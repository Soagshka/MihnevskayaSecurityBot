package ru.home.security_bot.botapi.handler;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.botapi.InputMessageHandler;
import ru.home.security_bot.cache.UserDataCache;
import ru.home.security_bot.model.RecordData;
import ru.home.security_bot.service.ReplyMessageService;

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
                    recordData.setFlatNumber(Integer.parseInt(userAnswer));
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_MARK);
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
                Pattern pattern = Pattern.compile("^[А-Я][0-9]{3}[А-Я]{2}[0-9]{2,3}$");
                Matcher matcher = pattern.matcher(userAnswer);
                if (matcher.matches()) {
                    recordData.setCarMark(userAnswer);
                    userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
                } else {
                    sendMessage = new SendMessage(chatId, "Неверный номер автомобиля! Введите заново : ");
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_NUMBER);
                }
                break;
            case RECORD_DATA_FILLED:
                recordData.setCarNumber(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.FILL_RECORD);
                sendMessage = new SendMessage(chatId, "Номер квартиры = " + recordData.getFlatNumber() + ", номер телефона = " +
                        recordData.getPhoneNumber() + ", марка автомобиля = " + recordData.getCarMark() + ", номер автомобиля = " + recordData.getCarNumber());
                break;
        }
        userDataCache.saveRecordData(userId, recordData);

        return sendMessage;
    }
}
