package me.soapiee.common.command.adminCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListAdminSub extends AbstractAdminSub {

    private final String IDENTIFIER = "list";

    public ListAdminSub(TFQuiz main) {
        super(main, null, 1, 1);
    }

    // /tf list
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        StringBuilder builder = new StringBuilder();
        builder.append(messageManager.get(Message.GAMELISTHEADER));
        for (Game game : gameManager.getGames())
            builder.append("\n").append(messageManager.getWithPlaceholder(Message.GAMELIST, game));

        sendMessage(sender, builder.toString());
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
