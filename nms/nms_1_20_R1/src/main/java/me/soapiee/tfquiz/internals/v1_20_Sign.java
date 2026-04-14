package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.gameSigns.GameSign;
import me.soapiee.tfquiz.utils.Keys;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.WallHangingSign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.persistence.PersistentDataType;

public class v1_20_Sign implements SignHandler {

    public BlockFace getBlockFace(BlockData blockData) {
        BlockFace blockFace;

        if (blockData instanceof WallSign wallSign) {
            blockFace = wallSign.getFacing();
        } else if (blockData instanceof HangingSign hangingSign) {
            blockFace = hangingSign.getRotation();
        } else if (blockData instanceof WallHangingSign wallHangingSign) {
            blockFace = wallHangingSign.getFacing();
        } else {
            org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) blockData;
            blockFace = signData.getRotation();
        }

        return blockFace;
    }

    public void spawn(GameSign gameSign, BlockFace blockFace) {
        Location location = gameSign.getLocation();
        Material material = gameSign.getMaterial();
        Block block = gameSign.getLocation().getBlock();

        if (block.getType() != material) block.setType(material);

        block = location.getBlock();
        BlockData blockData = block.getBlockData();
        setDirection(blockData, blockFace);
        block.setBlockData(blockData);

        Sign signBlock = (Sign) block.getState();
        signBlock.getPersistentDataContainer().set(Keys.GAME_SIGN, PersistentDataType.STRING, gameSign.getSignID());
        gameSign.setSignBlock(signBlock);

        updateText(gameSign);
    }

    private void setDirection(BlockData blockData, BlockFace blockFace) {
        if (blockData instanceof WallSign wallData) {
            wallData.setFacing(blockFace);
        } else if (blockData instanceof HangingSign hangingData) {
            hangingData.setRotation(blockFace);
        } else if (blockData instanceof WallHangingSign hangingData) {
            hangingData.setFacing(blockFace);
        } else {
            org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) blockData;
            signData.setRotation(blockFace);
        }
    }

    public void updateText(GameSign gameSign) {
        Sign signBlock = gameSign.getSignBlock();

        int i = 0;
        for (String line : gameSign.getFormattedText()) {
            setLine(signBlock, i, line);
            i++;
        }

        signBlock.update();
    }

    private void setLine(Sign sign, int lineNo, String text) {
        sign.getSide(Side.FRONT).setLine(lineNo, Utils.addColour(text));
    }

}
