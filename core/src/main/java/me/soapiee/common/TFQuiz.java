package me.soapiee.common;

import lombok.Getter;
import me.soapiee.common.command.AdminCommand;
import me.soapiee.common.command.PlayerCommand;
import me.soapiee.common.enums.Message;
import me.soapiee.common.hooks.PlaceHolderAPIHook;
import me.soapiee.common.hooks.VaultHook;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.listener.ChatListener;
import me.soapiee.common.listener.ConnectListener;
import me.soapiee.common.listener.PlayerListener;
import me.soapiee.common.manager.*;
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
    @Getter private GameManager gameManager;
    @Getter private SettingsManager settingsManager;
    @Getter private QuestionManager questionManager;
    @Getter private GameSignManager gameSignManager;
    @Getter private SchedulerManager schedulerManager;
    @Getter private UpdateManager updateManager;
    @Getter private PlayerListener playerListener;
    private InventoryManager inventoryManager;
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

        getCommand("tf").setExecutor(new AdminCommand(this));
        getCommand("game").setExecutor(new PlayerCommand(this));

        updateManager = new UpdateManager(this, 125077);
        updateManager.updateAlert(Bukkit.getConsoleSender());
    }

    @Override
    public void onDisable() {
        if (gameManager == null) return;

        for (Game game : gameManager.getGames()) {
            for (Player player : game.getAllPlayers()) {
                if (game.isSpectator(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                }
                game.restoreInventory(player);

                if (game.isPhysicalArena()) player.teleport(settingsManager.getLobbySpawn());
            }
            if (game.getHologram() != null) game.getHologram().despawn();

            killOtherHolos(game);

            if (game.getSigns() == null) continue;
            for (GameSign sign : game.getSigns()) sign.despawn();
        }
    }

    private void initiateManagers() {
        questionManager = new QuestionManager(this);
        if (settingsManager.isSaveInvs()) inventoryManager = new InventoryManager(this);
        else inventoryManager = null;
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

    public InventoryManager getInventoryManager() {
        if (!settingsManager.isSaveInvs()) return null;
        return inventoryManager;
    }

    private void killOtherHolos(Game game) {
        if (game.getSpawn() != null) {
            for (Entity entity : game.getSpawn().getWorld().getEntities()) {
                if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE)) {
                    entity.remove();
                }
            }
        }
    }
}
