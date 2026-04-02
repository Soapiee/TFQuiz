package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.gameSigns.GameSign;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public interface SignHandler {

    void updateText(GameSign gameSign);

    BlockFace getBlockFace(BlockData blockData);

    void spawn(GameSign gameSign, BlockFace blockFace);

    default void despawn(GameSign gameSign) {
        gameSign.getLocation().getBlock().setType(Material.AIR);
    }
}
