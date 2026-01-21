package me.soapiee.common.versionsupport;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import org.bukkit.block.Sign;

public class Sign_Unsupported implements SignProvider {

    private final CustomLogger logger;
    private final MessageManager messageManager;

    public Sign_Unsupported(MessageManager messageManager, CustomLogger logger) {
        this.logger = logger;
        this.messageManager = messageManager;
    }

    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        logger.logToFile(null, messageManager.get(Message.SIGNSUNSUPPORTEDVERSION));
    }
}
