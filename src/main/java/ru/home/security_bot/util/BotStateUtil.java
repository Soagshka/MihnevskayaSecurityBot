package ru.home.security_bot.util;

import org.springframework.stereotype.Component;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.dao.BotStateEntity;
import ru.home.security_bot.dao.repository.BotStateRepository;

@Component
public class BotStateUtil {
    private static BotStateRepository botStateRepository;

    public BotStateUtil(BotStateRepository botStateRepository) {
        BotStateUtil.botStateRepository = botStateRepository;
    }

    public static void saveBotState(int userId, Long chatId, BotState botState) {
        BotStateEntity botStateEntity = botStateRepository.findByUserIdAndChatId(userId, chatId);
        if (botStateEntity != null) {
            botStateEntity.setBotState(botState.getDescription());
            botStateRepository.save(botStateEntity);
        } else {
            botStateRepository.save(new BotStateEntity(userId, chatId, botState.getDescription()));
        }
    }

    public static BotState getBotState(int userId, Long chatId) {
        BotStateEntity botStateEntity = botStateRepository.findByUserIdAndChatId(userId, chatId);
        if (botStateEntity == null) {
            return BotState.FILL_RECORD;
        }
        return BotState.valueOf(botStateEntity.getBotState());
    }
}
