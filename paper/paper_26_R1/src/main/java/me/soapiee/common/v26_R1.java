package me.soapiee.common;

import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.utils.MessageManager;
import me.soapiee.common.versionsupport.NMSProvider;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

class v26_R1 implements NMSProvider {

    private CustomLogger customLogger;
    private MessageManager messageManager;

    @Override
    public void initialise(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    @Override
    public boolean setSpectator(Player player) {
//        player.setGameMode(GameMode.SPECTATOR);
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
