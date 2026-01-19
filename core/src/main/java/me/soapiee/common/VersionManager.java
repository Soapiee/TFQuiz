package me.soapiee.common;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.VersionProvider;
import me.soapiee.common.versionsupport.Version_Unsupported;
import org.bukkit.block.Sign;

public class VersionManager {

    private VersionProvider provider;

    public VersionManager(MessageManager messageManager, CustomLogger customLogger) {
        try {
            String packageName = VersionManager.class.getPackage().getName();
            int version = Utils.getMajorVersion();

            String providerName;
            if (version <= 19) providerName = "v1_19_Sign";
            else providerName = "v1_20_Sign";

            provider = (VersionProvider) Class.forName(packageName + "." + providerName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException exception) {
            customLogger.logToFile(exception, messageManager.get(Message.UNSUPPORTEDVERSION));
            provider = new Version_Unsupported(messageManager, customLogger);
        }
    }

    public void setText(Sign sign, int lineNo, String text) {
        provider.setLine(sign, lineNo, text);
    }
}
