package me.soapiee.common.command;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.*;
import me.soapiee.common.command.adminCmds.gameSubs.*;
import me.soapiee.common.command.adminCmds.signSubs.*;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GameManager;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.PlayerCache;
import me.soapiee.common.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final MessageManager messageManager;
    private final GameManager gameManager;
    private final PlayerCache playerCache;

    private final String PERMISSION = "tfquiz.admin.*";
    private final Map<String, SubCmd> subCommands = new HashMap<>();

    public AdminCommand(TFQuiz main) {
        this.messageManager = main.getMessageManager();
        this.gameManager = main.getGameManager();
        this.playerCache = main.getPlayerCache();

        register(new ReloadSub(main));
        register(new SetspawnSub(main));
        register(new ListAdminSub(main));
        register(new RemoveholosSub(main));
        register(new SpecSub(main));
        register(new UnspecSub(main));
        register(new GameSetSpawnSub(main));
        register(new GameSetHoloSpawnSub(main));
        register(new GameOpenSub(main));
        register(new GameCloseSub(main));
        register(new GameStartSub(main));
        register(new GameEndSub(main));
        register(new GameAddPlayerSub(main));
        register(new GameRemovePlayerSub(main));
        register(new GameInfoSub(main));
        register(new SignAddSub(main));
        register(new SignRemoveSub(main));
        register(new SignEditSub(main));
        register(new SignListSub(main));
        register(new SignTeleportSub(main));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) return true;

        if (args.length == 0) {
            sendMessage(sender, messageManager.get(Message.ADMINCMDUSAGE));
            return true;
        }

        SubCmd cmd = subCommands.get(getSubCmd(args));
        if (cmd == null) {
            sendHelpMsg(sender, label, args);
            return true;
        }

        cmd.execute(sender, label, args);
        return true;
    }

    private void sendHelpMsg(CommandSender sender, String label, String[] args) {
        Message helpMessage = Message.ADMINCMDUSAGE;

        if (args.length >= 1 && args[0].equalsIgnoreCase("game")) helpMessage = Message.GAMEADMINCMDUSAGE;
        if (args.length >= 1 && args[0].equalsIgnoreCase("sign")) helpMessage = Message.SIGNADMINCMDUSAGE;

        sendMessage(sender, messageManager.getWithPlaceholder(helpMessage, label));
    }

    private void register(SubCmd cmd) {
        subCommands.put(cmd.getIDENTIFIER(), cmd);
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

    private String getSubCmd(String[] args) {
        if (args[0].equalsIgnoreCase("game")) return getGameSub(args);

        if (args[0].equalsIgnoreCase("sign")) return getSignSub(args);

        return args[0].toLowerCase();
    }

    private String getGameSub(String[] args) {
        if (args.length == 1) return null;
        if (args.length == 2) return "gameadd";

        return args[0].toLowerCase() + args[2].toLowerCase();
    }

    private String getSignSub(String[] args) {
        if (args.length == 1) return null;

        return args[0] + args[1];
    }

    @Override
//      Usage: /tf <list|setspawn>
//      Usage: /tf game <game ID> <open|close|add|remove|setspawn>
//      Usage: /tf sign <sign ID> <list|add|remove|edit> <lineNo> text...
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> results = new ArrayList<>();

        switch (args.length) {
            case 1:
                if (sender instanceof Player && !sender.hasPermission("tfquiz.admin.*")) break;
                results.add("help");
                results.add("list");
                results.add("setspawn");
                results.add("game");
                if (sender instanceof Player && sender.hasPermission("tfquiz.admin.signs")) results.add("sign");
                if (sender instanceof Player && sender.hasPermission("tfquiz.reload")) results.add("reload");
                break;
            case 2:
                if (args[0].equalsIgnoreCase("sign") && sender.hasPermission("tfquiz.admin.signs")) {
                    results.add("add");
                    results.add("remove");
                    results.add("edit");
                    results.add("list");
                }
                if (args[0].equalsIgnoreCase("game") && sender.hasPermission("tfquiz.admin.*")) {
                    for (Game game : gameManager.getGames()) {
                        results.add(String.valueOf(game.getIdentifier()));
                    }
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("game") && sender.hasPermission("tfquiz.admin.*")) {
                    results.add("setspawn");
                    results.add("setholospawn");
                    results.add("open");
                    results.add("close");
                    results.add("start");
                    results.add("end");
                    results.add("addPlayer");
                    results.add("removePlayer");
                    results.add("info");
                }
                break;
            case 4:
                if (args[2].equalsIgnoreCase("addplayer") || args[2].equalsIgnoreCase("removeplayer")) {
                    for (final OfflinePlayer player : playerCache.getList()) {
                        results.add(player.getName().toLowerCase());
                    }
                } else if (args[2].equalsIgnoreCase("end")) results.add("-without");
                break;
        }
        return results.stream().filter(completion -> completion.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }
}
