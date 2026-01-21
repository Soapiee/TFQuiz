package me.soapiee.common;

import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.SignProvider;
import org.bukkit.block.Sign;

public class v1_19_Sign implements SignProvider {
    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        sign.setLine(lineNo, Utils.addColour(text));
    }
}
