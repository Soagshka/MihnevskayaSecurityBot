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
import ru.home.security_bot.util.BotStateUtil;

@Component
public class TelegramFacade {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TelegramFacade.class);
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;

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
            case "Последние 5 записей":
                botState = BotState.SHOW_5_LAST_RECORDS;
                break;
            default:
                botState = BotStateUtil.getBotState(message.getFrom().getId(), message.getChatId());
                break;
        }

        log.warn("SAVING BOT STATE = " + botState.getDescription());
        BotStateUtil.saveBotState(userId, message.getChatId(), botState);
        //userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");

        if (buttonQuery.getData().equals("UnknownMark")) {
            RecordData recordData = userDataCache.getRecordData(userId);
            BotState botState = BotStateUtil.getBotState(userId, chatId);
            if (BotStateUtil.isRestartNeeded(recordData, botState)) {
                botState = BotState.ASK_PHONE_NUMBER;
                BotStateUtil.saveBotState(userId, chatId, botState);
                return new SendMessage(chatId, "К сожалению время сессии закончилось, пройдите процедуру заново...  Введите номер квартиры : ");
            }
            recordData.setCarMark("Неизвестная марка");
            userDataCache.saveRecordData(userId, recordData);
            //userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_DATA_FILLED);
            BotStateUtil.saveBotState(userId, chatId, BotState.RECORD_DATA_FILLED);
            return new SendMessage(chatId, "Номер автомобиля большими буквами без пробелов :");
        }

        //userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        BotStateUtil.saveBotState(userId, chatId, BotState.SHOW_MAIN_MENU);
        return callBackAnswer;

    }
}
