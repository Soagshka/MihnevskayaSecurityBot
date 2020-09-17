package ru.home.security_bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.home.security_bot.dao.RecordDataEntity;
import ru.home.security_bot.model.RecordData;

@Component
@Mapper
public interface RecordDataMapper {
    RecordDataMapper RECORD_DATA_MAPPER = Mappers.getMapper(RecordDataMapper.class);

    RecordDataEntity recordDataToRecordEntity(RecordData recordData);

    RecordData recordDataEntityToRecordData(RecordDataEntity recordDataEntity);
}
