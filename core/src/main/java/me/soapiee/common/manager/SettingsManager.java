package me.soapiee.common.manager;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class SettingsManager {

    private final TFQuiz main;
    private final Logger customLogger;
    private final MessageManager messageManager;

    @Getter private Location lobbySpawn;
    @Getter private boolean clearInv;
    @Getter private boolean enforceLobbySpawn;
    @Getter private boolean saveInvs;
    @Getter private final ArrayList<String> disallowedCommands = new ArrayList<>();

    // Arena flags
    @Getter private boolean fallDamage;
    @Getter private boolean pvpDamage;
    @Getter private boolean hunger;
    @Getter private boolean breakBlocks;
    @Getter private boolean placeBlocks;
    @Getter private boolean teleport;

    public SettingsManager(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();

        load(null);
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        FileConfiguration config = main.getConfig();

        disallowedCommands.addAll(config.getStringList("disallowed_commands"));
        clearInv = getConfigValue(sender, "empty_inv_on_arena_join", true);
        enforceLobbySpawn = getConfigValue(sender, "enforce_lobby_spawn", true);
        saveInvs = getConfigValue(sender, "save_player_inventories", false);
        fallDamage = getConfigValue(sender, "arena_flags.allow_fall_damage", true);
        pvpDamage = getConfigValue(sender, "arena_flags.allow_pvp_damage", false);
        hunger = getConfigValue(sender, "arena_flags.allow_hunger", false);
        breakBlocks = getConfigValue(sender, "arena_flags.allow_block_break", false);
        placeBlocks = getConfigValue(sender, "arena_flags.allow_block_place", false);
        teleport = getConfigValue(sender, "arena_flags.allow_teleport", false);
        lobbySpawn = validateLocation();

        if (lobbySpawn == null && enforceLobbySpawn) {
            enforceLobbySpawn = false;
            customLogger.logToPlayer(sender, null, messageManager.get(Message.INVALIDLOBBYSPAWN));
            return true;
        }

        return false;
    }

    private boolean getConfigValue(CommandSender sender, String path, boolean defaultValue) {
        FileConfiguration config = main.getConfig();

        if (!config.isSet(path))
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.LOADSETTINGSERROR, path, defaultValue));

        return config.getBoolean(path, defaultValue);
    }

    private Location validateLocation() {
        FileConfiguration config = main.getConfig();
        String path = "lobby_spawn";
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
                    Double.parseDouble(z.replace("'", "")),
                    Float.parseFloat(yaw.replace("'", "")),
                    Float.parseFloat(pitch.replace("'", "")));
        } else {
            setEmptyLobbySpawn();
            location = null;
        }

        return location;
    }

    private void setEmptyLobbySpawn() {
        FileConfiguration config = main.getConfig();
        String path = "lobby_spawn";

        config.set(path + ".world", "null");
        config.set(path + ".x", "null");
        config.set(path + ".y", "null");
        config.set(path + ".z", "null");
        config.set(path + ".yaw", "null");
        config.set(path + ".pitch", "null");

        main.saveConfig();
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
}
