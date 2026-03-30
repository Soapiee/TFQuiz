package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.TFQuiz;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface NMSProvider {

    default void initialise(TFQuiz main) {
    }

    boolean setSpectator(Player player);

    void unSetSpectator(Player player);

    void updateTab(Player player, Set<UUID> spectators);
}
