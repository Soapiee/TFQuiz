package me.soapiee.common.command.adminCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.SubCmd;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.manager.*;
import me.soapiee.common.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractAdminSub implements SubCmd {

    protected final TFQuiz main;
    protected final MessageManager messageManager;
    protected final GameManager gameManager;
    protected final GameSignManager gameSignManager;
    protected final SettingsManager settingsManager;
    protected final SchedulerManager schedulerManager;

    protected final String PERMISSION;
    protected final int MIN_ARGS;
    protected final int MAX_ARGS;

    public AbstractAdminSub(TFQuiz main, String PERMISSION, int MIN_ARGS, int MAX_ARGS) {
        this.main = main;
        this.messageManager = main.getMessageManager();
        this.gameManager = main.getGameManager();
        this.settingsManager = main.getSettingsManager();
        this.gameSignManager = main.getGameSignManager();
        this.schedulerManager = main.getSchedulerManager();

        this.PERMISSION = PERMISSION;
        this.MIN_ARGS = MIN_ARGS;
        this.MAX_ARGS = MAX_ARGS;
    }

    public boolean checkRequirements(CommandSender sender, String label, String[] args) {
        if (!checkPermission(sender, PERMISSION)) {
            sendMessage(sender, messageManager.get(Message.NOPERMISSION));
            return false;
        }

        if (!checkArgs(args)) {
            Message message = Message.ADMINCMDUSAGE;
            switch ((args[0]).toLowerCase()) {
                case "reload":
                    message = Message.ADMINRELOADCMDUSAGE;
                    break;
                case "setspawn":
                    message = Message.ADMINSETLOBBYSPAWNCMDUSAGE;
                    break;
                case "list":
                    message = Message.ADMINLISTCMDUSAGE;
                    break;
                case "game":
                    message = Message.GAMEADMINCMDUSAGE;
                    break;
                case "sign":
                    message = Message.SIGNADMINCMDUSAGE;
                    break;
            }

            sendMessage(sender, messageManager.getWithPlaceholder(message, label));
            return false;
        }

        return true;
    }

    protected boolean checkPermission(CommandSender sender, String permission) {
        if (permission == null) return true;
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        return player.hasPermission(permission);
    }

    private boolean checkArgs(String[] args) {
        if (MIN_ARGS == -1 && MAX_ARGS == -1) return true;

        if (args.length < MIN_ARGS) return false;
        return !(args.length > MAX_ARGS);
    }

    protected boolean isConsole(CommandSender sender, boolean sendErrorMsg) {
        if (sender instanceof ConsoleCommandSender) {
            if (sendErrorMsg) sendMessage(sender, messageManager.get(Message.CONSOLEUSAGEERROR));
            return true;
        }

        return false;
    }

    protected void sendMessage(CommandSender sender, String message) {
        if (message == null) return;

        if (sender instanceof Player) sender.sendMessage(Utils.addColour(message));
        else Utils.consoleMsg(message);
    }

    protected Game getGame(CommandSender sender, String value) {
        int gameID = validateID(sender, value);
        if (gameID == -1) return null;

        Game game = gameManager.getGame(gameID);
        if (game == null) sendMessage(sender, messageManager.get(Message.GAMEINVALIDGAMEID));

        return game;
    }

    protected GameSign getSign(CommandSender sender, String value) {
        int signID = validateID(sender, value);
        if (signID == -1) return null;

        GameSign sign = gameSignManager.getSign(value);
        if (sign == null) sendMessage(sender, messageManager.get(Message.SIGNINVALIDSIGNID));

        return sign;
    }

    protected int validateID(CommandSender sender, String value) {
        int id;
        try {
            id = Integer.parseInt(value);
        } catch (NumberFormatException error) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.INVALIDNUMBER, value));
            return -1;
        }

        return id;
    }

    protected boolean gameHasSchedulder(CommandSender sender, Game game, Message errorMessage) {
        if (schedulerManager.hasScheduler(game)) {
            sendMessage(sender, messageManager.getWithPlaceholder(errorMessage, game));
            return true;
        }

        return false;
    }

    protected boolean gameIsInProgress(CommandSender sender, Game game, Message errorMessage) {
        if (game.getState() == GameState.LIVE || game.getState() == GameState.COUNTDOWN) {
            if (errorMessage != null) sendMessage(sender, messageManager.get(errorMessage));
            return true;
        }

        return false;
    }
}
