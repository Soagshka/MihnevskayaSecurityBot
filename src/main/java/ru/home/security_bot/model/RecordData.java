package ru.home.security_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordData {
    int flatNumber;
    String phoneNumber;
    String carMark;
    String carNumber;
}
