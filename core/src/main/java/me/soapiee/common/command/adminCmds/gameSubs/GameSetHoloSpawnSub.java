package me.soapiee.common.command.adminCmds.gameSubs;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.cosmetic.Hologram;
import org.bukkit.command.CommandSender;
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
        if (isConsole(sender)) return;
        Player player = (Player) sender;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        Hologram hologram = game.getHologram();
        if (hologram.getSpawnPoint() != null) hologram.despawn();
        hologram.setLocation(player.getLocation());

        if (game.getState() != GameState.LIVE && (game.getDescType().equals("hologram") || game.getDescType().equals("both")))
            hologram.spawn();

        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMEHOLOSPAWNSET, game.getIdentifier()));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
