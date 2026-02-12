package me.soapiee.common.versionsupport;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class NMS_Unsupported implements NMSProvider {
    @Override
    public boolean setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        return true;
    }

    @Override
    public void unSetSpectator(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void updateTab(Player player, Set<UUID> spectators) {

    }
}
