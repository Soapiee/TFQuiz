package me.soapiee.common.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

    public static final boolean IS_PAPER = detectPaper();
    public static final String VERSION = getVersion();

    public static int getMinorVersion() {
        String[] parts = VERSION.split("_");

        if (parts[0].equalsIgnoreCase("1")) return Integer.parseInt(parts[2]);
        return Integer.parseInt(parts[1]);
    }

    public static int getMajorVersion() {
        String[] parts = VERSION.split("_");

        if (parts[0].equalsIgnoreCase("1")) return Integer.parseInt(parts[1]);
        return Integer.parseInt(parts[0]);
    }

    public static String getVersion() {
        return Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
    }

    public static void consoleMsg(String message) {
        String prefix = "[" + Bukkit.getServer().getPluginManager().getPlugin("TrueFalseQuiz").getDescription().getPrefix() + "]";
        Bukkit.getConsoleSender().sendMessage(prefix + " " + addColour(message));
    }

    private static boolean detectPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ClientOption");
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }

    public static String addColour(String message) {
        Matcher matcher = Pattern.compile("#([A-Fa-f0-9]{6})").matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static boolean hasFreeSpace(Material type, int amount, Player player) {
        Inventory inv = player.getInventory();
        int items = 0;
        for (ItemStack item : inv.getStorageContents())
            try {
                if (item == null) {
                    items += type.getMaxStackSize();
                } else if (item.getType() == type) {
                    int stackAmount = item.getAmount();
                    items += type.getMaxStackSize() - stackAmount;
                }
            } catch (NullPointerException ignored) {
            }
        return items > amount;
    }

    public static String capitalise(String string) {
        String[] stringParts = string.toLowerCase().split("_");

        StringBuilder builder = new StringBuilder();
        builder.append(stringParts[0].substring(0, 1).toUpperCase()).append(stringParts[0].substring(1));

        if (stringParts.length > 1) {
            for (int i = 1; i < stringParts.length; i++) {
                builder.append(" ").append(stringParts[i].substring(0, 1).toUpperCase()).append(stringParts[i].substring(1));
            }
        }
        return builder.toString();
    }
}
