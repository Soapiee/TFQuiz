package me.soapiee.tfquiz.command.adminCmds.gameSubs;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.command.adminCmds.AbstractAdminSub;
import me.soapiee.tfquiz.games.Game;
import me.soapiee.tfquiz.games.LifeCycleHandler;
import me.soapiee.tfquiz.games.enums.GameState;
import me.soapiee.tfquiz.utils.Message;
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
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEFORCEENDERROR, game.getIdentifier()));
            return;
        }

        LifeCycleHandler lifeCycleHandler = game.getLifeCycleHandler();
        if (countdownIsActive(game)) {
            lifeCycleHandler.reset(false, false);
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEFORCEENDED, game.getIdentifier()));
            return;
        }

        Message message;
        if (args.length == 4 && args[3].equals("-without")) {
            lifeCycleHandler.reset(true, true);
            message = Message.GAMEFORCEENDED;
        } else {
//            game.end();
            lifeCycleHandler.endGame();
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
