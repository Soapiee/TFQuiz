package me.soapiee.common;

import me.soapiee.common.utils.Logger;
import me.soapiee.common.versionsupport.VersionProvider;
import me.soapiee.common.versionsupport.Version_Unsupported;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;

public class VersionManager {

    private VersionProvider provider;
    private final Logger logger;

    public VersionManager(Logger logger) {
        this.logger = logger;
        try {
            String packageName = VersionManager.class.getPackage().getName();
            int version = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
//            int version = Utils.getMajorVersion();

            String providerName;
            if (version <= 19) providerName = "v1_19_Sign";
            else providerName = "v1_20_Sign";

            provider = (VersionProvider) Class.forName(packageName + "." + providerName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException exception) {
            logger.logToFile(exception, "Unsupported version detected. Plugin may not function correctly. " +
                    "Contact the developer to get your version supported");
            provider = new Version_Unsupported(logger);
        }
    }

    public void setText(Sign sign, int lineNo, String text) {
        provider.setLine(sign, lineNo, text);
    }
}
