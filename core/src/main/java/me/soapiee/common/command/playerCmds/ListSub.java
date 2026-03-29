package me.soapiee.common.command.playerCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.utils.Utils;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListSub extends AbstractPlayerSub {

    private final String IDENTIFIER = "list";

    public ListSub(TFQuiz main) {
        super(main, "tfquiz.player.list", 1, 1);
    }

    // /game list
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;
        Player player = (Player) sender;

        ComponentBuilder builder = new ComponentBuilder();
        String header = messageManager.get(Message.GAMELISTHEADER);
        builder.append(TextComponent.fromLegacyText(header));

        for (Game game : gameManager.getGames()) {
            String messageFormat = messageManager.getWithPlaceholder(Message.GAMELIST, game);
            builder.append("", ComponentBuilder.FormatRetention.NONE)
                    .append("\n")
                    .append(getComponentMsg(messageFormat, game.getIdentifier()));
        }

        player.spigot().sendMessage(builder.create());
    }

    private TextComponent getComponentMsg(String string, int gameID) {
        return createTextComponent(string,
                "/game join " + gameID,
                messageManager.get(Message.GAMELISTHOVER));
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
