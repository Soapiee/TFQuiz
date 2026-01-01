package me.soapiee.common.command.playerCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        if (gameManager.getGame(player) != null) {
            sendMessage(player, messageManager.get(Message.GAMENOTNULL));
            return;
        }

        int gameID = validateGameID(player, args[1]);
        if (gameID == -1) return;

        Game gameToJoin = validateGame(player, gameID);
        if (gameToJoin == null) return;

        int result = gameToJoin.addPlayer(player);
        switch (result) {
            case 1:
                sendMessage(player, messageManager.get(Message.GAMEINVALIDGAMEMODE));
                return;
            case 2:
                sendMessage(player, messageManager.get(Message.GAMEINVALIDSTATE));
                return;
            case 3:
                sendMessage(player, messageManager.get(Message.GAMEFULL));
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
