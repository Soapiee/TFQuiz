package me.soapiee.common.instance.rewards.types;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemReward extends Reward {

    private final ArrayList<ItemStack> itemList;

    public ItemReward(TFQuiz main, String message, ArrayList<ItemStack> itemList) {
        super(RewardType.ITEM, main.getMessageManager(), message);
        this.itemList = itemList;
    }

    @Override
    public void give(Player player) {
        boolean invFull = false;
        for (ItemStack item : itemList) {
            if (Utils.hasFreeSpace(item.getType(), item.getAmount(), player)) {
                player.getInventory().addItem(item);
            } else {
                player.getLocation().getWorld().dropItem(player.getLocation(), item);
                invFull = true;
            }
        }

        if (invFull) player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEITEMWINERROR)));
        else super.give(player);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 1;

        for (ItemStack item : itemList) {
            int amount = item.getAmount();
            builder.append(amount).append(" ")
                    .append(Utils.capitalise(item.getType().toString()))
                    .append((amount > 1 ? "s" : ""));
            if (itemList.size() > i) builder.append(", ");
            i++;
        }

        return builder.toString();
    }
}
