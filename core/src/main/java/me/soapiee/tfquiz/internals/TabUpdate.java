package me.soapiee.tfquiz.internals;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

public class TabUpdate extends BukkitRunnable {

    private final SpectatorHandler spectatorHandler;
    private final Player player;
    private final Set<UUID> spectators;

    public TabUpdate(SpectatorHandler spectatorHandler, Player player, Set<UUID> spectators) {
        this.spectatorHandler = spectatorHandler;
        this.player = player;
        this.spectators = spectators;
    }

    @Override
    public void run() {
        spectatorHandler.updateTab(player, spectators);
        this.cancel();
    }
}
