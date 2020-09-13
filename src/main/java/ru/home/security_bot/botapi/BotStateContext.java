package ru.home.security_bot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(inputMessageHandler -> this.messageHandlers.put(inputMessageHandler.getHandlerName(), inputMessageHandler));
    }

    public SendMessage processInputMessage(BotState botState, Message message) {
        InputMessageHandler inputMessageHandler = findMessageHandler(botState);
        return inputMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState botState) {
        if (isFillingProfileState(botState)) {
            System.out.println("MESSAGE HANDLERS : " + messageHandlers.toString());
            return messageHandlers.get(BotState.FILL_RECORD);
        }
        return messageHandlers.get(botState);
    }

    private boolean isFillingProfileState(BotState botState) {
        switch (botState) {
            case FILL_RECORD:
            case ASK_FLAT:
            case ASK_CAR_MARK:
            case ASK_CAR_NUMBER:
            case ASK_PHONE_NUMBER:
            case RECORD_DATA_FILLED:
                return true;
            default:
                return false;
        }
    }
}
