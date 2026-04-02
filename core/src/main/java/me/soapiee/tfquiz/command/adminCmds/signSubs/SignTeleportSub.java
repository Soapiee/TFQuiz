package me.soapiee.tfquiz.command.adminCmds.signSubs;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.command.adminCmds.AbstractAdminSub;
import me.soapiee.tfquiz.instance.GameSign;
import me.soapiee.tfquiz.tasks.TeleportTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SignTeleportSub extends AbstractAdminSub {

    private final String IDENTIFIER = "signteleport";

    public SignTeleportSub(TFQuiz main) {
        super(main, "tfquiz.admin.signs", 3, 3);
    }

    // /tf sign teleport <signID>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, true)) return;

        GameSign sign = getSign(sender, args[2]);
        if (sign == null) return;

        Player player = (Player) sender;
        new TeleportTask(player, sign.getLocation()).runTaskLater(main, 1);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
