package ru.home.security_bot.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "record_data", schema = "security_bot", catalog = "securuty_bot_mh")
@Getter
@Setter
@NoArgsConstructor
public class RecordDataEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;
    private int userId;
    private Long chatId;
    private int flatNumber;
    private String phoneNumber;
    private String carMark;
    private String carNumber;
    private Timestamp recordDate;
}
