package me.soapiee.common;

import me.soapiee.common.versionsupport.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class SpectatorManager {

    private NMSProvider provider;
    private final TFQuiz main;
    private final HashSet<UUID> spectators;

    public SpectatorManager(TFQuiz main) {
        this.main = main;
        spectators = new HashSet<>();

        try {
            String version = Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
            String packageName = SpectatorManager.class.getPackage().getName();
            String providerName = NMSVersion.valueOf("v" + version).getProvider();
//            Utils.consoleMsg(ChatColor.GOLD + packageName + "." + providerName);

            provider = (NMSProvider) Class.forName(packageName + "." + providerName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 ClassCastException | IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[TFQuiz] Unsupported NMS version detected. The Spectator system will be diminished. Its recommended that you disable it in the config" +
                    "\nAll other features will work as normal");
            provider = new NMS_Unsupported();

            if (main.isDebugMode()) main.getCustomLogger().logToFile(ex, "");
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
