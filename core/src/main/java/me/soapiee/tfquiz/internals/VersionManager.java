package me.soapiee.tfquiz.internals;

import lombok.Getter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.enums.Message;
import me.soapiee.tfquiz.managers.SettingsManager;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.ChatColor;

public class VersionManager {

    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    @Getter private final SpectatorHandler spectatorHandler;
    @Getter private final SignHandler signHandler;

//    private final Set<UUID> spectators = new HashSet<>();

    public VersionManager(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        SettingsManager settingsManager = main.getSettingsManager();

        spectatorHandler = registerSpectatorHandler(settingsManager);
        signHandler = registerSignHandler(settingsManager);
    }

    private SpectatorHandler registerSpectatorHandler(SettingsManager settingsManager) {
        SpectatorHandler handler;

        try {
            String version = Utils.VERSION;
            String packageName = VersionManager.class.getPackage().getName();

            if (Utils.getMajorVersion() == 26) version = "26_1";
            String className = NMSVersion.valueOf("v" + version).getNmsClass();

            if (Utils.getMajorVersion() == 26 && Utils.IS_PAPER) {
                handler = new Spectator_Unsupported();
                Utils.consoleMsg(ChatColor.RED
                        + "The Spectator mode for MC version 26.1+ is currently not supported. Please update the plugin if one is available, or be patient whilst I work on a fix :)");
            } else {
                handler = (SpectatorHandler) Class.forName(packageName + "." + className).newInstance();
            }

            if (settingsManager.isDebugMode()) Utils.consoleMsg(ChatColor.BLUE + "Spec version: " + className);
            handler.initialise(messageManager, customLogger);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException |
                 IllegalArgumentException ex) {
            customLogger.logToFile(ex, messageManager.get(Message.DISABLESPECWARNING));
            handler = new Spectator_Unsupported();

            if (settingsManager.isDebugMode()) customLogger.logToFile(ex, "");
        }

        return handler;
    }

    private SignHandler registerSignHandler(SettingsManager settingsManager) {
        SignHandler handler;

        try {
            String packageName = VersionManager.class.getPackage().getName();
            int version = Utils.getMajorVersion();

            String className;
            if (version <= 19) className = "v1_16_Sign";
            else className = "v1_20_Sign";

            if (settingsManager.isDebugMode()) Utils.consoleMsg(ChatColor.BLUE + "Signs version: " + className);
            handler = (SignHandler) Class.forName(packageName + "." + className).newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException exception) {
            customLogger.logToFile(exception, messageManager.get(Message.UNSUPPORTEDVERSION));
            handler = new Sign_Unsupported(messageManager, customLogger);
        }

        return handler;
    }

//    public boolean setSpectator(Player player) {
//        if (spectatorHandler.setSpectator(player)) {
//            spectators.add(player.getUniqueId());
//            return true;
//        }
//        return false;
//    }

//    public void unSetSpectator(Player player) {
//        spectatorHandler.unSetSpectator(player);
//        spectators.remove(player.getUniqueId());
//        new GamemodeChange(player).runTaskLater(main, 1);
//    }

//    public boolean spectatorsExist() {
//        return !spectators.isEmpty();
//    }

//    public void updateTab(Player player) {
//        new TabUpdate(spectatorHandler, player, spectators).runTaskLater(main, 10);
//    }

}
