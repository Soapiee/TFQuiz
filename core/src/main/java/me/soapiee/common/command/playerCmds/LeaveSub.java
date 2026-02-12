package me.soapiee.common.command.playerCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        UUID uuid = player.getUniqueId();

        Game gameToLeave = gameManager.getGame(uuid);
        if (gameToLeave == null) {
            sendMessage(player, messageManager.get(Message.GAMELEFTERROR));
            return;
        }

        gameToLeave.getPlayerHandler().removePlayer(uuid);
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
