package ru.home.security_bot.botapi;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SecurityTelegramBot extends TelegramWebhookBot {
    private String botToken;
    private String botUserName;
    private String webHookPath;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            try {
                execute(new SendMessage(chatId, "Hi " + update.getMessage().getText()));
            } catch (TelegramApiException exception) {

            }
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }
}
