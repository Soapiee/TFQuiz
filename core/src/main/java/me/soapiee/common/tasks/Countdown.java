package me.soapiee.common.tasks;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private final TFQuiz main;
    private final Game game;
    private final MessageManager messageManager;
    @Getter private final int totalSeconds;
    private int countdownSeconds;

    public Countdown(TFQuiz main, Game game, int countdownSeconds) {
        this.main = main;
        this.game = game;
        this.messageManager = main.getMessageManager();

        this.totalSeconds = countdownSeconds;
        this.countdownSeconds = countdownSeconds + 1;
    }

    public void start() {
        this.game.setState(GameState.COUNTDOWN);
        runTaskTimer(this.main, 0, 20);
    }

    @Override
    public void run() {
        //DEBUG:
//        Utils.consoleMsg(ChatColor.DARK_PURPLE.toString() + this.countdownSeconds);
        this.countdownSeconds--;

        if (this.countdownSeconds == 0) {
            this.cancel();
            this.game.sendTitle("", "");
            this.game.start();
            return;
        }
        if (this.countdownSeconds <= 3 || this.countdownSeconds % 10 == 0) {
            this.game.sendMessage(this.messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNSTART, this.countdownSeconds));
            this.game.sendTitle(this.messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNTITLEPREFIX, this.countdownSeconds),
                    this.messageManager.getWithPlaceholder(Message.GAMECOUNTDOWNTITLESUFFIX, this.countdownSeconds));
        }
    }

    public int getSeconds() {
        return this.countdownSeconds;
    }
}
