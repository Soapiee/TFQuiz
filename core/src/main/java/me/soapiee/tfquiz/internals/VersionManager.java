package me.soapiee.tfquiz.internals;

import lombok.Getter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.managers.SettingsManager;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.Message;
import me.soapiee.tfquiz.utils.MessageManager;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.ChatColor;

public class VersionManager {

    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    @Getter private final SpectatorHandler spectatorHandler;
    @Getter private final SignHandler signHandler;
    @Getter private final HologramHandler hologramHandler;

    public VersionManager(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        SettingsManager settingsManager = main.getSettingsManager();

        spectatorHandler = registerSpectatorHandler(settingsManager);
        signHandler = registerSignHandler(settingsManager);
        hologramHandler = registerHologramHandler(settingsManager);
    }

    private SpectatorHandler registerSpectatorHandler(SettingsManager settingsManager) {
        SpectatorHandler handler;

        try {
            String version = Utils.SERVER_VERSION;
            String packageName = VersionManager.class.getPackage().getName();

            if (Utils.getMajorVersion() == 26) version = "26_1";
            String className = NMSVersion.valueOf("v" + version).getNmsClass();

            handler = (SpectatorHandler) Class.forName(packageName + "." + className).newInstance();

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

    private HologramHandler registerHologramHandler(SettingsManager settingsManager) {
        HologramHandler hologramHandler;

        try {
            String packageName = VersionManager.class.getPackage().getName();

            String className;
            int majorVersion = Utils.getMajorVersion();
            int minorVersion = Utils.getMinorVersion();

            if (majorVersion > 19) className = "HologramHandler_1_19_4";
            else
                className = (majorVersion == 19 && minorVersion > 3) ? "HologramHandler_1_19_4" : "HologramHandler_Legacy";

            if (settingsManager.isDebugMode()) Utils.consoleMsg(ChatColor.BLUE + className);
            hologramHandler = (HologramHandler) Class.forName(packageName + "." + className).newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException | IllegalArgumentException ex) {
            customLogger.logToFile(ex, messageManager.get(Message.UNSUPPORTEDVERSION));
            hologramHandler = new HologramHandler_Legacy();
        }

        return hologramHandler;
    }
}
