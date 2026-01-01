package me.soapiee.common;

import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.VersionProvider;
import org.bukkit.block.Sign;

public class v1_19_Sign implements VersionProvider {
    @Override
    public void setLine(Sign sign, int lineNo, String text) {
        sign.setLine(lineNo, Utils.addColour(text));
    }
}
