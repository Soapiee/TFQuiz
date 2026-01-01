package me.soapiee.common.command.adminCmds;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.conversations.ReloadConvo;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ReloadSub extends AbstractAdminSub {

    private final String IDENTIFIER = "reload";
    private final ConversationFactory convoFactory;

    public ReloadSub(TFQuiz main) {
        super(main, "TFQuiz.reload", 1, 1);
        convoFactory = new ConversationFactory(main)
                .withFirstPrompt(new ReloadConvo(main))
                .withTimeout(10)
                .addConversationAbandonedListener(main.getPlayerListener())
                .withEscapeSequence("cancel");
    }

    // /tf reload
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        if (sender instanceof Player) startPlayerConvo(sender);
        else startConsoleConvo(sender);
    }

    private void startPlayerConvo(CommandSender sender) {
        Player player = (Player) sender;
        String activeConvo = player.getPersistentDataContainer().get(Keys.ACTIVE_CONVERSATION, PersistentDataType.STRING);

        if (activeConvo == null) {
            player.getPersistentDataContainer().set(Keys.ACTIVE_CONVERSATION, PersistentDataType.STRING, "reloadConvo");
            convoFactory.buildConversation((Conversable) sender).begin();
        }
    }

    private void startConsoleConvo(CommandSender sender) {
        reloadCheck(sender);
    }

    private void reloadCheck(CommandSender sender) {
        sendMessage(sender, messageManager.get(Message.ADMINRELOADINPROGRESS));
        String reloadOutcome = Utils.addColour(messageManager.get(Message.ADMINRELOADSUCCESS));

        for (Game game : gameManager.getGames()) {
            game.reset(true, true);
            if (game.getHologram() != null) game.getHologram().despawn();
            game.setState(GameState.CLOSED);
        }

        boolean errors = !messageManager.load(sender);
        if (!gameManager.reloadAll(sender, main.getPlayerListener())) errors = true;

        if (errors) reloadOutcome = Utils.addColour(messageManager.get(Message.ADMINRELOADERROR));

        sendMessage(sender, reloadOutcome);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
