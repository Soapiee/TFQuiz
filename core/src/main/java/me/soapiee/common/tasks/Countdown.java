package me.soapiee.common.tasks;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.events.CountdownEndedEvent;
import me.soapiee.common.handlers.GameMessageHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private final TFQuiz main;
    private final Game game;
    private final MessageManager messageManager;
    private final GameMessageHandler gameMessageHandler;
    @Getter private final int totalSeconds;
    @Getter private int countdownSeconds;

    public Countdown(TFQuiz main, Game game, int countdownSeconds) {
        this.main = main;
        this.game = game;
        messageManager = main.getMessageManager();
        gameMessageHandler = game.getMessageHandler();

        totalSeconds = countdownSeconds;
        this.countdownSeconds = countdownSeconds + 1;
    }

    public void start() {
        game.setState(GameState.COUNTDOWN);
        runTaskTimer(main, 0, 20);
    }

    @Override
    public void run() {
        countdownSeconds--;

        if (countdownSeconds == 0) {
            cancel();
            Bukkit.getPluginManager().callEvent(new CountdownEndedEvent(game));
            return;
        }

        if (countdownSeconds <= 3 || countdownSeconds % 10 == 0) {
            gameMessageHandler.sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNSTART, countdownSeconds));
            gameMessageHandler.sendTitleToAll(
                    messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNTITLEPREFIX, countdownSeconds),
                    messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNTITLESUFFIX, countdownSeconds));
        }
    }
}
