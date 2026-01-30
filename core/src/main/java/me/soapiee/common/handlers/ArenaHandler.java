package me.soapiee.common.handlers;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.common.enums.DescriptionType;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Hologram;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.Location;

public class ArenaHandler {

    @Getter @Setter private DescriptionType descType;
    @Getter @Setter private boolean allowSpectators;
    @Getter final private Hologram hologram;
    @Getter @Setter private Location spawn;

    public ArenaHandler(MessageManager messageManager) {
        descType = DescriptionType.CHAT;
        allowSpectators = false;
        hologram = new Hologram(messageManager.get(Message.GAMEHOLODESC));
        spawn = null;
    }

    public String getDescString() {
        return descType.toString().toLowerCase();
    }

    public String getSpawnString() {
        return (spawn == null) ? "not set" : "World: " + spawn.getWorld().getName() + " X=" + Math.round(spawn.getX()) + ", Y=" + Math.round(spawn.getY()) + ", Z=" + Math.round(spawn.getZ());
    }

    public void despawnHologram() {
        if (hologram.getSpawnPoint() == null) return;
        hologram.despawn();
    }

    public void spawnHologram() {
        if (descType == DescriptionType.CHAT) return;
        if (hologram.getSpawnPoint() == null) return;

        hologram.spawn();
    }

}
