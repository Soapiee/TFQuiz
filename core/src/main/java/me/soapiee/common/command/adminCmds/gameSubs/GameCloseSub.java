package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameCloseSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameclose";

    public GameCloseSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> close
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        if (gameHasSchedulder(sender, game, Message.GAMECLOSEDERROR2)) return;

        Message message;
        if (gameIsCloseable(game)) {
            game.reset(true, true);
            game.setState(GameState.CLOSED);
            message = Message.GAMECLOSED;
        } else message = Message.GAMECLOSEDERROR;

        sendMessage(sender, messageManager.getWithPlaceholder(message, game));
    }

    private boolean gameIsCloseable(Game game) {
        return game.getState() == GameState.RECRUITING || game.getState() == GameState.COUNTDOWN;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
