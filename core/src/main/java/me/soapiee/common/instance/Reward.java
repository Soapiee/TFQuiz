package me.soapiee.common.instance;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.hooks.VaultHook;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Reward {

    private final MessageManager messageManager;
    private VaultHook vaultHook;
    @Getter private final RewardType type;
    @Getter private final String message;
    @Getter private ArrayList<ItemStack> itemList;
    @Getter private ArrayList<String> permissionList;
    @Getter private ArrayList<String> commandsList;
    @Getter private int xpAmount;
    @Getter private double moneyAmount;

    public Reward(TFQuiz main, RewardType type, String message) {
        this.messageManager = main.getMessageManager();
        this.type = type;
        this.message = message;
    }

    //Currency Type
    public Reward(TFQuiz main, RewardType type, String message, double amount) {
        this(main, type, message);
        this.vaultHook = main.getVaultHook();
        this.moneyAmount = amount;
    }

    //XP Type
    public Reward(TFQuiz main, RewardType type, String message, int amount) {
        this(main, type, message);
        this.xpAmount = amount;
    }

    //Item Type
    public Reward(TFQuiz main, RewardType type, String message, List<ItemStack> list) {
        this(main, type, message);
        this.itemList = (ArrayList<ItemStack>) list;
    }

    //Command + Permission Type
    public Reward(TFQuiz main, RewardType type, String message, ArrayList<String> list) {
        this(main, type, message);
        switch (type) {
            case PERMISSION:
                this.vaultHook = main.getVaultHook();
                this.permissionList = list;
                break;
            case COMMAND:
                this.commandsList = list;
                break;
        }
    }

    public void give(Player player) {
        switch (type) {
            case PERMISSION:
                for (String permission : getPermissionList()) {
                    vaultHook.setPermission(player, permission);
                }
                break;
            case CURRENCY:
                vaultHook.deposit(player, getMoneyAmount());
                break;
            case EXPERIENCE:
                player.giveExp(getXpAmount());
                break;
            case ITEM:
                for (ItemStack item : getItemList()) {
                    if (Utils.hasFreeSpace(item.getType(), item.getAmount(), player)) {
                        player.getInventory().addItem(item);
                    } else {
                        player.getLocation().getWorld().dropItem(player.getLocation(), item);
                        player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEITEMWINERROR)));
                        return;
                    }
                }
                break;
            case COMMAND:
                for (String command : getCommandsList()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
                break;
            case NONE:
                break;
        }
        if (getMessage() != null) player.sendMessage(Utils.addColour(getMessage()));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 1;

        switch (type) {
            case COMMAND:
                builder.append(type.toString().toLowerCase()).append("s: ");
                for (String permission : commandsList) {
                    builder.append(permission);
                    if (commandsList.size() > i) builder.append(", ");
                    i++;
                }
                break;
            case PERMISSION:
                builder.append(type.toString().toLowerCase()).append("s: ");
                for (String permission : permissionList) {
                    builder.append(permission);
                    if (permissionList.size() > i) builder.append(", ");
                    i++;
                }
                break;
            case ITEM:
                for (ItemStack item : itemList) {
                    builder.append(item.getAmount()).append(" ").append(item.getType().toString().toLowerCase().replace("_", " "));
                    if (itemList.size() > i) builder.append(", ");
                    i++;
                }
                break;
            case CURRENCY:
                builder.append(moneyAmount);
                if (vaultHook != null) builder.append(vaultHook.getCurrencyName());
                break;
            case EXPERIENCE:
                builder.append(xpAmount).append(" exp");
                break;
        }

        return builder.toString();
    }
}
