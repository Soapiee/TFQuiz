package me.soapiee.common.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    // 1.21.10       ||        26.1.0
    public static String getMinorVersion() {
        String version = "v" + Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
        return version.split("_")[2];
    }

    public static int getMajorVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }

    public static void consoleMsg(String message) {
        String prefix = "[" + Bukkit.getServer().getPluginManager().getPlugin("TrueFalseQuiz").getDescription().getPrefix() + "]";
        Bukkit.getConsoleSender().sendMessage(prefix + " " + addColour(message));
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
}
