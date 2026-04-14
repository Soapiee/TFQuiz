package me.soapiee.tfquiz.listeners;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.instance.Game;
import me.soapiee.tfquiz.managers.GameManager;
import me.soapiee.tfquiz.managers.GamePlayerManager;
import me.soapiee.tfquiz.managers.SettingsManager;
import me.soapiee.tfquiz.utils.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectListener implements Listener {

    private final TFQuiz main;
    private final GamePlayerManager gamePlayerManager;
    private final GameManager gameManager;
    private final SettingsManager settingsManager;
    private final PlayerCache playerCache;

    public ConnectListener(TFQuiz main) {
        this.main = main;
        gamePlayerManager = main.getGamePlayerManager();
        gameManager = main.getGameManager();
        settingsManager = main.getSettingsManager();
        playerCache = main.getPlayerCache();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (settingsManager.isEnforceLobbySpawn()) player.teleport(settingsManager.getLobbySpawn());

        if (gamePlayerManager.spectatorsExist()) gamePlayerManager.updateTab(main, player);

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
        UUID uuid = player.getUniqueId();

        Game game = gameManager.getGame(uuid);
        if (game != null) {
            if (gamePlayerManager.isSpectator(game.getIdentifier(), uuid)) player.setGameMode(GameMode.SURVIVAL);

            if (game.isPhysicalArena()) player.teleport(settingsManager.getLobbySpawn());

            game.getPlayerHandler().removePlayer(uuid);
        }
    }
}
