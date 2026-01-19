package me.soapiee.common.enums;

import lombok.Getter;

public enum GameState {

    CLOSED("game_state_closed", "Not available"),
    RECRUITING("game_state_recruiting", "Waiting for players"),
    COUNTDOWN("game_state_countdown", "Starting soon"),
    LIVE("game_state_live", "Game is active");

    @Getter public final String path;
    private final String defaultDesc;

    GameState(String path, String defaultDesc) {
        this.path = path;
        this.defaultDesc = defaultDesc;
    }

    public String getDefault() {
        return defaultDesc;
    }

}
