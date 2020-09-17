package ru.home.security_bot.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bot_state", schema = "security_bot", catalog = "securuty_bot_mh")
@Getter
@Setter
@NoArgsConstructor
public class BotStateEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;
    private Long userId;
    private String botState;
}
