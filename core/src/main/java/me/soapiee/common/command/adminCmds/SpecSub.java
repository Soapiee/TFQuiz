package me.soapiee.common.command.adminCmds;

import me.soapiee.common.TFQuiz;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpecSub extends AbstractAdminSub {

    private final String IDENTIFIER = "spec";

    public SpecSub(TFQuiz main) {
        super(main, null, 2, 2);
    }

    // /tf spec <player>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!main.isDebugMode()) return;
        if (!checkRequirements(sender, label, args)) return;

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) return;

        main.getSpecManager().setSpectator(player);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
