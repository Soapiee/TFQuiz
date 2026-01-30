package me.soapiee.common.tasks;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SchedulerReset extends BukkitRunnable {

    private final MessageManager messageManager;
    private final Game game;
    private final Scheduler scheduler;

    public SchedulerReset(TFQuiz main, Scheduler scheduler, int delay) {
        messageManager = main.getMessageManager();
        game = scheduler.getGame();
        this.scheduler = scheduler;
        runTaskLater(main, delay * 20L);
    }

    @Override
    public void run() {
        if (!scheduler.isPlayed()) {
            game.getLifeCycleHandler().reset(true, true);

            String message = Utils.addColour(messageManager.getWithPlaceholder(Message.GAMECLOSEDSCHEDULER, game.getIdentifier()));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }

        cancel();
    }
}
