package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameEndSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameend";

    public GameEndSub(TFQuiz main) {
        super(main, null, 3, 4);
    }

    // /tf game <id> end
    // /tf game <id> end -without
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        if (gameHasSchedulder(sender, game, Message.GAMEENDSCHEDULERERROR)) return;

        if (!gameIsInProgress(sender, game, null)) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEFORCEENDERROR, game.getID()));
            return;
        }

        if (countdownIsActive(game)) {
            game.reset(false, false);
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEFORCEENDED, game.getID()));
            return;
        }

        Message message;
        if (args.length == 4 && args[3].equals("-without")) {
            game.reset(true, true);
            message = Message.GAMEFORCEENDED;
        } else {
            game.end();
            message = Message.GAMEFORCEENDEDWITHWINNERS;
        }

        sendMessage(sender, messageManager.getWithPlaceholder(message, game));
    }

    private boolean countdownIsActive(Game game) {
        return game.getState() == GameState.COUNTDOWN;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
