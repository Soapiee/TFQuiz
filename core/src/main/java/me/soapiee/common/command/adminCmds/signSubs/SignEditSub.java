package me.soapiee.common.command.adminCmds.signSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SignEditSub extends AbstractAdminSub {

    private final String IDENTIFIER = "signedit";

    public SignEditSub(TFQuiz main) {
        super(main, "tfquiz.admin.signs", 5, 100);
    }

    // /tf sign edit <ID> <lineNo> "text..."
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        int lineNumber = getLine(sender, args[3]);
        if (lineNumber == -1) return;

        GameSign sign = getSign(sender, args[2]);
        if (sign == null) return;

        StringBuilder builder = new StringBuilder();
        int a = 4;
        for (int i = a; i <= args.length - 1; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) builder.append(" ");
        }

        sign.update((lineNumber - 1), builder.toString());
        gameSignManager.saveNewText(sign);

        sendMessage(sender, messageManager.getWithPlaceholder(Message.SIGNEDITED, sign.getSignID()));
    }

    private int getLine(CommandSender sender, String value) {
        int lineNo;
        try {
            lineNo = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.INVALIDNUMBER, value));
            return -1;
        }

        if (lineNo < 1 || lineNo > 4) {
            sender.sendMessage(Utils.addColour(messageManager.get(Message.SIGNINVALIDLINENUM)));
            return -1;
        }

        return lineNo;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
