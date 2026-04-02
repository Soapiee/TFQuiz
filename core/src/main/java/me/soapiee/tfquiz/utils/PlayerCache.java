package me.soapiee.tfquiz.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayerCache {

    private final Set<OfflinePlayer> offlinePlayers;

    public PlayerCache() {
        offlinePlayers = new HashSet<>();
        offlinePlayers.addAll(Arrays.asList(Bukkit.getServer().getOfflinePlayers()));
    }

    public void addOfflinePlayer(OfflinePlayer offlinePlayer) {
        offlinePlayers.add(offlinePlayer);
    }

    public Set<OfflinePlayer> getList() {
        return offlinePlayers;
    }
}
