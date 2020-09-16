package ru.home.security_bot.botapi;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.security_bot.cache.UserDataCache;
import ru.home.security_bot.model.RecordData;
import ru.home.security_bot.service.MainMenuService;

@Component
public class TelegramFacade {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TelegramFacade.class);
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.SHOW_MAIN_MENU;
                break;
            case "Заполнить данные для пропуска":
                botState = BotState.FILL_RECORD;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");

        if (buttonQuery.getData().equals("UnknownMark")) {
            RecordData recordData = userDataCache.getRecordData(userId);
            recordData.setCarMark("Неизвестная марка");
            userDataCache.saveRecordData(userId, recordData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CAR_NUMBER);
            return new SendMessage(chatId, "Номер автомобиля :");
        }

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        return callBackAnswer;

    }
}
