package me.soapiee.common.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCmd {

    String getIDENTIFIER();

    void execute(CommandSender sender, String label, String[] args);

    List<String> getTabCompletions(String[] args);

    boolean checkRequirements(CommandSender sender, String label, String[] args);

}
