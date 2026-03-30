package me.soapiee.tfquiz.internals;

import lombok.Getter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.enums.Message;
import me.soapiee.tfquiz.managers.SettingsManager;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VersionManager {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final NMSProvider NMSProvider;
    @Getter private final SignHandler signHandler;

    private final Set<UUID> spectators = new HashSet<>();

    public VersionManager(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        SettingsManager settingsManager = main.getSettingsManager();

        NMSProvider = registerSpectatorHandler(settingsManager);
        signHandler = registerSignHandler(settingsManager);
    }

    private NMSProvider registerSpectatorHandler(SettingsManager settingsManager) {
        NMSProvider provider;

        try {
            String version = Utils.VERSION;
            String packageName = VersionManager.class.getPackage().getName();
            String nmsClassName = NMSVersion.valueOf("v" + version).getNmsClass();

            if (Utils.getMajorVersion() == 26 && Utils.IS_PAPER) {
                provider = new NMS_Unsupported();
                Utils.consoleMsg(ChatColor.BLUE
                        + "The Spectator mode for MC version 26.1+ is currently not supported. Please update the plugin if one is available, or be patient whilst I work on a fix :)");
            } else {
                provider = (NMSProvider) Class.forName(packageName + "." + nmsClassName).newInstance();
            }

            if (settingsManager.isDebugMode()) Utils.consoleMsg(ChatColor.BLUE + "NMS Provider: " + nmsClassName);
            provider.initialise(main);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException | IllegalArgumentException ex) {
            main.getCustomLogger().logToFile(ex, main.getMessageManager().get(Message.DISABLESPECWARNING));
            provider = new NMS_Unsupported();

            if (settingsManager.isDebugMode()) main.getCustomLogger().logToFile(ex, "");
        }

        return provider;
    }

    private SignHandler registerSignHandler(SettingsManager settingsManager) {
        SignHandler provider;

        try {
            String packageName = VersionManager.class.getPackage().getName();
            int version = Utils.getMajorVersion();

            String providerName;
            if (version <= 19) providerName = "v1_16_Sign";
            else providerName = "v1_20_Sign";

            if (settingsManager.isDebugMode()) Utils.consoleMsg(ChatColor.BLUE + "Signs version: " + providerName);
            provider = (SignHandler) Class.forName(packageName + "." + providerName).newInstance();
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

}
