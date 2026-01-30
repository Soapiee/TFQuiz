package me.soapiee.common.command.playerCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.AddPlayerResult;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinSub extends AbstractPlayerSub {

    private final String IDENTIFIER = "join";

    public JoinSub(TFQuiz main) {
        super(main, "tfquiz.player.join", 2, 2);
    }

    // /game join <gameID>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (gameManager.getGame(uuid) != null) {
            sendMessage(player, messageManager.get(Message.GAMENOTNULL));
            return;
        }

        int gameID = validateGameID(player, args[1]);
        if (gameID == -1) return;

        Game gameToJoin = validateGame(player, gameID);
        if (gameToJoin == null) return;

        AddPlayerResult result = gameToJoin.getPlayerHandler().addPlayer(uuid);
        switch (result) {
            case NOT_SURVIVAL:
                sendMessage(player, messageManager.get(Message.GAMEINVALIDGAMEMODE));
                return;
            case GAME_CLOSED:
                sendMessage(player, messageManager.get(Message.GAMEINVALIDSTATE));
                return;
            case GAME_FULL:
                sendMessage(player, messageManager.get(Message.GAMEFULL));
                return;
            case SUCCESS:
                sendMessage(player, messageManager.getWithPlaceholder(Message.GAMEJOIN, gameToJoin));
        }
    }


    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    private int validateGameID(Player player, String input) {
        int gameID;
        try {
            gameID = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sendMessage(player, messageManager.get(Message.GAMEINVALIDGAMEID));
            return -1;
        }

        return gameID;
    }

    private Game validateGame(Player player, int gameID) {
        Game gameToJoin = gameManager.getGame(gameID);

        if (gameToJoin == null) {
            sendMessage(player, messageManager.get(Message.GAMEINVALIDGAMEID));
        }

        return gameToJoin;
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
