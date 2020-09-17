package ru.home.security_bot.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.home.security_bot.dao.RecordDataEntity;

@Repository
public interface RecordDataRepository extends JpaRepository<RecordDataEntity, Long> {
}
