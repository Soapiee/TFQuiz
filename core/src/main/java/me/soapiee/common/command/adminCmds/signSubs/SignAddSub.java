package me.soapiee.common.command.adminCmds.signSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SignAddSub extends AbstractAdminSub {

    private final String IDENTIFIER = "signadd";

    public SignAddSub(TFQuiz main) {
        super(main, "tfquiz.admin.signs", 3, 3);
    }

    // /tf sign add <gameID> (must be looking at sign)
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, true)) return;
        Player player = (Player) sender;

        Game game = getGame(sender, args[2]);
        if (game == null) return;

        Sign signBlock = getSignBlock(player);
        if (signBlock == null) return;

        if (signAlreadyExist(signBlock, player)) return;

        main.getGameSignManager().createSign(signBlock, game);
        sendMessage(player, messageManager.getWithPlaceholder(Message.SIGNADDED, game.getIdentifier()));
    }

    private boolean signAlreadyExist(Sign signBlock, Player player) {
        String dataContainer = signBlock.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING);

        if (dataContainer != null && gameSignManager.getSign(dataContainer) != null) {
            sendMessage(player, messageManager.get(Message.SIGNALREADYEXISTS));
            return true;
        }

        return false;
    }

    private Sign getSignBlock(Player player) {
        Block blockTarget = player.getTargetBlock(null, 5);

        Sign signBlock = null;
        if (blockTarget.getState() instanceof Sign) signBlock = (Sign) blockTarget.getState();
        if (signBlock == null) player.sendMessage(Utils.addColour(messageManager.get(Message.SIGNNOTLOOKINGATSIGN)));

        return signBlock;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
