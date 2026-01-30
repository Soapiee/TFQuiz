package me.soapiee.common.events;

import lombok.Getter;
import me.soapiee.common.enums.EndGameResult;
import me.soapiee.common.instance.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEndedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Game game;
    @Getter private final EndGameResult result;
    private boolean cancelled;

    public GameEndedEvent(Game game, EndGameResult result) {
        this.game = game;
        this.result = result;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = true;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
