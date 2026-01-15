package me.soapiee.common.instance.rewards.types;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.instance.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandReward extends Reward {

    private final ArrayList<String> commandList;

    public CommandReward(TFQuiz main, String message, ArrayList<String> commandList) {
        super(RewardType.COMMAND, main.getMessageManager(), message);
        this.commandList = commandList;
    }

    @Override
    public void give(Player player) {
        for (String command : commandList) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        super.give(player);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 1;

        builder.append(type.toString().toLowerCase()).append("s: ");
        for (String permission : commandList) {
            builder.append(permission);
            if (commandList.size() > i) builder.append(", ");
            i++;
        }
        return builder.toString();
    }
}
