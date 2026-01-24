package me.soapiee.common.command.playerCmds;


import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.SubCmd;
import me.soapiee.common.enums.Message;
import me.soapiee.common.managers.GameManager;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractPlayerSub implements SubCmd {

    protected final TFQuiz main;
    protected final MessageManager messageManager;
    protected final GameManager gameManager;

    protected final String PERMISSION;
    protected final int MIN_ARGS;
    protected final int MAX_ARGS;

    public AbstractPlayerSub(TFQuiz main, String PERMISSION, int MIN_ARGS, int MAX_ARGS) {
        this.main = main;
        this.messageManager = main.getMessageManager();
        this.gameManager = main.getGameManager();

        this.PERMISSION = PERMISSION;
        this.MIN_ARGS = MIN_ARGS;
        this.MAX_ARGS = MAX_ARGS;
    }

    public boolean checkRequirements(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (!checkPermission(player, PERMISSION)) {
            sendMessage(player, messageManager.get(Message.NOPERMISSION));
            return false;
        }

        if (!checkArgs(args)) {
            Message message = Message.GAMECCMDUSAGE;
            if (args[0].equalsIgnoreCase("join")) message = Message.GAMEJOINCMDUSAGE;

            sendMessage(player, messageManager.getWithPlaceholder(message, label));
            return false;
        }

        return true;
    }

    protected boolean checkPermission(Player player, String permission) {
        if (permission == null) return true;
        return player.hasPermission(permission);
    }

    private boolean checkArgs(String[] args) {
        if (MIN_ARGS == -1 && MAX_ARGS == -1) return true;

        if (args.length < MIN_ARGS) return false;
        return !(args.length > MAX_ARGS);
    }

    protected void sendMessage(Player sender, String message) {
        if (message == null) return;

        sender.sendMessage(Utils.addColour(message));
    }
}
