package me.soapiee.common.instance;

import lombok.Getter;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class Hologram {

    private final String[] text;
    @Getter private Location spawnPoint;

    public Hologram(String text) {
        this.text = text.split("\n");
    }

    public void spawn() {
        if (spawnPoint == null) return;
        despawn();

        for (String line : text) {
            ArmorStand armourStand = this.spawnPoint.getWorld().spawn(spawnPoint, ArmorStand.class);
            armourStand.setVisible(false);
            armourStand.setGravity(false);
            armourStand.setInvulnerable(true);
            armourStand.getPersistentDataContainer().set(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE, (byte) 1);
            armourStand.setCustomNameVisible(true);
            armourStand.setCustomName(Utils.addColour(line));
            spawnPoint.subtract(0, 0.25, 0);
        }
        double change = 0.25 * text.length;
        spawnPoint.add(0, change, 0);
    }

    public void despawn() {
        if (spawnPoint == null) return;
        for (Entity entity : spawnPoint.getWorld().getNearbyEntities(spawnPoint, 5, 5, 5)) {
            if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM_ARMOURSTAND, PersistentDataType.BYTE)) {
                entity.remove();
            }
        }
    }

    public void setLocation(Location newLoc) {
        spawnPoint = newLoc;
    }

    public String getLocationString() {
        if (spawnPoint == null) return null;
        return "World: " + spawnPoint.getWorld().getName() + " X=" + spawnPoint.getX() + ", Y=" + spawnPoint.getY() + " Z=" + spawnPoint.getZ();
    }
}
