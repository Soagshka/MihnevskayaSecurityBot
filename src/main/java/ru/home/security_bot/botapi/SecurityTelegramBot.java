package ru.home.security_bot.botapi;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SecurityTelegramBot extends TelegramWebhookBot {
    private static final String TOKEN = "1350420034:AAGngYk1W6Q3eBfAFi6R8CXVlQUNZ3K7ztM";
    private static final String USERNAME = "MihnevskayaSecurityBot";

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
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
