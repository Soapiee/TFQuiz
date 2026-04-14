package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.games.arena.Hologram;
import me.soapiee.tfquiz.utils.Keys;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class HologramHandler_Legacy implements HologramHandler {

    @Override public void spawn(Hologram holo) {
        Location location = holo.getSpawnPoint().clone();
        location.setY((location.getY() - 2.5)); // Only needed for amrour stands

        for (String line : holo.getText()) {
            ArmorStand armourStand = location.getWorld().spawn(location, ArmorStand.class);
            armourStand.setVisible(false);
            armourStand.setGravity(false);
            armourStand.setInvulnerable(true);
            armourStand.getPersistentDataContainer().set(Keys.HOLOGRAM, PersistentDataType.STRING, holo.getGameID());
            armourStand.setCustomNameVisible(true);
            armourStand.setCustomName(Utils.addColour(line));
            location.subtract(0, 0.25, 0);
        }
    }

    @Override public void despawn(Hologram holo) {
        Location location = holo.getSpawnPoint();
        for (Entity entity : location.getWorld().getNearbyEntities(location, 5, 5, 5)) {
            if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.HOLOGRAM, PersistentDataType.STRING)) {
                String id = entity.getPersistentDataContainer().get(Keys.HOLOGRAM, PersistentDataType.STRING);
                if (id.equalsIgnoreCase(holo.getGameID())) entity.remove();
            }
        }
    }
}
