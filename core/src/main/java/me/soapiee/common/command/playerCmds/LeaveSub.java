package me.soapiee.common.command.playerCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaveSub extends AbstractPlayerSub {

    private final String IDENTIFIER = "leave";

    public LeaveSub(TFQuiz main) {
        super(main, "tfquiz.player.join", 1, 1);
    }

    // /game leave
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        Player player = (Player) sender;

        Game gameToLeave = gameManager.getGame(player);
        if (gameToLeave == null) {
            sendMessage(player, messageManager.get(Message.GAMELEFTERROR));
            return;
        }

        gameToLeave.removePlayer(player);
        sendMessage(player, messageManager.getWithPlaceholder(Message.GAMELEAVE, gameToLeave));
    }


    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
