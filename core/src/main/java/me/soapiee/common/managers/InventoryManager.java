package me.soapiee.common.managers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.utils.CustomLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryManager {
    private final TFQuiz main;
    private final CustomLogger logger;
    private final MessageManager messageManager;
    private final File file;
    private final YamlConfiguration config;

    public InventoryManager(TFQuiz main) {
        this.main = main;
        logger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        file = new File(main.getDataFolder(), "playerInventories.yml");
        config = new YamlConfiguration();

        load();
    }

    private void load() {
        if (!file.exists()) {
            main.saveResource("playerInventories.yml", false);
        }

        try {
            config.load(file);
        } catch (Exception ex) {
            logger.logToFile(ex, messageManager.get(Message.INVENTORIESFILEERROR));
        }
    }

    public void savePlayer(Player player, ItemStack[] inv) {
        String section = player.getUniqueId().toString();
        ArrayList<String> materials = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        for (ItemStack stack : Arrays.stream(inv).filter(Objects::nonNull).collect(Collectors.toList())) {
            materials.add(stack.getType() + " x" + stack.getAmount());
        }

        if (config.isConfigurationSection(section)) {
            int nextSlot = config.getConfigurationSection(section).getKeys(false).size() + 1;
            config.set(section + "." + nextSlot + ".occured", format.format(date));
            config.set(section + "." + nextSlot + ".items", materials);
        } else {
            config.createSection(player.getUniqueId().toString());
            config.set(section + ".1.occured", format.format(date));
            config.set(section + ".1.items", materials);
        }

        try {
            config.save(file);
        } catch (Exception ex) {
            logger.logToFile(ex, messageManager.getWithPlaceholder(Message.INVENTORIESSAVEERROR, player.getName()));
        }
    }

    public void removePlayer(Player player) {
        String section = player.getUniqueId().toString();
        if (!config.isConfigurationSection(section)) return;

        int lastSlot = config.getConfigurationSection(section).getKeys(false).size();

        if (lastSlot == 1) config.set(section, null);
        else config.set(section + "." + lastSlot, null);

        try {
            config.save(file);
        } catch (Exception ex) {
            logger.logToFile(ex, messageManager.getWithPlaceholder(Message.INVENTORIESREMOVEERROR, player.getName()));
        }
    }
}
