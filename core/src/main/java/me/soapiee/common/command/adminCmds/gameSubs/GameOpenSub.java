package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameOpenSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameopen";

    public GameOpenSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> open
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        if (gameHasSchedulder(sender, game, Message.GAMEOPENEDERROR2)) return;

        Message message;
        if (gameIsClosed(game)) {
            game.setState(GameState.RECRUITING);
            message = Message.GAMEOPENED;
        } else message = Message.GAMEOPENEDERROR;

        sendMessage(sender, messageManager.getWithPlaceholder(message, game));
    }

    private boolean gameIsClosed(Game game) {
        return game.getState() == GameState.CLOSED;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
