package me.soapiee.common.versionsupport;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Logger;
import org.bukkit.block.Sign;

public class Version_Unsupported implements VersionProvider {

    private final Logger logger;
    private final MessageManager messageManager;

    public Version_Unsupported(MessageManager messageManager, Logger logger) {
        this.logger = logger;
        this.messageManager = messageManager;
    }

    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        logger.logToFile(null, messageManager.get(Message.SIGNSUNSUPPORTEDVERSION));
    }
}
