package me.soapiee.tfquiz.games.arena;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.games.enums.DescriptionType;
import me.soapiee.tfquiz.internals.HologramHandler;
import me.soapiee.tfquiz.utils.Message;
import org.bukkit.Location;

public class ArenaHandler {

    @Getter @Setter private DescriptionType descType;
    @Getter @Setter private boolean allowSpectators;
    @Getter final private Hologram hologram;
    @Getter @Setter private Location spawn;

    private final HologramHandler hologramHandler;

    public ArenaHandler(TFQuiz main, int gameID) {
        descType = DescriptionType.CHAT;
        allowSpectators = false;
        hologram = new Hologram(main.getMessageManager().get(Message.GAMEHOLODESC), gameID);
        spawn = null;

        hologramHandler = main.getVersionManager().getHologramHandler();
    }

    public String getDescString() {
        return descType.toString().toLowerCase();
    }

    public String getSpawnString() {
        return (spawn == null) ? "not set" : "World: " + spawn.getWorld().getName() + " X=" + Math.round(spawn.getX()) + ", Y=" + Math.round(spawn.getY()) + ", Z=" + Math.round(spawn.getZ());
    }

    public void despawnHologram() {
        if (hologram.getSpawnPoint() == null) return;

        hologramHandler.despawn(hologram);
    }

    public void spawnHologram() {
        if (descType == DescriptionType.CHAT) return;
        if (hologram.getSpawnPoint() == null) return;

        hologramHandler.spawn(hologram);
    }

}
