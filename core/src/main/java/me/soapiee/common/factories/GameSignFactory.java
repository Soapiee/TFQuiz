package me.soapiee.common.factories;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.GameSign;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GameSignFactory {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;

    public GameSignFactory(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    public GameSign create(CommandSender sender, String signID, int gameID) {
        FileConfiguration config = main.getConfig();

        BlockFace blockFace = validateBlockface(config, signID);
        HashMap<Integer, String> text = validateText(config, signID);
        Location location = validateLocation(sender, config, signID);
        if (location == null) return null;

        Material material = validateMaterial(sender, config, signID);
        if (material == null) return null;

        HashMap<String, String> dataValues = new HashMap<>();
        dataValues.put("game_ID", String.valueOf(gameID));
        dataValues.put("sign_ID", signID);
        dataValues.put("material", String.valueOf(material));
        dataValues.put("block_face", blockFace.toString());

        return new GameSign(main, dataValues, location, text);
    }

    public GameSign create(Sign block, int gameID) {
        FileConfiguration config = main.getConfig();
        Location location = block.getLocation();
        Material material = block.getType();

        if (!config.isConfigurationSection("signs")) config.createSection("signs");
        String signID = getLowestNumber(config.getConfigurationSection("signs").getKeys(false));

        config.set("signs." + signID + ".game_ID", gameID);
        config.set("signs." + signID + ".material", material.toString());
        config.set("signs." + signID + ".world", location.getWorld().getName());
        config.set("signs." + signID + ".x", location.getX());
        config.set("signs." + signID + ".y", location.getY());
        config.set("signs." + signID + ".z", location.getZ());

        BlockFace blockFace;
        if (block.getBlockData() instanceof WallSign) {
            WallSign wallSign = (WallSign) block.getBlockData();
            blockFace = wallSign.getFacing();
            config.set("signs." + signID + ".facing", blockFace.toString().toLowerCase());
        } else {
            org.bukkit.block.data.type.Sign signdata = (org.bukkit.block.data.type.Sign) block.getBlockData(); //1.20
            blockFace = signdata.getRotation();
            config.set("signs." + signID + ".facing", blockFace.toString().toLowerCase());
        }

        HashMap<Integer, String> text = setDefaultText(config, "signs." + signID + ".format");
        HashMap<String, String> dataValues = new HashMap<>();
        dataValues.put("game_ID", String.valueOf(gameID));
        dataValues.put("sign_ID", signID);
        dataValues.put("material", material.toString());
        dataValues.put("block_face", blockFace.toString());

        return new GameSign(main, dataValues, location, text);
    }

    private World validateWorld(CommandSender sender, FileConfiguration config, String signID) {
        World world = null;

        if (config.isSet("signs." + signID + ".world")) {
            world = Bukkit.getWorld(config.getString("signs." + signID + ".world"));
        }

        if (world == null)
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDSIGNLOCATION, signID));

        return world;
    }

    private Location validateLocation(CommandSender sender, FileConfiguration config, String signID) {
        World world = validateWorld(sender, config, signID);
        if (world == null) return null;

        String xPath = "signs." + signID + ".x";
        String yPath = "signs." + signID + ".y";
        String zPath = "signs." + signID + ".z";

        if (!config.isSet(xPath) || !config.isSet(yPath) || !config.isSet(zPath)) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDSIGNLOCATION, signID));
            return null;
        }

        double x = config.getDouble(xPath);
        double y = config.getDouble(yPath);
        double z = config.getDouble(zPath);

        return new Location(world, x, y, z);
    }

    private Material validateMaterial(CommandSender sender, FileConfiguration config, String signID) {
        Material material;
        String materialPath = ("signs." + signID + ".material");

        material = Material.matchMaterial(config.getString(materialPath, "OAK_WALL_SIGN"));

        if (material == null || !material.name().contains("SIGN")) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDSIGNMATERIAL, signID));
            material = Material.OAK_SIGN;
            config.set(materialPath, "OAK_SIGN");
        }

        return material;
    }

    private BlockFace validateBlockface(FileConfiguration config, String signID) {
        String path = "signs." + signID + ".facing";
        return BlockFace.valueOf(config.isSet(path) ? config.getString(path).toUpperCase() : "NORTH");
    }

    private HashMap<Integer, String> validateText(FileConfiguration config, String signID) {
        String formatPath = "signs." + signID + ".format";

        if (!config.isSet(formatPath)) return setDefaultText(config, formatPath);

        HashMap<Integer, String> text = new HashMap<>();
        List<String> lines = config.getStringList(formatPath);
        for (String string : lines) {
            text.put(lines.indexOf(string) + 1, string);
        }

        return text;
    }

    private HashMap<Integer, String> setDefaultText(FileConfiguration config, String formatPath) {
        HashMap<Integer, String> text = new HashMap<>();

        text.put(1, "%game_ID%");
        text.put(2, " ");
        text.put(3, "Edit me..");

        config.set(formatPath, new ArrayList<>(text.values()));
        main.saveConfig();

        return text;
    }

    private String getLowestNumber(Set<String> numbers) {
        int lowest = 1;

        while (numbers.contains(String.valueOf(lowest))) {
            lowest++;
        }

        return String.valueOf(lowest);
    }

}
