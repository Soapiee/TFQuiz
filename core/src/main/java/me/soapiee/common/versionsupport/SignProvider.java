package me.soapiee.common.versionsupport;

import org.bukkit.block.Sign;

public interface SignProvider {

    //Adds 1.20 sign (multi-side) support
    void setLine(Sign sign, int lineNo, String text);
}
