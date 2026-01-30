package me.soapiee.common.instance;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.handlers.ArenaHandler;
import me.soapiee.common.handlers.GameMessageHandler;
import me.soapiee.common.handlers.GamePlayerHandler;
import me.soapiee.common.handlers.LifeCycleHandler;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.GameSignManager;
import me.soapiee.common.managers.MessageManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Game {

    private final MessageManager messageManager;
    private final GamePlayerManager gamePlayerManager;
    private final GameSignManager gameSignManager;

    //Required options
    @Getter private GameState state;
    @Getter private final int identifier, maxPlayers, minPlayers, maxRounds, countdownSeconds;
    @Getter private final Reward reward;
    @Getter private final boolean physicalArena;
    @Getter private final boolean broadcastWinners;
    @Getter private final GameMessageHandler messageHandler;
    @Getter private final GamePlayerHandler playerHandler;
    @Getter final private LifeCycleHandler lifeCycleHandler;

    //Arena options
    @Getter private final ArenaHandler arenaHandler;

    //Non-arena options
    @Getter private int schedulerDelay, schedulerResetterDelay;

    public Game(TFQuiz main, Map<String, String> settings, Reward reward) {
        identifier = Integer.parseInt(settings.get("identifier"));
        messageManager = main.getMessageManager();
        gamePlayerManager = main.getGamePlayerManager();
        gameSignManager = main.getGameSignManager();
        arenaHandler = new ArenaHandler(messageManager);

        state = GameState.valueOf(settings.get("initial_state").toUpperCase());
        maxPlayers = Integer.parseInt(settings.get("max_players"));
        minPlayers = Integer.parseInt(settings.get("min_players"));
        maxRounds = Integer.parseInt(settings.get("max_rounds"));
        countdownSeconds = Integer.parseInt(settings.get("countdown_seconds"));
        this.reward = reward;
        physicalArena = Boolean.parseBoolean(settings.get("physical_arena"));
        broadcastWinners = Boolean.parseBoolean(settings.get("broadcast_winners"));

        messageHandler = new GameMessageHandler(main, this);
        playerHandler = new GamePlayerHandler(main, this);
        lifeCycleHandler = new LifeCycleHandler(main, this);
    }

    public void setUpNonArenaOptions(int schedulerDelay, int resetDelay) {
        this.schedulerDelay = physicalArena ? -1 : schedulerDelay;
        this.schedulerResetterDelay = physicalArena ? -1 : resetDelay;
    }

    public boolean isOpen() {
        return (state == GameState.RECRUITING || state == GameState.COUNTDOWN);
    }

    public String getStateDescription() {
        return messageManager.get(state);
    }

    public void setState(GameState state) {
        this.state = state;

        if (!getSigns().isEmpty())
            for (GameSign sign : getSigns()) sign.update(getStateDescription());
    }

    public void updateSigns() {
        if (!getSigns().isEmpty())
            for (GameSign sign : getSigns()) sign.update(gamePlayerManager.getPlayingPlayers(identifier).size());
    }

    public List<GameSign> getSigns() {
        return (gameSignManager.getSigns(this) == null) ? Collections.emptyList() : gameSignManager.getSigns(this);
    }

}