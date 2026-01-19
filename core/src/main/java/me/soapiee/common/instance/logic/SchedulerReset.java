package me.soapiee.common.instance.logic;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SchedulerReset extends BukkitRunnable {

    private final MessageManager messageManager;
    private final Game game;
    private final Scheduler scheduler;

    public SchedulerReset(TFQuiz main, Scheduler scheduler, int delay) {
        this.messageManager = main.getMessageManager();
        this.game = scheduler.getGame();
        this.scheduler = scheduler;
        this.runTaskLater(main, delay * 20L);
    }

    @Override
    public void run() {
        if (!this.scheduler.isPlayed()) {
            this.game.reset(true, true);

            String message = Utils.addColour(this.messageManager.getWithPlaceholder(Message.GAMECLOSEDSCHEDULER, game.getIdentifier()));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }
        this.cancel();
    }
}
