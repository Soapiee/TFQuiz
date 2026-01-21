package me.soapiee.common.tasks;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.logic.Procedure;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundTimer extends BukkitRunnable {

    private final TFQuiz main;
    private final Game game;
    private final Procedure procedure;
    private final MessageManager messageManager;
    private int countdownSeconds;

    public RoundTimer(TFQuiz main, Game game, Procedure procedure, int countdownSeconds) {
        this.main = main;
        this.game = game;
        this.procedure = procedure;
        this.messageManager = main.getMessageManager();
        this.countdownSeconds = countdownSeconds;
    }

    public void start() {
        runTaskTimer(main, 0, 20L);
    }

    @Override
    public void run() {
        if (this.countdownSeconds == 0) {
            this.game.sendTitle("", "");
            this.procedure.revealOutcomeStage();
        }

        if (this.countdownSeconds == -5) {
            cancel();
            this.procedure.eliminateStage();
            return;
        }

        if (this.countdownSeconds > 0) {
            if (this.countdownSeconds <= 3) {
                this.game.sendMessage(this.messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWN, this.countdownSeconds));
            }
            this.game.sendTitle(this.messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLEPREFIX, this.countdownSeconds),
                    this.messageManager.getWithPlaceholder(Message.GAMEROUNDCOUNTDOWNTITLESUFFIX, this.countdownSeconds));
        }
//        DEBUG:
//        Utils.consoleMsg(ChatColor.DARK_PURPLE.toString() + this.countdownSeconds);

        this.countdownSeconds--;
    }
}
