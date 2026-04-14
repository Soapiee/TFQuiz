package me.soapiee.tfquiz.conversations;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.games.Game;
import me.soapiee.tfquiz.games.enums.GameState;
import me.soapiee.tfquiz.managers.GameManager;
import me.soapiee.tfquiz.managers.SchedulerManager;
import me.soapiee.tfquiz.utils.Message;
import me.soapiee.tfquiz.utils.MessageManager;
import me.soapiee.tfquiz.utils.Utils;
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

    private final TFQuiz main;
    private final MessageManager messageManager;
    private final GameManager gameManager;

    public ReloadConvo(TFQuiz main) {
        super("confirm", "cancel");
        this.main = main;
        messageManager = main.getMessageManager();
        gameManager = main.getGameManager();
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

        SchedulerManager schedulerManager = main.getSchedulerManager();
        schedulerManager.cancelSchedulers();

        for (Game game : gameManager.getGames()) {
            game.getLifeCycleHandler().reset(true, true);
            game.getArenaHandler().despawnHologram();
            game.setState(GameState.CLOSED);
        }

        CommandSender commandSender = (CommandSender) sender;
        main.reloadConfig();
        boolean errors = messageManager.reload(commandSender);
        if (main.getSettingsManager().reload(commandSender)) errors = true;
        main.getPlayerListener().setFlags();
        if (main.getQuestionManager().reload(commandSender)) errors = true;
        if (gameManager.reload(commandSender)) errors = true;
        if (main.getGameSignManager().reload(commandSender)) errors = true;
        schedulerManager.reload();

        if (errors) reloadOutcome = Utils.addColour(messageManager.get(Message.ADMINRELOADERROR));

        if (sender instanceof Player) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ((CommandSender) sender).getName() + " " + reloadOutcome);
        }

        sender.sendRawMessage(reloadOutcome);
    }
}
