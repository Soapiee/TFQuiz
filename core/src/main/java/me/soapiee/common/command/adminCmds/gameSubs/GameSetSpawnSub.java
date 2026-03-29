package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameSetSpawnSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gamesetspawn";

    public GameSetSpawnSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> setspawn
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, true)) return;
        Player player = (Player) sender;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        Location newSpawn = player.getLocation();
        game.getArenaHandler().setSpawn(newSpawn);
        updateConfig(game.getIdentifier(), newSpawn);

        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMESPAWNSET, game.getIdentifier()));
    }

    private void updateConfig(int gameID, Location location) {
        FileConfiguration config = main.getConfig();
        config.set("games." + gameID + ".arena_options.spawn_point.world", location.getWorld().getName());
        config.set("games." + gameID + ".arena_options.spawn_point.x", location.getX());
        config.set("games." + gameID + ".arena_options.spawn_point.y", location.getY());
        config.set("games." + gameID + ".arena_options.spawn_point.z", location.getZ());
        config.set("games." + gameID + ".arena_options.spawn_point.yaw", location.getYaw());
        config.set("games." + gameID + ".arena_options.spawn_point.pitch", location.getPitch());

        main.saveConfig();
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
