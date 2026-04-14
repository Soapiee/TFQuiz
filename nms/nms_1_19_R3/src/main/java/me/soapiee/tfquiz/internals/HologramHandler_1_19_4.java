package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.games.arena.Hologram;
import me.soapiee.tfquiz.utils.Keys;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

public class HologramHandler_1_19_4 implements HologramHandler {

    @Override public void spawn(Hologram holo) {
        Location location = holo.getSpawnPoint().clone();
        location.setY((location.getY() - 0.25));

        for (String line : holo.getText()) {
            TextDisplay text = location.getWorld().spawn(location, TextDisplay.class);
            text.setBillboard(Display.Billboard.CENTER);
            text.getPersistentDataContainer().set(Keys.HOLOGRAM, PersistentDataType.STRING, holo.getGameID());
            text.setText(Utils.addColour(line));
            text.setLineWidth(1000);
            location.subtract(0, 0.25, 0);
        }
    }

    //TODO:
//    private void spawnTextDisplay(){}
//    private void spawnItemDisplay(){}
//    private void spawnBlockDisplay(){}

    @Override public void despawn(Hologram holo) {
        Location location = holo.getSpawnPoint();
        for (Entity entity : location.getWorld().getNearbyEntities(location, 5, 5, 5)) {
            if (entity instanceof TextDisplay && entity.getPersistentDataContainer().has(Keys.HOLOGRAM, PersistentDataType.STRING)) {
                String id = entity.getPersistentDataContainer().get(Keys.HOLOGRAM, PersistentDataType.STRING);
                if (id.equalsIgnoreCase(holo.getGameID())) entity.remove();
            }
        }
    }
}
