package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
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
        if (isConsole(sender)) return;
        Player player = (Player) sender;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        game.setSpawn(player.getLocation());
        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMESPAWNSET, game.getIdentifier()));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
