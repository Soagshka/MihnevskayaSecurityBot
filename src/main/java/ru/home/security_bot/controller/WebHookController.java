package ru.home.security_bot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.security_bot.botapi.SecurityTelegramBot;

@RestController
public class WebHookController {
    private final SecurityTelegramBot securityTelegramBot;

    public WebHookController(SecurityTelegramBot securityTelegramBot) {
        this.securityTelegramBot = securityTelegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return securityTelegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping(value = "/getsmth", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getsmth() {
        return 1;
    }
}
