package ru.home.security_bot.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.home.security_bot.dao.BotStateEntity;

@Repository
public interface BotStateRepository extends JpaRepository<BotStateEntity, Long> {
    BotStateEntity findByUserIdAndChatId(int userId, Long chatId);
}
