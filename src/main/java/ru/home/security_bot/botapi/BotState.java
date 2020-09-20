package ru.home.security_bot.botapi;

public enum BotState {
    SHOW_MAIN_MENU("SHOW_MAIN_MENU"),
    FILL_RECORD("FILL_RECORD"),
    ASK_FLAT("ASK_FLAT"),
    ASK_PHONE_NUMBER("ASK_PHONE_NUMBER"),
    ASK_CAR_MARK("ASK_CAR_MARK"),
    ASK_CAR_NUMBER("ASK_CAR_NUMBER"),
    RECORD_DATA_FILLED("RECORD_DATA_FILLED"),
    SHOW_5_LAST_RECORDS("SHOW_5_LAST_RECORDS"),
    SHOW_HELP("SHOW_HELP");

    private final String description;

    BotState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
