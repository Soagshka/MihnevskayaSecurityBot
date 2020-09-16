package ru.home.security_bot.botapi.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.botapi.InputMessageHandler;
import ru.home.security_bot.service.MainMenuService;
import ru.home.security_bot.service.ReplyMessageService;

@Component
public class MainMenuHandler implements InputMessageHandler {
    private ReplyMessageService messagesService;
    private MainMenuService mainMenuService;

    public MainMenuHandler(ReplyMessageService messagesService, MainMenuService mainMenuService) {
        this.messagesService = messagesService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.showMainMenu"));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }
}
