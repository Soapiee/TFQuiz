package me.soapiee.common.conversations;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.listener.PlayerListener;
import me.soapiee.common.manager.GameManager;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReloadConvo extends FixedSetPrompt {

    private final MessageManager messageManager;
    private final GameManager gameManager;
    private final PlayerListener playerListener;

    public ReloadConvo(TFQuiz main) {
        super("confirm", "cancel");
        messageManager = main.getMessageManager();
        gameManager = main.getGameManager();
        playerListener = main.getPlayerListener();
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
        if (s.equalsIgnoreCase("confirm")) {
            reloadCheck(conversationContext.getForWhom());
            return Prompt.END_OF_CONVERSATION;
        }

        conversationContext.getForWhom().sendRawMessage(Utils.addColour(messageManager.get(Message.RELOADCONVOCANCEL)));
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return Utils.addColour(messageManager.get(Message.RELOADCONVOINVALID));
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return Utils.addColour(messageManager.get(Message.RELOADCONVOSTART));
    }

    private void reloadCheck(Conversable sender) {
        sender.sendRawMessage(Utils.addColour(messageManager.get(Message.ADMINRELOADINPROGRESS)));
        String reloadOutcome = Utils.addColour(messageManager.get(Message.ADMINRELOADSUCCESS));

        for (Game game : gameManager.getGames()) {
            game.reset(true, true);
            if (game.getHologram() != null) game.getHologram().despawn();
            game.setState(GameState.CLOSED);
        }

        boolean errors = !messageManager.load((CommandSender) sender);
        if (!gameManager.reloadAll((CommandSender) sender, playerListener)) errors = true;

        if (errors) reloadOutcome = Utils.addColour(messageManager.get(Message.ADMINRELOADERROR));

        if (sender instanceof Player) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ((CommandSender) sender).getName() + " " + reloadOutcome);
        }

        sender.sendRawMessage(reloadOutcome);
    }
}
