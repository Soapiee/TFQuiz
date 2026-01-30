package me.soapiee.common;

import lombok.Getter;
import me.soapiee.common.command.AdminCommand;
import me.soapiee.common.command.PlayerCommand;
import me.soapiee.common.enums.Message;
import me.soapiee.common.handlers.ArenaHandler;
import me.soapiee.common.hooks.PlaceHolderAPIHook;
import me.soapiee.common.hooks.VaultHook;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.GameSign;
import me.soapiee.common.listeners.ChatListener;
import me.soapiee.common.listeners.ConnectListener;
import me.soapiee.common.listeners.GameEvents;
import me.soapiee.common.listeners.PlayerListener;
import me.soapiee.common.managers.*;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.PlayerCache;
import me.soapiee.common.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class TFQuiz extends JavaPlugin {

    //TODO: Add "addGame" + "deleteGame" command functionality
    //TODO: Add question categories
    //TODO: Make "True or False" in the question, clickable
    //TODO: Add a instant confirmation flag to /tf reload cmd
    //TODO: Let users decide the command label
    //TODO:

    @Getter private MessageManager messageManager;
    @Getter private VersionManager versionManager;
    @Getter private PlayerCache playerCache;
    @Getter private GamePlayerManager gamePlayerManager;
    @Getter private GameManager gameManager;
    @Getter private SettingsManager settingsManager;
    @Getter private QuestionManager questionManager;
    @Getter private GameSignManager gameSignManager;
    @Getter private SchedulerManager schedulerManager;
    @Getter private UpdateManager updateManager;
    @Getter private PlayerListener playerListener;
    @Getter private InventoryManager inventoryManager;
    private VaultHook vaultHook;
    @Getter private CustomLogger customLogger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messageManager = new MessageManager(this);
        customLogger = new CustomLogger(this);
        settingsManager = new SettingsManager(this);
        versionManager = new VersionManager(this);

        registerHooks();
        new Metrics(this, 25563);

        initiateManagers();
        playerCache = new PlayerCache();

        playerListener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameEvents(this), this);

        getCommand("tf").setExecutor(new AdminCommand(this));
        getCommand("game").setExecutor(new PlayerCommand(this));

        updateManager = new UpdateManager(this, 125077);
        updateManager.updateAlert(Bukkit.getConsoleSender());
    }

    @Override
    public void onDisable() {
        if (gameManager == null) return;

        for (Game game : gameManager.getGames()) {
            for (UUID uuid : gamePlayerManager.getAllPlayers(game.getIdentifier())) removePlayerFromGame(game, uuid);

            ArenaHandler arenaHandler = game.getArenaHandler();
            if (arenaHandler.getHologram() != null) arenaHandler.getHologram().despawn();
            killOtherHolos(game);

            if (!game.getSigns().isEmpty()) for (GameSign sign : game.getSigns()) sign.despawn();
        }
    }

    private void removePlayerFromGame(Game game, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        if (gamePlayerManager.isSpectator(game.getIdentifier(), uuid)) player.setGameMode(GameMode.SURVIVAL);
        inventoryManager.restoreInventory(player);
        if (game.isPhysicalArena()) player.teleport(settingsManager.getLobbySpawn());
    }

    private void initiateManagers() {
        questionManager = new QuestionManager(this);
        inventoryManager = new InventoryManager(this);
        gamePlayerManager = new GamePlayerManager();
        gameManager = new GameManager(this);
        gameSignManager = new GameSignManager(this, gameManager);
        schedulerManager = new SchedulerManager(this);

        gameManager.load(null);
        gameSignManager.load(Bukkit.getConsoleSender());
        schedulerManager.startSchedulers();
    }

    private void registerHooks() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPIHook(this).register();
            Utils.consoleMsg(messageManager.get(Message.HOOKEDPLACEHOLDERAPI));
        }

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHook = new VaultHook(this);
            Utils.consoleMsg(messageManager.get(Message.HOOKEDVAULT));
        } else {
            vaultHook = null;
            Utils.consoleMsg(messageManager.get(Message.HOOKEDVAULTERROR));
        }
    }

    public VaultHook getVaultHook() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return null;
        else return vaultHook;
    }

    private void killOtherHolos(Game game) {
        ArenaHandler arenaHandler = game.getArenaHandler();
        if (arenaHandler.getSpawn() != null) {
            for (Entity entity : arenaHandler.getSpawn().getWorld().getEntities()) {
                if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE)) {
                    entity.remove();
                }
            }
        }
    }
}
