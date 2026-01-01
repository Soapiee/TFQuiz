package me.soapiee.common;

import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.VersionProvider;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

public class v1_20_Sign implements VersionProvider {
    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        sign.getSide(Side.FRONT).setLine(lineNo, Utils.addColour(text));
    }
}
