package me.soapiee.common.handlers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.VersionManager;
import me.soapiee.common.enums.AddPlayerResult;
import me.soapiee.common.enums.DescriptionType;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.events.MinPlayerReachedEvent;
import me.soapiee.common.events.MinPlayerReducedEvent;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.InventoryManager;
import me.soapiee.common.managers.SettingsManager;
import me.soapiee.common.tasks.TeleportTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayerHandler {

    private final TFQuiz main;
    private final SettingsManager settingsManager;
    private final InventoryManager inventoryManager;
    private final VersionManager versionManager;
    private final GamePlayerManager gamePlayerManager;
    private final GameMessageHandler gameMessageHandler;
    private final ArenaHandler arenaHandler;

    private final Game game;
    private final int gameID, maxPlayers, minPlayers;
    private final boolean enforceSurvival;

    public GamePlayerHandler(TFQuiz main, Game game) {
        this.game = game;
        this.main = main;
        settingsManager = main.getSettingsManager();
        inventoryManager = main.getInventoryManager();
        versionManager = main.getVersionManager();
        gamePlayerManager = main.getGamePlayerManager();
        gameMessageHandler = game.getMessageHandler();
        arenaHandler = game.getArenaHandler();

        gameID = game.getIdentifier();
        maxPlayers = game.getMaxPlayers();
        minPlayers = game.getMinPlayers();
        enforceSurvival = settingsManager.isEnforceSurvival();
    }

    public AddPlayerResult addPlayer(UUID uuid) {
        if (!game.isOpen()) return AddPlayerResult.GAME_CLOSED;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return AddPlayerResult.PLAYER_NOT_FOUND;
        if (enforceSurvival) if (player.getGameMode() != GameMode.SURVIVAL) return AddPlayerResult.NOT_SURVIVAL;

        int playerCount = gamePlayerManager.getAllPlayers(gameID).size();
        if (playerCount >= maxPlayers) return AddPlayerResult.GAME_FULL;

        gamePlayerManager.addPlayer(game, uuid);
        gameMessageHandler.successfullyJoined(player);

        if (game.isPhysicalArena()) {
            teleportPlayer(player);
            inventoryManager.saveInventory(player);
        }

        if (arenaHandler.getDescType() != DescriptionType.HOLOGRAM) gameMessageHandler.sendGameDesc(player);

        GameState state = game.getState();
        playerCount = gamePlayerManager.getAllPlayers(gameID).size();

        if (state == GameState.RECRUITING && playerCount >= minPlayers) {
            MinPlayerReachedEvent event = new MinPlayerReachedEvent(game);
            Bukkit.getPluginManager().callEvent(event);
        } else game.updateSigns(); //so the sign isnt updated when the player joins AND when the game state changes

        return AddPlayerResult.SUCCESS;
    }

    private void teleportPlayer(Player player) {
        new TeleportTask(player, arenaHandler.getSpawn()).runTaskLater(main, 1);
    }

    public void removePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        gamePlayerManager.removePlayer(game, uuid);
        gameMessageHandler.cleanTitles(player);

        if (game.isOpen()) gameMessageHandler.otherPlayerLeft(player);

        GameState state = game.getState();
        if (state == GameState.COUNTDOWN && gamePlayerManager.getAllPlayers(gameID).size() < minPlayers) {
            MinPlayerReducedEvent event = new MinPlayerReducedEvent(game);
            Bukkit.getPluginManager().callEvent(event);
        }

        if (game.isPhysicalArena()) {
            removeSpectator(player);
            teleportToLobby(player);
            inventoryManager.restoreInventory(player);
        }

        game.updateSigns();
    }

    public void handlePlayerWhenReset(UUID uuid, boolean removedMessage) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        if (removedMessage) gameMessageHandler.removedFromGame(player);
        gameMessageHandler.cleanTitles(player);

        if (game.isPhysicalArena()) {
            removeSpectator(player);
            teleportToLobby(player);
            inventoryManager.restoreInventory(player);
        }
    }

    public void setSpectator(Player player) {
        UUID uuid = player.getUniqueId();
        if (!versionManager.setSpectator(player)) {
            removePlayer(uuid);
            gameMessageHandler.spectatorError(player);
            return;
        }

        gamePlayerManager.addSpectator(gameID, uuid);
        game.updateSigns();
    }

    public void removeSpectator(Player player) {
        UUID uuid = player.getUniqueId();
        if (!gamePlayerManager.isSpectator(gameID, uuid)) return;

        if (player.isOnline()) versionManager.unSetSpectator(player);
        gamePlayerManager.removeSpectator(gameID, uuid);
    }

    public void eliminate(UUID uuid) {
        gamePlayerManager.eliminatePlayer(gameID, uuid);
    }

    private void teleportToLobby(Player player) {
        new TeleportTask(player, settingsManager.getLobbySpawn()).runTaskLater(main, 1);
    }

}
