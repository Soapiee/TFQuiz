package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameAddSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameadd";

    public GameAddSub(TFQuiz main) {
        super(main, null, 2, 2);
    }

    // /tf game add
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

//        gameManager.addGame();
//        sendMessage(sender, messageManager.get(Message.GAMEADDED));
//        sendMessage(sender, messageManager.get(Message.GAMEADDEDERROR));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
