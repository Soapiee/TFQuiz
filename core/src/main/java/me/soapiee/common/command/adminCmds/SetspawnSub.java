package me.soapiee.common.command.adminCmds;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.SettingsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetspawnSub extends AbstractAdminSub {

    private final SettingsManager settingsManager;
    @Getter private final String IDENTIFIER = "setspawn";

    public SetspawnSub(TFQuiz main) {
        super(main, null, 1, 1);
        settingsManager = main.getSettingsManager();
    }

    // /tf setspawn
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, true)) return;

        Player player = (Player) sender;
        settingsManager.setLobbySpawn(player.getLocation());
        sendMessage(player, messageManager.get(Message.ADMINSETLOBBYSPAWN));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

}
