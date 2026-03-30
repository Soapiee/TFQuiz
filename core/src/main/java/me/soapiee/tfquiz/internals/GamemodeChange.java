package me.soapiee.tfquiz.internals;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GamemodeChange extends BukkitRunnable {

    private final Player player;

    public GamemodeChange(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }
        player.setGameMode(GameMode.SURVIVAL);
        this.cancel();
    }
}
