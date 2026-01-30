package me.soapiee.common.factories;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.DescriptionType;
import me.soapiee.common.enums.Message;
import me.soapiee.common.handlers.ArenaHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.Hologram;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class GameFactory {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final RewardFactory rewardFactory;
    private FileConfiguration config;

    public GameFactory(TFQuiz main) {
        this.main = main;
        config = main.getConfig();
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        rewardFactory = new RewardFactory(main);
    }

    public void reload() {
        config = main.getConfig();
        rewardFactory.reload();
    }

    public Game create(CommandSender sender, String configID) {
        HashMap<String, String> settingsMap = new HashMap<>();
        String path = "games." + configID + ".";
        settingsMap.put("identifier", configID);
        settingsMap.put("initial_state", config.getString(path + "state_on_startup", "CLOSED"));
        settingsMap.put("max_players", config.getString(path + "maximum_players", "5"));
        settingsMap.put("min_players", config.getString(path + "minimum_players", "1"));
        settingsMap.put("max_rounds", config.getString(path + "maximum_rounds", "10"));
        settingsMap.put("physical_arena", config.getString(path + "arena", "false"));
        settingsMap.put("broadcast_winners", config.getString(path + "broadcast_winners", "true"));
        settingsMap.put("countdown_seconds", config.getString(path + "countdown_seconds", "10"));

        Reward reward = rewardFactory.create(sender, configID);
        Game game = new Game(main, settingsMap, reward);
        main.getGamePlayerManager().addGame(game.getIdentifier());

        boolean hasArena = Boolean.parseBoolean(settingsMap.get("physical_arena"));
        if (hasArena) setupArenaHandler(sender, configID, game);
        createNonArenaOptions(configID, game, hasArena);

        return game;
    }

    private void setupArenaHandler(CommandSender sender, String configID, Game game) {
        ArenaHandler arenaHandler = game.getArenaHandler();
        arenaHandler.setDescType(validateDescriptionType(configID));
        arenaHandler.setAllowSpectators(config.getBoolean("games." + configID + ".arena_options.spectators", false));
        arenaHandler.setSpawn(validateSpawn(sender, configID));
        Hologram hologram = arenaHandler.getHologram();
        hologram.setLocation(validateHologramSpawn(configID));
        hologram.spawn();
    }

    private void createNonArenaOptions(String configID, Game game, boolean hasArena) {
        if (hasArena) {
            game.setUpNonArenaOptions(-1, -1);
            return;
        }

        int delay = config.getInt("games." + configID + ".schedule_delay", -1);
        int resetDelay = config.getInt("games." + configID + ".schedule_resets", delay * 2);
        game.setUpNonArenaOptions(delay, resetDelay);
    }

    private Location validateSpawn(CommandSender sender, String configID) {
        Location spawnLocation;

        String path = "games." + configID + ".arena_options.spawn_point";
        spawnLocation = validateLocation(path);

        if (spawnLocation == null)
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDGAMESPAWN, configID));

        return spawnLocation;
    }

    private DescriptionType validateDescriptionType(String configID) {
        String configString = config.getString("games." + configID + ".arena_options.desc_option", "chat");
        DescriptionType descType;

        try {
            descType = DescriptionType.valueOf(configString.toUpperCase());
        } catch (IllegalArgumentException error) {
            descType = DescriptionType.CHAT;
        }

        return descType;
    }

    private Location validateHologramSpawn(String configID) {
        return validateLocation("games." + configID + ".arena_options.holo_location");
    }

    private Location validateLocation(String path) {
        Location location;

        if (config.isConfigurationSection(path)) {
            String world = config.getString(path + ".world", null);
            String x = config.getString(path + ".x", null);
            String y = config.getString(path + ".y", null);
            String z = config.getString(path + ".z", null);
            String yaw = config.getString(path + ".yaw", null);
            String pitch = config.getString(path + ".pitch", null);

            if (world == null || world.equalsIgnoreCase("null")
                    || (x == null) || x.equalsIgnoreCase("null")
                    || (y == null) || y.equalsIgnoreCase("null")
                    || (z == null) || z.equalsIgnoreCase("null")) {
                return null;
            }

            location = new Location(
                    Bukkit.getWorld(world.replace("'", "")),
                    Double.parseDouble(x.replace("'", "")),
                    Double.parseDouble(y.replace("'", "")),
                    Double.parseDouble(z.replace("'", "")));

            if (yaw != null) {
                location.setYaw(Float.parseFloat(yaw.replace("'", "")));
                location.setPitch(Float.parseFloat(pitch.replace("'", "")));
            }

        } else {
            config.set(path + ".world", "null");
            config.set(path + ".x", "null");
            config.set(path + ".y", "null");
            config.set(path + ".z", "null");

            if (path.contains("spawn_point")) {
                config.set(path + ".yaw", "null");
                config.set(path + ".pitch", "null");
            }

            main.saveConfig();
            location = null;
        }

        return location;
    }
}
