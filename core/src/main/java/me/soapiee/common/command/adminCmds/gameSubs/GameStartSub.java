package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.handlers.LifeCycleHandler;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameStartSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gamestart";

    public GameStartSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> start
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        if (gameHasSchedulder(sender, game, Message.GAMESTARTSCHEDULERERROR)) return;
        if (gameIsInProgress(sender, game, Message.GAMEFORCESTARTERROR)) return;
        if (gameIsClosed(sender, game)) return;
        if (gameIsEmpty(sender, game)) return;

        LifeCycleHandler lifeCycleHandler = game.getLifeCycleHandler();
        lifeCycleHandler.setForceStart(true);
        lifeCycleHandler.getCountdown().start();

        sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMEFORCESTARTED, game));
    }

    private boolean gameIsClosed(CommandSender sender, Game game) {
        if (game.getState() == GameState.CLOSED) {
            sendMessage(sender, messageManager.get(Message.GAMESTARTCLOSEDERROR));
            return true;
        }
        return false;
    }

    private boolean gameIsEmpty(CommandSender sender, Game game) {
        if (gamePlayerManager.getAllPlayers(game.getIdentifier()).isEmpty()) {
            sendMessage(sender, messageManager.get(Message.GAMESTARTEMPTYERROR));
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
