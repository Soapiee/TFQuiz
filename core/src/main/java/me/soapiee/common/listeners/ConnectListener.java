package me.soapiee.common.listeners;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.VersionManager;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GameManager;
import me.soapiee.common.managers.SettingsManager;
import me.soapiee.common.utils.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private final TFQuiz main;
    private final GameManager gameManager;
    private final SettingsManager settingsManager;
    private final PlayerCache playerCache;
    private final VersionManager specManager;

    public ConnectListener(TFQuiz main) {
        this.main = main;
        gameManager = main.getGameManager();
        settingsManager = main.getSettingsManager();
        playerCache = main.getPlayerCache();
        specManager = main.getVersionManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (settingsManager.isEnforceLobbySpawn()) player.teleport(settingsManager.getLobbySpawn());

        if (specManager.spectatorsExist()) specManager.updateTab(player);

        if (!player.hasPlayedBefore()) playerCache.addOfflinePlayer(player);

        if (player.hasPermission("tfquiz.admin.notification")) updateNotif(player);
    }

    private void updateNotif(Player player) {
        if (settingsManager.isUpdateNotif()) {
            Bukkit.getScheduler().runTaskLater(main, () -> {
                main.getUpdateManager().updateAlert(player);
            }, 15);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Game game = gameManager.getGame(player);
        if (game != null) {
            if (game.isSpectator(player)) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            if (game.isPhysicalArena()) {
                player.teleport(settingsManager.getLobbySpawn());
            }
            game.removePlayer(player);
        }
    }
}
