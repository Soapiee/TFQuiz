package me.soapiee.common.listeners;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.EndGameResult;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.events.*;
import me.soapiee.common.handlers.LifeCycleHandler;
import me.soapiee.common.handlers.LiveGameHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameEvents implements Listener {

    private final MessageManager messageManager;
    private final GamePlayerManager playerManager;

    public GameEvents(TFQuiz main) {
        messageManager = main.getMessageManager();
        playerManager = main.getGamePlayerManager();
    }

    @EventHandler
    public void onMinPlayersReached(MinPlayerReachedEvent event) {
        Game game = event.getGame();
        startCountdown(game);
    }

    @EventHandler
    public void onMinPlayersReduced(MinPlayerReducedEvent event) {
        Game game = event.getGame();
        cancelCountdown(game);
    }

    @EventHandler
    public void onCountdownEnd(CountdownEndedEvent event) {
        Game game = event.getGame();
        startGame(game);
    }

    @EventHandler
    public void onGameEnd(GameEndedEvent event) {
        Game game = event.getGame();
        EndGameResult result = event.getResult();

        game.getMessageHandler().announceWinners();
        game.getLifeCycleHandler().reset(kickPlayers(game.getIdentifier()), false);

        if (result == EndGameResult.WINNERS_END) giveRewards(game);
    }

    @EventHandler
    public void onRoundEnd(RoundEndedEvent event) {
        LiveGameHandler liveGameHandler = event.getLiveGameHandler();
        liveGameHandler.eliminatePlayers();

        EndGameResult result = liveGameHandler.shouldEnd();

        if (result == EndGameResult.NEW_ROUND) liveGameHandler.startNewRound();
        else {
            GameEndedEvent gameEndedEvent = new GameEndedEvent(event.getGame(), result);
            Bukkit.getPluginManager().callEvent(gameEndedEvent);
        }
    }

    private void startCountdown(Game game) {
        game.getLifeCycleHandler().getCountdown().start();
    }

    private void cancelCountdown(Game game) {
        LifeCycleHandler lifeCycleHandler = game.getLifeCycleHandler();
        if (lifeCycleHandler.isForceStart() && !playerManager.getAllPlayers(game.getIdentifier()).isEmpty()) {
            game.updateSigns();
            return;
        }

        game.getMessageHandler().sendMessageToAll(messageManager.get(Message.GAMENOTENOUGH));

        lifeCycleHandler.resetCountdown();
        game.setState(GameState.RECRUITING);
    }

    private void startGame(Game game) {
        game.getMessageHandler().sendTitleToAll("", "");
        game.getLifeCycleHandler().startGame();
    }

    private boolean kickPlayers(int gameID) {
        return !playerManager.getAllPlayers(gameID).isEmpty();
    }

    private void giveRewards(Game game) {
        int gameID = game.getIdentifier();
        int size = playerManager.getPlayingPlayers(gameID).size();
        Reward reward = game.getReward();
        Set<UUID> players = new HashSet<>();

        if (size >= 1) players.addAll(playerManager.getPlayingPlayers(gameID));

        for (UUID uuid : players) {
            reward.give(Bukkit.getPlayer(uuid));
        }
    }
}
