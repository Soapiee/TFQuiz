package me.soapiee.tfquiz.instance.rewards;

import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.entity.Player;

public interface RewardInterface {

    String getMessage();

    default void give(Player player) {
        if (getMessage() != null) player.sendMessage(Utils.addColour(getMessage()));
    }

    String toString();
}
