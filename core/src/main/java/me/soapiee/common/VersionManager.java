package me.soapiee.common;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.manager.SettingsManager;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class VersionManager {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final NMSProvider NMSProvider;
    private final SignProvider signProvider;

    private final HashSet<UUID> spectators;

    public VersionManager(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();

        spectators = new HashSet<>();
        NMSProvider = getNMSProvider();
        signProvider = getSignProvider();
    }

    private NMSProvider getNMSProvider() {
        SettingsManager settingsManager = main.getSettingsManager();
        NMSProvider provider;

        try {
            String version = Utils.VERSION;
            String packageName = VersionManager.class.getPackage().getName();
            String providerName = NMSVersion.valueOf("v" + version).getProvider();

            provider = (NMSProvider) Class.forName(packageName + "." + providerName).newInstance();
            provider.initialise(main);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException | IllegalArgumentException ex) {
            main.getCustomLogger().logToFile(ex, main.getMessageManager().get(Message.DISABLESPECWARNING));
            provider = new NMS_Unsupported();

            if (settingsManager.isDebugMode()) main.getCustomLogger().logToFile(ex, "");
        }

        return provider;
    }

    private SignProvider getSignProvider() {
        SignProvider provider;

        try {
            String packageName = VersionManager.class.getPackage().getName();
            int version = Utils.getMajorVersion();

            String providerName;
            if (version <= 19) providerName = "v1_19_Sign";
            else providerName = "v1_20_Sign";

            provider = (SignProvider) Class.forName(packageName + "." + providerName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException exception) {
            customLogger.logToFile(exception, messageManager.get(Message.UNSUPPORTEDVERSION));
            provider = new Sign_Unsupported(messageManager, customLogger);
        }

        return provider;
    }

    public boolean setSpectator(Player player) {
        if (NMSProvider.setSpectator(player)) {
            spectators.add(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void unSetSpectator(Player player) {
        NMSProvider.unSetSpectator(player);
        spectators.remove(player.getUniqueId());
        new GamemodeChange(player).runTaskLater(main, 1);
    }

    public boolean spectatorsExist() {
        return !spectators.isEmpty();
    }

    public void updateTab(Player player) {
        new TabUpdate(NMSProvider, player, spectators).runTaskLater(main, 10);
    }

    public void setText(Sign sign, int lineNo, String text) {
        signProvider.setLine(sign, lineNo, text);
    }
}
