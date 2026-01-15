package me.soapiee.common;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.SettingsManager;
import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class SpectatorManager {

    private NMSProvider provider;
    private final TFQuiz main;
    private final HashSet<UUID> spectators;

    public SpectatorManager(TFQuiz main) {
        this.main = main;
        SettingsManager settingsManager = main.getSettingsManager();
        spectators = new HashSet<>();

        try {
            String version = Utils.VERSION;
            String packageName = SpectatorManager.class.getPackage().getName();
            String providerName = NMSVersion.valueOf("v" + version).getProvider();

            provider = (NMSProvider) Class.forName(packageName + "." + providerName).newInstance();
            provider.initialise(main);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException | IllegalArgumentException ex) {
            main.getCustomLogger().logToFile(ex, main.getMessageManager().get(Message.DISABLESPECWARNING));
            provider = new NMS_Unsupported();

            if (settingsManager.isDebugMode()) main.getCustomLogger().logToFile(ex, "");
        }
    }

    public boolean setSpectator(Player player) {
        if (provider.setSpectator(player)) {
            spectators.add(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void unSetSpectator(Player player) {
        provider.unSetSpectator(player);
        spectators.remove(player.getUniqueId());
        new GamemodeChange(player).runTaskLater(main, 1);
    }

    public boolean spectatorsExist() {
        return !spectators.isEmpty();
    }

    public void updateTab(Player player) {
        new TabUpdate(provider, player, spectators).runTaskLater(main, 10);
    }
}
