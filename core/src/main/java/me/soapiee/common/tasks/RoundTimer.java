package me.soapiee.common.tasks;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.logic.GameLifecycle;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundTimer extends BukkitRunnable {

    private final TFQuiz main;
    private final Game game;
    private final GameLifecycle gameLifecycle;
    private final MessageManager messageManager;
    private int countdownSeconds;

    public RoundTimer(TFQuiz main, Game game, GameLifecycle gameLifecycle, int countdownSeconds) {
        this.main = main;
        this.game = game;
        this.gameLifecycle = gameLifecycle;
        messageManager = main.getMessageManager();
        this.countdownSeconds = countdownSeconds;
    }

    public void start() {
        runTaskTimer(main, 0, 20L);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            game.sendTitle("", "");
            gameLifecycle.revealOutcomeStage();
        }

        if (countdownSeconds == -5) {
            cancel();
            gameLifecycle.eliminateStage();
            return;
        }

        if (countdownSeconds > 0) {
            if (countdownSeconds <= 3) {
                game.sendMessage(messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWN, countdownSeconds));
            }
            game.sendTitle(messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLEPREFIX, countdownSeconds),
                    messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLESUFFIX, countdownSeconds));
        }
//        DEBUG:
//        Utils.consoleMsg(ChatColor.DARK_PURPLE.toString() + this.countdownSeconds);

        this.countdownSeconds--;
    }
}
