package ru.home.security_bot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.security_bot.botapi.SecurityTelegramBot;

public class WebHookController {
    private final SecurityTelegramBot securityTelegramBot;

    public WebHookController(SecurityTelegramBot securityTelegramBot) {
        this.securityTelegramBot = securityTelegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return securityTelegramBot.onWebhookUpdateReceived(update);
    }

//    @GetMapping(value = "/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<UserTicketsSubscriptions> index() {
//        return null;
//    }
}
