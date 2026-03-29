package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.AddPlayerResult;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameAddPlayerSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameaddplayer";

    public GameAddPlayerSub(TFQuiz main) {
        super(main, null, 4, 4);
    }

    // /tf game <id> addplayer <playerName>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        if (gameIsInProgress(sender, game, Message.GAMEPLAYERADDEDERROR)) return;

        Player player = Bukkit.getPlayer(args[3]);
        if (player == null) {
            sendMessage(sender, messageManager.get(Message.PLAYERNOTFOUND));
            return;
        }

        if (playerIsInAGame(sender, player)) return;

        Message message;
        AddPlayerResult outcome = game.getPlayerHandler().addPlayer(player.getUniqueId());
        switch (outcome) {
            case SUCCESS:
                sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEPLAYERADDED, player.getName(), game.getIdentifier()));
                sendMessage(player, messageManager.getWithPlaceholder(Message.GAMEJOIN, game));
                return;
            case NOT_SURVIVAL:
                message = Message.GAMEINVALIDGAMEMODEOTHER;
                break;
            case GAME_CLOSED:
                message = Message.GAMEINVALIDSTATE;
                break;
            case GAME_FULL:
            default:
                message = Message.GAMEFULL;
        }

        sendMessage(sender, messageManager.getWithPlaceholder(message, game));
    }

    private boolean playerIsInAGame(CommandSender sender, Player player) {
        if (gameManager.getGame(player.getUniqueId()) != null) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEPLAYERALREADYINGAME, player.getName()));
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
