package me.soapiee.tfquiz.command.adminCmds;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.utils.Message;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class VersionSub extends AbstractAdminSub {

    private final String IDENTIFIER = "version";

    public VersionSub(TFQuiz main) {
        super(main, null, 1, 1);
    }

    // /tf version
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        sendMessage(sender, messageManager.getWithPlaceholder(Message.ADMINVERSION, Utils.PLUGIN_VERSION));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
