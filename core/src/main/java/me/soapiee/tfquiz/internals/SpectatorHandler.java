package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface SpectatorHandler {

    default void initialise(MessageManager messageManager, CustomLogger customLogger) {
    }

    boolean setSpectator(Player player);

    void unSetSpectator(Player player);

    void updateTab(Player player, Set<UUID> spectators);
}
