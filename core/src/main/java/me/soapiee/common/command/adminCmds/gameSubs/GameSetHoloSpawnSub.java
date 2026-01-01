package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameSetHoloSpawnSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gamesetholospawn";

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

        if (game.getHologram() != null) {
            game.getHologram().despawn();
            game.getHologram().setLocation(player.getLocation());
        }

        game.updateHologramSpawn(player.getLocation());
        if (game.getState() != GameState.LIVE && (game.getDescType().equals("hologram") || game.getDescType().equals("both")))
            game.getHologram().spawn();
        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMEHOLOSPAWNSET, game.getID()));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
