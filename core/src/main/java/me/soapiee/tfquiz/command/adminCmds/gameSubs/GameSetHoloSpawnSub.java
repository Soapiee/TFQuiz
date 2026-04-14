package me.soapiee.tfquiz.command.adminCmds.gameSubs;

import lombok.Getter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.command.adminCmds.AbstractAdminSub;
import me.soapiee.tfquiz.games.Game;
import me.soapiee.tfquiz.games.arena.ArenaHandler;
import me.soapiee.tfquiz.games.arena.Hologram;
import me.soapiee.tfquiz.games.enums.DescriptionType;
import me.soapiee.tfquiz.games.enums.GameState;
import me.soapiee.tfquiz.utils.Message;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameSetHoloSpawnSub extends AbstractAdminSub {

    @Getter private final String IDENTIFIER = "gamesetholospawn";

    public GameSetHoloSpawnSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> setholospawn
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, true)) return;
        Player player = (Player) sender;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        ArenaHandler arenaHandler = game.getArenaHandler();
        Hologram hologram = arenaHandler.getHologram();

        arenaHandler.despawnHologram();
        Location newLocation = player.getLocation();
        newLocation.setY(newLocation.getY() + (0.25 * hologram.getText().length) + 1);

        hologram.setLocation(newLocation);
        updateConfig(game.getIdentifier(), newLocation);

        DescriptionType descType = game.getArenaHandler().getDescType();
        if (game.getState() != GameState.LIVE && (descType == DescriptionType.HOLOGRAM || descType == DescriptionType.BOTH))
            arenaHandler.spawnHologram();

        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMEHOLOSPAWNSET, game.getIdentifier()));
    }

    private void updateConfig(int gameID, Location location) {
        FileConfiguration config = main.getConfig();
        config.set("games." + gameID + ".arena_options.holo_location.world", location.getWorld().getName());
        config.set("games." + gameID + ".arena_options.holo_location.x", location.getX());
        config.set("games." + gameID + ".arena_options.holo_location.y", location.getY());
        config.set("games." + gameID + ".arena_options.holo_location.z", location.getZ());

        main.saveConfig();
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
