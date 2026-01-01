package me.soapiee.common.command.adminCmds.gameSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameInfoSub extends AbstractAdminSub {

    private final String IDENTIFIER = "gameinfo";

    public GameInfoSub(TFQuiz main) {
        super(main, null, 3, 3);
    }

    // /tf game <id> info
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        Game game = getGame(sender, args[1]);
        if (game == null) return;

        sendMessage(sender, messageManager.getInfo(Message.GAMEINFO, game));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
