package me.soapiee.tfquiz.internals;

import me.soapiee.tfquiz.enums.Message;
import me.soapiee.tfquiz.instance.GameSign;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class Sign_Unsupported implements SignHandler {

    private final CustomLogger logger;
    private final MessageManager messageManager;

    public Sign_Unsupported(MessageManager messageManager, CustomLogger logger) {
        this.logger = logger;
        this.messageManager = messageManager;
    }

    public void updateText(GameSign gameSign) {
    }

    public BlockFace getBlockFace(BlockData blockData) {
        return BlockFace.NORTH;
    }

    public void spawn(GameSign gameSign, BlockFace blockFace) {
        logger.logToFile(null, messageManager.get(Message.SIGNSUNSUPPORTEDVERSION));
    }

    @Override
    public void despawn(GameSign gameSign) {
    }
}
