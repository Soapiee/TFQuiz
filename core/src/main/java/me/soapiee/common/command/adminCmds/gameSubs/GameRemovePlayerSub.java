package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameRemovePlayerSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameremoveplayer";

    public GameRemovePlayerSub(TFQuiz main) {
        super(main, null, 4, 4);
    }

    // /tf game <id> removeplayer <playerName>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        Player player = Bukkit.getPlayer(args[3]);
        if (player == null) {
            sendMessage(sender, messageManager.get(Message.PLAYERNOTFOUND));
            return;
        }

        if (!gameContainsPlayer(sender, player, game)) return;

        int gameID = game.getIdentifier();
        sendMessage(player, messageManager.getWithPlaceholder(Message.FORCEDGAMELEAVE, gameID));
        game.removePlayer(player);
        sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEPLAYERREMOVED, player.getName(), gameID));
    }

    private boolean gameContainsPlayer(CommandSender sender, Player player, Game game) {
        if (!game.getAllPlayers().contains(player)) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEPLAYERNOTINGAME, player.getName()));
            return false;
        }

        return true;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
