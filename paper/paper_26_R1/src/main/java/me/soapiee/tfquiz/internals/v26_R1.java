package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.utils.Message;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

class v26_R1 implements SpectatorHandler {

    private CustomLogger customLogger;
    private MessageManager messageManager;

    @Override
    public void initialise(MessageManager messageManager, CustomLogger customLogger) {
        this.customLogger = customLogger;
        this.messageManager = messageManager;
    }

    @Override
    public boolean setSpectator(Player player) {
        customLogger.logToPlayer(Bukkit.getConsoleSender(), null, "&cThe spectator system is currently unsupported on this version with Paper");
        if (!Utils.IS_PAPER)
            customLogger.logToPlayer(Bukkit.getConsoleSender(), null, messageManager.get(Message.DOWNLOADSPIGOTJAR));
        return true;
    }

    @Override
    public void unSetSpectator(Player player) {
//        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void updateTab(org.bukkit.entity.Player player, Set<UUID> spectators) {

    }
}
