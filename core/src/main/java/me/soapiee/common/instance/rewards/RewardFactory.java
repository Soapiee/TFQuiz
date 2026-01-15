package me.soapiee.common.instance.rewards;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.hooks.VaultHook;
import me.soapiee.common.instance.rewards.types.*;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Logger;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RewardFactory {

    private final TFQuiz main;
    private final Logger customLogger;
    private final MessageManager messageManager;
    private final VaultHook vaultHook;
    private FileConfiguration config;

    public RewardFactory(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        vaultHook = main.getVaultHook();
        config = main.getConfig();
    }

    public Reward create(CommandSender sender, String gameID) {
        String path = "games." + gameID + ".reward.";
        RewardType rewardType = validateType(sender, path, gameID);

        if (!isRewardConfigured(sender, gameID)) rewardType = RewardType.NONE;

        switch (rewardType) {
            case CURRENCY:
                return createCurrencyReward(sender, path, gameID);

            case EXPERIENCE:
                return createExperienceReward(sender, path, gameID);

            case ITEM:
                return createItemReward(sender, path, gameID);

            case PERMISSION:
                return createPermissionReward(sender, path, gameID);

            case COMMAND:
                return createCommandReward(sender, path, gameID);

            default:
                return createNullReward();
        }
    }

    public void reload() {
        config = main.getConfig();
    }

    private Reward createCurrencyReward(CommandSender sender, String path, String gameID) {
        if (vaultHook == null) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDVAULTHOOK, gameID));
            return createNullReward();
        }

        String rawDouble = config.getString(path + "reward");
        double money = validateNumber(sender, gameID, rawDouble);
        if (money == -1) return createNullReward();

        if (money <= 0) {
            customLogger.logToPlayer(sender, null, messageManager.get(Message.INVALIDREWARDAMOUNT));
            return createNullReward();
        }

        String message = validateMessage(sender, path, gameID);
        return new CurrencyReward(main, message, money);
    }

    private Reward createExperienceReward(CommandSender sender, String path, String gameID) {
        String rawInt = config.getString(path + "reward");
        int experience = (int) validateNumber(sender, gameID, rawInt);
        if (experience == -1) return createNullReward();


        if (experience <= 0) {
            customLogger.logToPlayer(sender, null, messageManager.get(Message.INVALIDREWARDAMOUNT));
            return createNullReward();
        }

        String message = validateMessage(sender, path, gameID);
        return new ExperienceReward(main, message, experience);
    }

    private Reward createItemReward(CommandSender sender, String path, String gameID) {
        ArrayList<ItemStack> itemList = new ArrayList<>();
        String[] itemParts;
        Material material;
        int quantity;

        if (config.isString(path + "reward")) {
            itemParts = getItemParts(sender, gameID, config.getString(path + "reward"));
            if (itemParts == null) return createNullReward();

            material = validateMaterial(sender, gameID, itemParts[0]);
            quantity = (int) validateNumber(sender, gameID, itemParts[1]);

            if (material != null && quantity != -1) itemList.add(new ItemStack(material, quantity));

        } else if (config.isList(path + "reward")) {
            for (String rawItemString : config.getStringList(path + "reward")) {

                itemParts = getItemParts(sender, gameID, rawItemString);
                if (itemParts == null) continue;

                material = validateMaterial(sender, gameID, itemParts[0]);
                quantity = (int) validateNumber(sender, gameID, itemParts[1]);
                if (material == null || quantity == -1) continue;

                itemList.add(new ItemStack(material, quantity));
            }
        } else {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDITEM, gameID));
            return createNullReward();
        }

        if (itemList.isEmpty()) return createNullReward();

        String message = validateMessage(sender, path, gameID);
        return new ItemReward(main, message, itemList);
    }

    private Reward createPermissionReward(CommandSender sender, String path, String gameID) {
        if (vaultHook == null) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDVAULTHOOK, gameID));
            return createNullReward();
        }

        ArrayList<String> permissionList = new ArrayList<>();

        if (config.isString(path + "reward")) permissionList.add(config.getString(path + "reward"));

        if (config.isList(path + "reward")) permissionList.addAll(config.getStringList(path + "reward"));

        if (permissionList.isEmpty()) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARD, gameID));
            return createNullReward();
        }

        String message = validateMessage(sender, path, gameID);
        return new PermissionReward(main, message, permissionList);
    }

    private Reward createCommandReward(CommandSender sender, String path, String gameID) {
        ArrayList<String> commandList = new ArrayList<>();

        if (config.isString(path + "reward"))
            commandList.add(config.getString(path + "reward"));

        if (config.isList(path + "reward"))
            commandList.addAll(config.getStringList(path + "reward"));

        if (commandList.isEmpty()) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARD, gameID));
            return createNullReward();
        }


        String message = validateMessage(sender, path, gameID);
        return new CommandReward(main, message, commandList);
    }

    private Reward createNullReward() {
        return new NullReward(main);
    }

    private boolean isRewardConfigured(CommandSender sender, String gameID) {
        if (!config.isSet("games." + gameID + ".reward.reward")) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARD, gameID));
            return false;
        }

        return true;
    }

    private String validateMessage(CommandSender sender, String path, String gameID) {
        String message = config.getString(path + "message", null);

        if (message == null || message.isEmpty()) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDMESSAGE, gameID));
            return null;
        }

        return message;
    }

    private RewardType validateType(CommandSender sender, String path, String gameID) {
        RewardType rewardType;
        try {
            rewardType = RewardType.valueOf(config.getString(path + "type").toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            rewardType = RewardType.NONE;
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDTYPE, gameID));
        }

        return rewardType;
    }

    private String[] getItemParts(CommandSender sender, String gameID, String string) {
        String[] itemParts = string.split(", ");

        if (itemParts.length != 2) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDITEM, gameID));
            return null;
        }

        return itemParts;
    }

    private Material validateMaterial(CommandSender sender, String gameID, String stringMaterial) {
        Material material;
        try {
            material = Material.valueOf(stringMaterial.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException error) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDMATERIAL, gameID));
            material = null;
        }

        return material;
    }

    private double validateNumber(CommandSender sender, String gameID, String stringAmount) {
        double amount;
        try {
            amount = Double.parseDouble(stringAmount);
        } catch (NumberFormatException | NullPointerException error) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDREWARDNUMBER, gameID));
            amount = -1;
        }

        return amount;
    }

}
