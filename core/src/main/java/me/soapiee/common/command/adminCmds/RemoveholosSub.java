package me.soapiee.common.command.adminCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.utils.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class RemoveholosSub extends AbstractAdminSub {

    private final String IDENTIFIER = "removeholos";

    public RemoveholosSub(TFQuiz main) {
        super(main, null, 1, 2);
    }

    // /tf removeholos
    // /tf removeholos -all
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!settingsManager.isDebugMode()) return;
        if (isConsole(sender, true)) return;
        if (!checkRequirements(sender, label, args)) return;

        Player player = (Player) sender;

        if (args.length == 1) {
            removeNear(player);
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("-all")) {
            removeAll(player);
            return;
        }

        sendMessage(player, messageManager.getWithPlaceholder(Message.ADMINCMDUSAGE, label));
    }

    private void removeAll(Player player) {
        int count = 0;

        for (Entity entity : Bukkit.getWorld("world").getEntities()) {
            if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE)) {
                entity.remove();
                count++;
            }
        }

        sendMessage(player, "&a" + count + " holograms were removed");
    }

    private void removeNear(Player player) {
        int count = 0;

        Location loc = player.getLocation();
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, loc.getX() + 3, loc.getY() + 3, loc.getX() + 3)) {
            if (entity instanceof ArmorStand) {
                entity.remove();
                count++;
            }
        }

        sendMessage(player, "&a" + count + " holograms were removed near you");
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
