package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.games.arena.Hologram;

public interface HologramHandler {

    void spawn(Hologram holo);

    void despawn(Hologram holo);
}
