package me.soapiee.common.handlers;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.SchedulerManager;
import me.soapiee.common.tasks.Countdown;

import java.util.UUID;

public class LifeCycleHandler {

    private final TFQuiz main;
    private final SchedulerManager schedulerManager;
    private final GamePlayerManager playerManager;

    private final Game game;
    private final int gameID, countdownSeconds;
    private final ArenaHandler arenaHandler;
    private final GamePlayerHandler playerHandler;
    @Getter private LiveGameHandler liveGameHandler;
    @Getter private Countdown countdown;
    @Setter @Getter private boolean forceStart;

    public LifeCycleHandler(TFQuiz main, Game game) {
        this.main = main;
        this.game = game;
        schedulerManager = main.getSchedulerManager();
        playerManager = main.getGamePlayerManager();

        gameID = game.getIdentifier();
        countdownSeconds = game.getCountdownSeconds();
        arenaHandler = game.getArenaHandler();
        playerHandler = game.getPlayerHandler();
        liveGameHandler = new LiveGameHandler(main, game);
        countdown = new Countdown(main, game, countdownSeconds);
        forceStart = false;
    }

    public void startGame() {
        arenaHandler.despawnHologram();
        if (schedulerManager.getScheduler(gameID) != null) schedulerManager.getScheduler(gameID).setPlayed();

        liveGameHandler.generateQuestions();
        game.setState(GameState.LIVE);
        game.getMessageHandler().gameStarted();

        liveGameHandler.startNewRound();
    }

    public void endGame() {
        liveGameHandler.setCommandEnd();
    }

    public void reset(boolean kickPlayers, boolean removedMessage) {
        if (kickPlayers) {
            for (UUID uuid : playerManager.getAllPlayers(gameID))
                playerHandler.handlePlayerWhenReset(uuid, removedMessage);

            playerManager.resetPlayers(gameID);
        }

        forceStart = false;
        arenaHandler.despawnHologram();
        resetCountdown();
        resetScheduler();
        resetLifecycle();
        arenaHandler.spawnHologram();
    }

    public void resetCountdown() {
        if (countdown != null) {
            try {
                countdown.cancel();
            } catch (IllegalStateException ignored) {
            }
        }

        countdown = new Countdown(main, game, countdownSeconds);
    }

    private void resetLifecycle() {
        liveGameHandler.cancelTimer();
        liveGameHandler.unregister();
        liveGameHandler = new LiveGameHandler(main, game);
    }

    private void resetScheduler() {
        if (schedulerManager.getScheduler(gameID) != null) {
            schedulerManager.newScheduler(game);
            game.setState(GameState.CLOSED);
        } else game.setState(GameState.RECRUITING);
    }
}
