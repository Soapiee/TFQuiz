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
import org.bukkit.block.data.type.WallSign;
import org.bukkit.persistence.PersistentDataType;

public class v1_16_Sign implements SignHandler {

    private void setLine(Sign sign, int lineNo, String text) {
        sign.setLine(lineNo, Utils.addColour(text));
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

    public BlockFace getBlockFace(BlockData blockData) {
        BlockFace blockFace;

        if (blockData instanceof WallSign) {
            WallSign wallSign = (WallSign) blockData;
            blockFace = wallSign.getFacing();
        } else {
            org.bukkit.block.data.type.Sign signdata = (org.bukkit.block.data.type.Sign) blockData; //1.20
            blockFace = signdata.getRotation();
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
        if (blockData instanceof WallSign) {
            WallSign wallData = (WallSign) blockData;
            wallData.setFacing(blockFace);
        } else {
            org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) blockData;
            signData.setRotation(blockFace);
        }
    }

}
