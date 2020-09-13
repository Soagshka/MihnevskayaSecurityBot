package ru.home.security_bot.cache;

import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.model.RecordData;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    RecordData getRecordData(int userId);

    void saveRecordData(int userId, RecordData recordData);
}
