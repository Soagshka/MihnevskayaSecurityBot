package ru.home.security_bot.botapi.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.botapi.InputMessageHandler;
import ru.home.security_bot.cache.UserDataCache;
import ru.home.security_bot.model.RecordData;
import ru.home.security_bot.service.ReplyMessageService;

@Component
public class FillingRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;

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
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarMark");
                recordData.setPhoneNumber(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_NUMBER);
                break;
            case ASK_CAR_NUMBER:
                sendMessage = replyMessageService.getReplyMessage(chatId, "reply.askCarNUmber");
                recordData.setCarMark(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
                break;
            case RECORD_DATA_FILLED:
                recordData.setCarNumber(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.FILL_RECORD);
                sendMessage = new SendMessage(chatId, String.format("%s %s", "Данные по записи", recordData));
                break;
        }
        userDataCache.saveRecordData(userId, recordData);

        return sendMessage;
    }
}
