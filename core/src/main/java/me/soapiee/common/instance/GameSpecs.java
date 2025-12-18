package me.soapiee.common.instance;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameSpecs {

    private final TFQuiz main;

    private final int id;
    private final int maxPlayers;
    private final int minPlayers;
    private final int countdownSeconds;
    private final int maxRounds;
    private final GameState initialState;
    private final Reward reward;
    private final boolean physicalArena;
    private final boolean enforceSurvival;
    private final boolean broadcastWinners;

    //Non-Arena options
    private int schedulerDelay;
    private int schedulerResetterDelay;

    //Arena options
    private final boolean allowSpectators;
    private final String descType;
    private Location spawn;
    private Location holoLocation;


    public GameSpecs(TFQuiz main, CommandSender sender, String id) throws IllegalArgumentException {
        this.main = main;
        try {
            this.id = Integer.parseInt(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ChatColor.RED + "A game with the ID of \""
                    + ChatColor.YELLOW + id + ChatColor.RED
                    + "\" could not be created due it not being a numeric value");
        }

        FileConfiguration config = main.getConfig();

        maxPlayers = config.getInt("games." + id + ".maximum_players");
        minPlayers = config.getInt("games." + id + ".minimum_players");
        countdownSeconds = config.getInt("games." + id + ".countdown_seconds");
        maxRounds = config.getInt("games." + id + ".maximum_rounds");
        enforceSurvival = main.getConfig().getBoolean("enforce_survival_mode");
        broadcastWinners = main.getConfig().getBoolean("games." + id + ".broadcast_winners");
        schedulerDelay = 0;
        schedulerResetterDelay = 0;

        try {
            initialState = GameState.valueOf(config.getString("games." + id + ".state_on_startup").toUpperCase());
            if (initialState != GameState.RECRUITING && initialState != GameState.CLOSED) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ChatColor.RED + "Game number "
                    + ChatColor.YELLOW + id + ChatColor.RED
                    + " could not be created due to invalid state on startup");
        }

        reward = setupRewards(config, sender);

        physicalArena = config.getBoolean("games." + id + ".arena");

        String descriptionType = config.getString("games." + id + ".arena_options.desc_option");
        if (descriptionType == null) {
            descType = "chat";
        } else {
            descType = descriptionType;
        }

        if (physicalArena) {

            if (config.isConfigurationSection("games." + id + ".arena_options.spawn_point")) {

                if (config.getString("games." + id + ".arena_options.spawn_point.world") == null ||
                        config.getString("games." + id + ".arena_options.spawn_point.world").equalsIgnoreCase("null")) {
                    throw new IllegalArgumentException(ChatColor.RED + "Game number "
                            + ChatColor.YELLOW + id + ChatColor.RED
                            + " could not be created due to invalid spawn location. Edit the config to add coordinates");
                }

                spawn = new Location(
                        Bukkit.getWorld(config.getString("games." + id + ".arena_options.spawn_point.world").replace("'", "")),
                        Double.parseDouble(config.getString("games." + id + ".arena_options.spawn_point.x", "0.0").replace("'", "")),
                        Double.parseDouble(config.getString("games." + id + ".arena_options.spawn_point.y", "0.0").replace("'", "")),
                        Double.parseDouble(config.getString("games." + id + ".arena_options.spawn_point.z", "0.0").replace("'", "")),
                        Float.parseFloat(config.getString("games." + id + ".arena_options.spawn_point.yaw", "0.0").replace("'", "")),
                        Float.parseFloat(config.getString("games." + id + ".arena_options.spawn_point.pitch", "0.0").replace("'", "")));
            } else {
                config.set("games." + id + ".arena_options.spawn_point.world", "null");
                config.set("games." + id + ".arena_options.spawn_point.x", "null");
                config.set("games." + id + ".arena_options.spawn_point.y", "null");
                config.set("games." + id + ".arena_options.spawn_point.z", "null");
                config.set("games." + id + ".arena_options.spawn_point.yaw", "null");
                config.set("games." + id + ".arena_options.spawn_point.pitch", "null");
                main.saveConfig();
                throw new IllegalArgumentException(ChatColor.RED + "Game number "
                        + ChatColor.YELLOW + id + ChatColor.RED
                        + " could not be created due to invalid spawn location. Edit the config to add coordinates");
            }

            allowSpectators = config.getBoolean("games." + id + ".arena_options.spectators");

            switch (this.descType) {
                case "chat":
                    return;
                case "hologram":
                case "both":
                    if (config.isConfigurationSection("games." + id + ".arena_options.holo_location")) {

                        if (config.getString("games." + id + ".arena_options.holo_location.world") == null ||
                                config.getString("games." + id + ".arena_options.holo_location.world").equalsIgnoreCase("null")) {
                            return;
                        }

                        holoLocation = new Location(
                                Bukkit.getWorld(config.getString("games." + id + ".arena_options.holo_location.world")),
                                Double.parseDouble(config.getString("games." + id + ".arena_options.holo_location.x").replace("'", "")),
                                Double.parseDouble(config.getString("games." + id + ".arena_options.holo_location.y").replace("'", "")),
                                Double.parseDouble(config.getString("games." + id + ".arena_options.holo_location.z").replace("'", "")));
                    } else {
                        config.set("games." + id + ".arena_options.holo_location.world", "null");
                        config.set("games." + id + ".arena_options.holo_location.x", "null");
                        config.set("games." + id + ".arena_options.holo_location.y", "null");
                        config.set("games." + id + ".arena_options.holo_location.z", "null");
                        main.saveConfig();
                        holoLocation = null;
                    }
                    return;
                default:
                    throw new IllegalArgumentException(ChatColor.RED + "Game "
                            + ChatColor.YELLOW + id + ChatColor.RED
                            + " has an invalid desc_option. Accepted options: \"both\", \"hologram\" or \"chat\"");

            }
        } else {
            spawn = null;
            allowSpectators = false;
            if (config.isSet("games." + id + ".schedule_delay"))
                schedulerDelay = config.getInt("games." + id + ".schedule_delay");

            if (config.isSet("games." + id + ".schedule_resets"))
                schedulerResetterDelay = config.getInt("games." + id + ".schedule_resets");
            else
                schedulerResetterDelay = 60;
        }
    }

    public Reward setupRewards(FileConfiguration config, CommandSender sender) {
        Logger logger = main.getCustomLogger();

        RewardType rewardType;
        try {
            rewardType = RewardType.valueOf(config.getString("games." + id + ".reward.type").toUpperCase());
        } catch (Exception e) {
            rewardType = RewardType.NONE;
            logger.logToPlayer(sender, null, ChatColor.RED + "Invalid reward type for game "
                    + ChatColor.YELLOW + id + ChatColor.RED + ". No reward will be given.");
        }

        String messagePath = "games." + id + ".reward.message";
        String rewardMessage;
        if (config.isSet(messagePath)) {
            rewardMessage = config.getString(messagePath);
        } else {
            logger.onlyLogToPlayer(sender, ChatColor.DARK_GREEN + "No reward message set for game "
                    + ChatColor.YELLOW + id + ChatColor.DARK_GREEN + ". Reward will be given without a message.");
            rewardMessage = null;
        }

        String rewardPath = "games." + id + ".reward.reward";
        switch (rewardType) {
            case PERMISSION:
                if (main.getVaultHook() == null) {
                    logger.logToPlayer(sender, null, ChatColor.RED + "Cannot give permission reward for game "
                            + ChatColor.YELLOW + id + ChatColor.RED + " as Vault could not be hooked into");
                    return new Reward(main, RewardType.NONE, null);
                }

                ArrayList<String> list = new ArrayList<>();
                if (config.isList(rewardPath)) {
                    list = (ArrayList<String>) config.getStringList(rewardPath);
                } else if (config.isString(rewardPath)) {
                    list.add(config.getString(rewardPath));
                }
                return new Reward(main, rewardType, rewardMessage, list);

            case COMMAND:
                ArrayList<String> list2 = new ArrayList<>();
                if (config.isList(rewardPath)) {
                    list2 = (ArrayList<String>) config.getStringList(rewardPath);
                } else if (config.isString(rewardPath)) {
                    list2.add(config.getString(rewardPath));
                }
                return new Reward(main, rewardType, rewardMessage, list2);

            case CURRENCY:
                if (main.getVaultHook() == null) {
                    logger.logToPlayer(sender, null, ChatColor.RED + "Cannot give currency reward for game "
                            + ChatColor.YELLOW + id + ChatColor.RED + " as Vault could not be hooked into");
                    return new Reward(main, RewardType.NONE, null);
                }

                double amount = config.getDouble(rewardPath);
                if (amount <= 0) {
                    logger.logToPlayer(sender, null, ChatColor.RED + "Invalid currency value for game "
                            + ChatColor.YELLOW + id + ChatColor.RED + ". No reward will be given.");
                    return new Reward(main, RewardType.NONE, null);
                } else return new Reward(main, rewardType, rewardMessage, amount);

            case EXPERIENCE:
                int xp = config.getInt(rewardPath);
                if (xp <= 0) {
                    logger.logToPlayer(sender, null, ChatColor.RED + "Invalid experience value for game "
                            + ChatColor.YELLOW + id + ChatColor.RED + ". No reward will be given.");
                    return new Reward(main, RewardType.NONE, null);
                } else return new Reward(main, rewardType, rewardMessage, xp);

            case ITEM:
                List<ItemStack> items = new ArrayList<>();
                if (config.isList(rewardPath)) {
                    List<String> stringList = config.getStringList(rewardPath);
                    for (String item : stringList) {
                        Material material = Material.matchMaterial(item.split(",")[0]);
                        if (material == null) continue;

                        int quantity;
                        try {
                            quantity = Integer.parseInt(item.split(",")[1].replace(" ", ""));
                        } catch (NumberFormatException ex) {
                            quantity = 1;
                        }
                        items.add(new ItemStack(material, quantity));
                    }
                } else if (config.isString(rewardPath)) {
                    String item = config.getString(rewardPath);
                    Material material = Material.matchMaterial(item.split(",")[0]);
                    if (material != null) {
                        int quantity;
                        try {
                            quantity = Integer.parseInt(item.split(",")[1].replace(" ", ""));
                        } catch (NumberFormatException ex) {
                            quantity = 1;
                        }
                        items.add(new ItemStack(material, quantity));
                    }
                }

                if (!items.isEmpty()) return new Reward(main, rewardType, rewardMessage, items);
                else {
                    logger.logToPlayer(sender, null, ChatColor.RED + "Invalid reward item(s) for game "
                            + ChatColor.YELLOW + id + ChatColor.RED + ". No reward will be given.");
                    return new Reward(main, RewardType.NONE, null);
                }

            case NONE:
            default:
                return new Reward(main, rewardType, null);
        }
    }

    public int getId() {
        return id;
    }

    public GameState getInitialState() {
        return initialState;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void updateSpawn(Location loc) {
        spawn = loc;

        FileConfiguration config = main.getConfig();
        String id = String.valueOf(getId());

        config.set("games." + id + ".arena_options.spawn_point.world", loc.getWorld().getName());
        config.set("games." + id + ".arena_options.spawn_point.x", loc.getX());
        config.set("games." + id + ".arena_options.spawn_point.y", loc.getY());
        config.set("games." + id + ".arena_options.spawn_point.z", loc.getZ());
        config.set("games." + id + ".arena_options.spawn_point.yaw", loc.getYaw());
        config.set("games." + id + ".arena_options.spawn_point.pitch", loc.getPitch());

        main.saveConfig();
    }

    public String getDescType() {
        return descType;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public Reward getReward() {
        return reward;
    }

    public int getSchedulerDelay() {
        return schedulerDelay;
    }

    public int getSchedulerResetterDelay() {
        return schedulerResetterDelay;
    }

    public boolean isPhysicalArena() {
        return physicalArena;
    }

    public boolean enforceSurvival() {
        return enforceSurvival;
    }

    public boolean broadcastWinners() {
        return broadcastWinners;
    }

    public boolean getAllowSpectators() {
        return allowSpectators;
    }

    public Location getHoloLocation() {
        return holoLocation;
    }

    public void updateHoloLocation(Location loc) {
        holoLocation = loc;

        FileConfiguration config = main.getConfig();
        String id = String.valueOf(getId());

        config.createSection("games." + id + ".arena_options.holo_location");
        config.set("games." + id + ".arena_options.holo_location.world", loc.getWorld().getName());
        config.set("games." + id + ".arena_options.holo_location.x", loc.getX());
        config.set("games." + id + ".arena_options.holo_location.y", loc.getY());
        config.set("games." + id + ".arena_options.holo_location.z", loc.getZ());

        main.saveConfig();
    }

}