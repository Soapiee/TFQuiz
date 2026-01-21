package me.soapiee.common;

import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.SignProvider;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

public class v1_20_Sign implements SignProvider {
    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        sign.getSide(Side.FRONT).setLine(lineNo, Utils.addColour(text));
    }
}
