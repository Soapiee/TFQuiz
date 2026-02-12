package me.soapiee.common.events;

import lombok.Getter;
import me.soapiee.common.handlers.LiveGameHandler;
import me.soapiee.common.instance.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RoundEndedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Game game;
    @Getter private final LiveGameHandler liveGameHandler;
    private boolean cancelled;

    public RoundEndedEvent(Game game) {
        this.game = game;
        liveGameHandler = game.getLifeCycleHandler().getLiveGameHandler();
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
