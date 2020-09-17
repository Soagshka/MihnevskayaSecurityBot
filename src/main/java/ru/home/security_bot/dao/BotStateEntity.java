package ru.home.security_bot.dao;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "bot_state", schema = "security_bot", catalog = "securuty_bot_mh")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class BotStateEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;
    @NonNull
    private int userId;
    @NonNull
    private Long chatId;
    @NonNull
    private String botState;
}
