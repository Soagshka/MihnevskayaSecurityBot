package ru.home.security_bot.cache;

import org.springframework.stereotype.Component;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.model.RecordData;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    Map<Integer, BotState> usersBotState = new HashMap<>();
    Map<Integer, RecordData> recordDataMap = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotState.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotState.get(userId);
        if (botState == null) {
            botState = BotState.FILL_RECORD;
        }

        return botState;
    }

    @Override
    public RecordData getRecordData(int userId) {
        RecordData recordData = recordDataMap.get(userId);
        if (recordData == null) {
            recordData = new RecordData();
        }

        return recordData;
    }

    @Override
    public void saveRecordData(int userId, RecordData recordData) {
        recordDataMap.put(userId, recordData);
    }


}
