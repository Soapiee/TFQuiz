package me.soapiee.common.instance.logic;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class Scheduler extends BukkitRunnable {

    private final TFQuiz main;
    @Getter private final Game game;
    private final int resetDelay;
    private SchedulerReset resetter;
    private final long endTime;

    @Getter private boolean played;

    public Scheduler(TFQuiz main, Game game) {
        this.main = main;
        this.game = game;
        this.resetDelay = game.getSchedulerResetterDelay();
        this.played = false;
        int delay = game.getSchedulerDelay();
        this.endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
        this.runTaskLater(main, delay * 20L);
    }

    @Override
    public void run() {
        if (game.getState() != GameState.RECRUITING) game.setState(GameState.RECRUITING);

        String message = main.getMessageManager().getWithPlaceholder(Message.GAMEOPENEDSCHEDULER, game.getIdentifier());
        TextComponent clickableText = new TextComponent(Utils.addColour(message));
        clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/game join " + game.getIdentifier()));
        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.addColour(main.getMessageManager().get(Message.GAMESOPENSCHEDULERHOVER)))));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(clickableText);
        }

        resetter = new SchedulerReset(main, this, resetDelay);
        cancel();
    }

    public void setPlayed() {
        played = true;
        if (this.resetter != null) {
            try {
                resetter.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public int getRemainingTime() {
        long remaining = endTime - System.currentTimeMillis();
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(remaining);

        int result = remainingSeconds;

        if (remainingSeconds < 0) {
            result = resetDelay + remainingSeconds;
        }

        return result;
    }
}