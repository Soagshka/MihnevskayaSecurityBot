package ru.home.security_bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.home.security_bot.botapi.BotState;
import ru.home.security_bot.dao.BotStateEntity;

@Component
@Mapper
public interface BotStateMapper {
    BotStateMapper BOT_STATE_MAPPER = Mappers.getMapper(BotStateMapper.class);

    BotStateEntity botStateToBotStateEntity(BotState botState);

    BotState botStateEntityToBotState(BotStateEntity botStateEntity);
}
