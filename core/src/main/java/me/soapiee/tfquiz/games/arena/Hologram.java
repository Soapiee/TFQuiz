package me.soapiee.tfquiz.games.arena;

import lombok.Getter;
import org.bukkit.Location;

public class Hologram {

    @Getter private final String gameID;
    @Getter private final String[] text;
    @Getter private Location spawnPoint;

    public Hologram(String text, int gameID) {
        this.text = text.split("\n");
        this.gameID = String.valueOf(gameID);
    }

    public void setLocation(Location newLoc) {
        spawnPoint = newLoc;
    }

    public String getLocationString() {
        if (spawnPoint == null) return null;
        return "World: " + spawnPoint.getWorld().getName() + " X=" + spawnPoint.getX() + ", Y=" + spawnPoint.getY() + " Z=" + spawnPoint.getZ();
    }
}
