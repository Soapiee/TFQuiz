package me.soapiee.common.command.adminCmds.gameSubs;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.DescriptionType;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.Hologram;
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

        Hologram hologram = game.getArenaHandler().getHologram();
        if (hologram.getSpawnPoint() != null) hologram.despawn();
        Location holoLocation = player.getLocation();
        hologram.setLocation(holoLocation);
        updateConfig(game.getIdentifier(), holoLocation);

        DescriptionType descType = game.getArenaHandler().getDescType();
        if (game.getState() != GameState.LIVE && (descType == DescriptionType.HOLOGRAM || descType == DescriptionType.BOTH))
            hologram.spawn();

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
