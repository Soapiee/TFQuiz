package me.soapiee.common.manager;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.GameSpecs;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.instance.logic.Question;
import me.soapiee.common.instance.logic.Scheduler;
import me.soapiee.common.listener.PlayerListener;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Logger;
import me.soapiee.common.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GameManager {

    private final List<Game> games = new ArrayList<>();
    private final TFQuiz main;
    private final Logger logger;
    private Location lobbySpawn;
    private boolean clearInv;
    private boolean enforceLobbySpawn;
    private final ArrayList<String> disallowedCommands = new ArrayList<>();

    private final File TFFile;
    private final YamlConfiguration TFConfig = new YamlConfiguration();
    private final ArrayList<Question> trueQuestions = new ArrayList<>();
    private final ArrayList<Question> falseQuestions = new ArrayList<>();

    private final HashMap<Integer, Scheduler> schedulers;

    private InventoryManager inventoryManager;
    private boolean saveInvs;

    public GameManager(TFQuiz main) {
        this.main = main;
        logger = main.getCustomLogger();

        TFFile = new File(main.getDataFolder(), "questions.yml");
        schedulers = new HashMap<>();

        readMainConfig(Bukkit.getConsoleSender());
        createTFList(Bukkit.getConsoleSender());

        for (String error : loadSigns(null))
            Utils.consoleMsg(error);

        startSchedulders(main.getConfig());
    }

    public boolean reloadAll(CommandSender sender, PlayerListener playerListener) {
        boolean noErrors = true;

        games.clear();
        trueQuestions.clear();
        falseQuestions.clear();
        cancelSchedulders();
        schedulers.clear();

        main.reloadConfig();
        startSchedulders(main.getConfig());
        playerListener.ruleCheck();
        if (!readMainConfig(sender)) noErrors = false;
        if (!createTFList(sender)) noErrors = false;

        for (String string1 : loadSigns(sender)) {
            logger.logToPlayer(sender, null, string1);
            noErrors = false;
        }

        return noErrors;
    }

    public boolean readMainConfig(CommandSender sender) {
        FileConfiguration config = main.getConfig();
        boolean noErrors = true;

        lobbySpawn = new Location(
                //TODO: create configSection
                Bukkit.getWorld(config.getString("lobby_spawn.world")),
                config.getDouble("lobby_spawn.x"),
                config.getDouble("lobby_spawn.y"),
                config.getDouble("lobby_spawn.z"),
                (float) config.getDouble("lobby_spawn.yaw"),
                (float) config.getDouble("lobby_spawn.pitch"));

        clearInv = config.getBoolean("empty_inv_on_arena_join");
        enforceLobbySpawn = config.getBoolean("enforce_lobby_spawn");
        disallowedCommands.addAll(config.getStringList("disallowed_commands"));

        saveInvs = config.getBoolean("save_player_inventories");
        if (saveInvs) inventoryManager = new InventoryManager(main);

        for (String id : config.getConfigurationSection("games.").getKeys(false)) {
            GameSpecs specs;
            try {
                specs = new GameSpecs(main, sender, id);
            } catch (IllegalArgumentException e) {
                logger.logToPlayer(sender, e, e.getMessage());
                noErrors = false;
                continue;
            }

            Game game = new Game(main, specs);
            games.add(game);
            logger.onlyLogToPlayer(sender, ChatColor.GREEN + "Game " + ChatColor.YELLOW + id + ChatColor.GREEN + " was successfully created");
        }

        return noErrors;
    }

    public boolean createTFList(CommandSender sender) {
        if (!TFFile.exists()) {
            main.saveResource("questions.yml", false);
        }

        try {
            TFConfig.load(TFFile);
        } catch (Exception ex) {
            logger.logToPlayer(sender, ex, "Could not load questions.yml");
            return false;
        }

        if (TFConfig.getConfigurationSection("Questions.").getKeys(false).isEmpty()) {
            logger.logToPlayer(sender, null, "Could not find any questions inside questions.yml");
            return false;
        }

        for (String string : TFConfig.getConfigurationSection("Questions").getKeys(false)) {
            if (TFConfig.getBoolean("Questions." + string + ".Answer")) {
                trueQuestions.add(new Question(TFConfig.getString("Questions." + string + ".Question"),
                        TFConfig.getString("Questions." + string + ".Correction_Message")));
            } else {
                falseQuestions.add(new Question(TFConfig.getString("Questions." + string + ".Question"),
                        TFConfig.getString("Questions." + string + ".Correction_Message")));
            }
        }

        return true;
    }

    public ArrayList<String> loadSigns(CommandSender sender) {
        ArrayList<String> errors = new ArrayList<>();
        FileConfiguration config = main.getConfig();

        if (!config.isConfigurationSection("signs.")) return errors;
        if (config.getConfigurationSection("signs.") == null) return errors;

        for (String id : config.getConfigurationSection("signs.").getKeys(false)) {
            int gameID;
            Game game = null;
            if (config.isSet("signs." + id + ".game_ID")) {
                gameID = config.getInt("signs." + id + ".game_ID");
                game = getGame(gameID);
            }
            if (game == null) {
                errors.add(ChatColor.RED + "Skipping sign with ID \" + id + \" due to invalid game ID");
                continue;
            }

            World world = null;
            if (config.isSet("signs." + id + ".world")) {
                world = Bukkit.getWorld(config.getString("signs." + id + ".world"));
            }

            if (world == null) {
                errors.add(ChatColor.RED + "Skipping sign with ID " + id + " due to invalid world");
                continue;
            }

            Material material = Material.matchMaterial(config.getString("signs." + id + ".material"));
            if (material == null || !material.name().contains("SIGN")) {
                errors.add(ChatColor.RED + "Invalid sign material for sign " + id + ". Defaulting to OAK_WALL_SIGN");
            }

            GameSign gameSign = new GameSign(main, game, id);
            game.addSign(gameSign);
            logger.logToPlayer(sender, null, ChatColor.GREEN + "Game sign " + ChatColor.YELLOW + id + ChatColor.GREEN + " was successfully created");
        }

        return errors;
    }
//TODO:
//    public void deleteGame(Game game) {
//        // Cancel any schedulers
//        Scheduler scheduler = getScheduler(game.getID());
//        if (scheduler != null) {
//            try {
//                scheduler.setPlayed();
//                scheduler.cancel();
//            } catch (IllegalStateException ignored) {
//            }
//        }
//
//        // Remove game from hashMap
//        int gameID = game.getID();
//        games.remove(game);
//
//        // Remove it from the config
//        FileConfiguration config = main.getConfig();
//        config.set("games." + gameID, null);
//        main.saveConfig();
//    }
//
//    public void addGame() {
//
//    }

    public Game getGame(Player player) {
        for (Game game : games) {
            if (game.getAllPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }

    public Game getGame(int id) {
        for (Game game : games) {
            if (game.getID() == id) {
                return game;
            }
        }
        return null;
    }

    public Game getGame(String signID) {
        for (Game game : games) {
            if (game.getSign(signID) != null) {
                return game;
            }
        }
        return null;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setLobbySpawn(Location loc) {
        lobbySpawn = loc;
        FileConfiguration config = main.getConfig();

        config.set("lobby_spawn.world", loc.getWorld().getName());
        config.set("lobby_spawn..x", loc.getX());
        config.set("lobby_spawn..y", loc.getY());
        config.set("lobby_spawn..z", loc.getZ());
        config.set("lobby_spawn.yaw", loc.getY());
        config.set("lobby_spawn.pitch", loc.getPitch());

        main.saveConfig();
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public ArrayList<Question> getTrueQuestions() {
        return trueQuestions;
    }

    public ArrayList<Question> getFalseQuestions() {
        return falseQuestions;
    }

    public void startSchedulders(FileConfiguration config) {
        for (Game game : games) {
            int id = game.getID();
            if (config.isSet("games." + id + ".schedule_delay")) {
                if (game.isPhysicalArena()) return;

                int delay = config.getInt("games." + id + ".schedule_delay", 30);
                int resetDelay = config.getInt("games." + id + ".schedule_resets", 60);
                schedulers.put(id, new Scheduler(main, game, delay, resetDelay));
            }
        }
    }

    public void cancelSchedulders() {
        for (Scheduler scheduler : schedulers.values()) {
            try {
                scheduler.setPlayed();
                scheduler.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public boolean hasScheduler(Game game) {
        return schedulers.containsKey(game.getID());
    }

    public Scheduler getScheduler(int gameID) {
        return schedulers.get(gameID);
    }

    public void newScheduler(int gameID, int delay, int resetDelay) {
        if (this.schedulers.containsKey(gameID)) {
            try {
                this.schedulers.get(gameID).setPlayed();
                this.schedulers.get(gameID).cancel();
            } catch (IllegalStateException ignored) {
            }
        }
        this.schedulers.put(gameID, new Scheduler(main, this.getGame(gameID), delay, resetDelay));
    }

    public boolean getClearInvSetting() {
        return clearInv;
    }

    public boolean getEnforceLobbySpawn() {
        return enforceLobbySpawn;
    }

    public ArrayList<String> getDisallowedCommands() {
        return disallowedCommands;
    }

    public String getLowestNumber(Set<String> numbers) {
        int lowest = 1;

        while (numbers.contains(String.valueOf(lowest))) {
            lowest++;
        }

        return String.valueOf(lowest);
    }

    public void saveSign(Sign block, int gameID) { //saves a newly created (block)sign (created via command)
        Location loc = block.getLocation();

        FileConfiguration config = main.getConfig();
        if (!config.isConfigurationSection("signs")) config.createSection("signs");
        String signID = getLowestNumber(config.getConfigurationSection("signs").getKeys(false));

        config.set("signs." + signID + ".game_ID", gameID);
        config.set("signs." + signID + ".material", block.getType().toString());
        config.set("signs." + signID + ".world", loc.getWorld().getName());
        config.set("signs." + signID + ".x", loc.getX());
        config.set("signs." + signID + ".y", loc.getY());
        config.set("signs." + signID + ".z", loc.getZ());

        if (block.getBlockData() instanceof WallSign) {
            WallSign wallSign = (WallSign) block.getBlockData();
            BlockFace face = wallSign.getFacing();
            config.set("signs." + signID + ".facing", face.toString().toLowerCase());
        } else {
            org.bukkit.block.data.type.Sign signdata = (org.bukkit.block.data.type.Sign) block.getBlockData(); //1.20
            BlockFace face = signdata.getRotation();
            config.set("signs." + signID + ".facing", face.toString().toLowerCase());
        }

        ArrayList<String> defaultFormat = new ArrayList<>();
        defaultFormat.add("%game_ID%");
        defaultFormat.add(" ");
        defaultFormat.add("Edit me..");
        config.set("signs." + signID + ".format", defaultFormat);

        main.saveConfig();

        Game game = getGame(gameID);
        game.addSign(new GameSign(main, game, signID));
    }

    public void saveSign(GameSign sign) { //updates the text on an already existing ArenaSign in the config
        FileConfiguration config = main.getConfig();
        String signID = sign.getID();

        config.set("signs." + signID + ".format", sign.getText());

        main.saveConfig();
    }

    public void deleteSign(String signID) {
        FileConfiguration config = main.getConfig();

        config.set("signs." + signID, null);
        main.saveConfig();
        getGame(signID).removeSign(signID);
    }

    public GameSign getSign(String signID) {
        if (getGame(signID) == null) {
            return null;
        }
        return getGame(signID).getSign(signID);
    }

    public InventoryManager getInventoryManager() {
        if (!saveInvs) return null;
        return inventoryManager;
    }

    public void killOtherHolos() {
        for (Game game : getGames()) {
            if (game.getSpawn() != null) {
                for (Entity entity : game.getSpawn().getWorld().getEntities()) {
                    if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE)) {
                        entity.remove();
                    }
                }
            }
        }
    }
}
