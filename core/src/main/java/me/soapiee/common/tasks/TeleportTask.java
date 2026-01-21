package me.soapiee.common.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final Player player;
    private final Location loc;

    public TeleportTask(Player player, Location loc) {
        this.player = player;
        this.loc = loc;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }
        Bukkit.getPlayer(player.getUniqueId()).teleport(loc);
        this.cancel();
    }
}
