package ru.home.security_bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.home.security_bot.botapi.SecurityTelegramBot;
import ru.home.security_bot.botapi.TelegramFacade;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public SecurityTelegramBot securityTelegramBot(TelegramFacade telegramFacade) {

        SecurityTelegramBot securityTelegramBot = new SecurityTelegramBot(telegramFacade);
        securityTelegramBot.setBotUserName(botUserName);
        securityTelegramBot.setBotToken(botToken);
        securityTelegramBot.setWebHookPath(webHookPath);

        return securityTelegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
