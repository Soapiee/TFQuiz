package me.soapiee.common.command;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.playerCmds.JoinSub;
import me.soapiee.common.command.playerCmds.LeaveSub;
import me.soapiee.common.command.playerCmds.ListSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerCommand implements CommandExecutor, TabCompleter {

    private final TFQuiz main;
    private final MessageManager messageManager;

    private final String PERMISSION = "tfquiz.player";
    private final Map<String, SubCmd> subCommands = new HashMap<>();

    public PlayerCommand(TFQuiz main) {
        this.main = main;
        this.messageManager = main.getMessageManager();

        register(new JoinSub(main));
        register(new LeaveSub(main));
        register(new ListSub(main));
    }

    // /game join|leave <gameID>
    // /game list
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (isConsole(sender)) return true;
        if (!hasPermission(sender)) return true;

        if (args.length == 0) {
            sendMessage(sender, messageManager.get(Message.GAMECCMDUSAGE));
            return true;
        }

        SubCmd cmd = subCommands.get(args[0]);
        if (cmd == null) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.GAMECCMDUSAGE, label));
            return true;
        }

        cmd.execute(sender, label, args);
        return true;
    }

    private void register(SubCmd cmd) {
        subCommands.put(cmd.getIDENTIFIER(), cmd);
    }

    private boolean isConsole(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sendMessage(sender, messageManager.get(Message.CONSOLEUSAGEERROR));
            return true;
        }

        return false;
    }

    private boolean hasPermission(CommandSender sender) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (!player.hasPermission(PERMISSION)) sendMessage(player, messageManager.get(Message.NOPERMISSION));

        return player.hasPermission(PERMISSION);
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message == null) return;

        if (sender instanceof Player) sender.sendMessage(Utils.addColour(message));
        else Utils.consoleMsg(message);
    }

    // /game join|leave <gameID>
    // /game list
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> results = new ArrayList<>();
        switch (args.length) {
            case 1:
                results.add("help");

                if (sender instanceof Player && sender.hasPermission("tfquiz.player.list")) {
                    results.add("list");
                }
                if (sender instanceof Player && sender.hasPermission("tfquiz.player.join")) {
                    results.add("join");
                    results.add("leave");
                }
                break;
            case 2:
                if (sender instanceof Player && !sender.hasPermission("tfquiz.player.join")) break;
                if (args[0].equals("join")) {
                    for (Game game : this.main.getGameManager().getGames()) {
                        results.add(String.valueOf(game.getID()));
                    }
                }
                break;
        }
        return results.stream().filter(completion -> completion.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }
}
