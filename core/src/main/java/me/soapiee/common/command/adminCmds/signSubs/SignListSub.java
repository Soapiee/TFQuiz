package me.soapiee.common.command.adminCmds.signSubs;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.command.adminCmds.AbstractAdminSub;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.utils.Utils;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SignListSub extends AbstractAdminSub {

    private final String IDENTIFIER = "signlist";

    public SignListSub(TFQuiz main) {
        super(main, "tfquiz.admin.signs", 2, 2);
    }

    // /tf sign list
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        if (isConsole(sender, false)) {
            sendConsoleList(sender);
        } else {
            Player player = (Player) sender;
            sendPlayerList(player);
        }
    }

    private void sendPlayerList(Player player) {
        ComponentBuilder builder = new ComponentBuilder();

        String header = messageManager.get(Message.SIGNLISTHEADER);
        builder.append(TextComponent.fromLegacyText(header));

        for (Game game : gameManager.getGames()) {
            if (game.getSigns() == null) continue;

            for (GameSign sign : game.getSigns()) {
                String messageFormat = messageManager.getWithPlaceholder(Message.SIGNLISTFORMAT, sign.getSignID(), game.getIdentifier());
                builder.append("", ComponentBuilder.FormatRetention.NONE)
                        .append("\n")
                        .append(getComponentMsg(messageFormat, sign.getSignID()));
            }
        }

        player.spigot().sendMessage(builder.create());
    }

    private void sendConsoleList(CommandSender console) {
        StringBuilder builder = new StringBuilder();

        String header = messageManager.get(Message.SIGNLISTHEADER);
        builder.append(Utils.addColour(header));

        for (Game game : gameManager.getGames()) {
            if (game.getSigns() == null) continue;

            for (GameSign sign : game.getSigns()) {
                String messageFormat = messageManager.getWithPlaceholder(Message.SIGNLISTFORMAT, sign.getSignID(), game.getIdentifier());
                builder.append("\n")
                        .append(Utils.addColour(messageFormat));
            }
        }

        sendMessage(console, builder.toString());
    }

    private TextComponent getComponentMsg(String string, String signID) {
        return createTextComponent(string,
                "/tf sign teleport " + signID,
                messageManager.get(Message.SIGNLISTHOVER));
    }

    private TextComponent createTextComponent(String message, String cmd, String hoverText) {
        if (message == null || message.isEmpty()) return null;

        TextComponent component = new TextComponent("");
        String translatedMessage = Utils.addColour(message);
        for (BaseComponent child : TextComponent.fromLegacyText(translatedMessage)) {
            component.addExtra(child);
        }

        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        if (hoverText != null && !hoverText.isEmpty()) {
            String translatedHover = Utils.addColour(hoverText);

            BaseComponent[] hoverComponents = TextComponent.fromLegacyText(translatedHover);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverComponents)));
        }

        return component;
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
