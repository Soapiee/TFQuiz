package me.soapiee.tfquiz.command.adminCmds;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.internals.GamemodeChange;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UnspecSub extends AbstractAdminSub {

    private final String IDENTIFIER = "unspec";

    public UnspecSub(TFQuiz main) {
        super(main, null, 2, 2);
    }

    // /tf unspec <player>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!settingsManager.isDebugMode()) return;
        if (!checkRequirements(sender, label, args)) return;

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) return;

        spectatorHandler.unSetSpectator(player);
        gamePlayerManager.removeFakeSpectator(player.getUniqueId());
        new GamemodeChange(player).runTaskLater(main, 1);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
