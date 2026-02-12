package me.soapiee.common.tasks;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.events.RoundEndedEvent;
import me.soapiee.common.handlers.LiveGameHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundTimer extends BukkitRunnable {

    private final TFQuiz main;
    private final Game game;
    private final LiveGameHandler liveGameHandler;
    private final MessageManager messageManager;
    private int countdownSeconds;

    public RoundTimer(TFQuiz main, Game game, LiveGameHandler liveGameHandler, int countdownSeconds) {
        this.main = main;
        this.game = game;
        this.liveGameHandler = liveGameHandler;
        messageManager = main.getMessageManager();
        this.countdownSeconds = countdownSeconds;
    }

    public void start() {
        runTaskTimer(main, 0, 20L);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            game.getMessageHandler().sendTitleToAll("", "");
            liveGameHandler.revealOutcome();
        }

        if (countdownSeconds == -5) {
            cancel();
            Bukkit.getPluginManager().callEvent(new RoundEndedEvent(game));
            return;
        }

        if (countdownSeconds > 0) {
            if (countdownSeconds <= 3) {
                game.getMessageHandler().sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWN, countdownSeconds));
            }
            game.getMessageHandler().sendTitleToAll(
                    messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLEPREFIX, countdownSeconds),
                    messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLESUFFIX, countdownSeconds));
        }

        countdownSeconds--;
    }
}
