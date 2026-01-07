package me.soapiee.common.conversations;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.manager.GameSignManager;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Utils;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SignConvo extends MessagePrompt {

    private final MessageManager messageManager;
    private final GameSignManager gameSignManager;

    public SignConvo(TFQuiz main) {
        messageManager = main.getMessageManager();
        gameSignManager = main.getGameSignManager();
    }

    @Override
    protected @Nullable Prompt getNextPrompt(@NotNull ConversationContext conversationContext) {
        return new askForLinePrompt();
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return Utils.addColour(messageManager.get(Message.SIGNCONVOSTART));
    }

    private class askForLinePrompt extends NumericPrompt {
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return Utils.addColour(messageManager.get(Message.SIGNCONVOLINEPROMPT));
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 4;
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull Number number) {
            conversationContext.setSessionData("line", (int) number - 1);
            return new askForTextPrompt();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return Utils.addColour(messageManager.get(Message.SIGNCONVOLINEINVALID));
        }
    }

    private class askForTextPrompt extends StringPrompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            int lineNo = (int) conversationContext.getSessionData("line") + 1;
            return Utils.addColour(messageManager.getWithPlaceholder(Message.SIGNCONVOTEXTPROMPT, lineNo));
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
            conversationContext.setSessionData("text", s);
            return new updateSignPrompt();
        }
    }

    private class updateSignPrompt extends MessagePrompt {

        @Override
        protected @Nullable Prompt getNextPrompt(@NotNull ConversationContext conversationContext) {
            Player player = (Player) conversationContext.getForWhom();
            String signID = player.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING);
            int lineNo = (int) conversationContext.getSessionData("line");
            String text = (String) conversationContext.getSessionData("text");

            //update the sign
            GameSign gameSign = gameSignManager.getSign(signID);
            gameSign.update(lineNo, text);
            gameSignManager.saveNewText(gameSign);

            conversationContext.setSessionData("line", null);
            conversationContext.setSessionData("text", null);
            return new askForLinePrompt();
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            Player player = (Player) conversationContext.getForWhom();
            String signID = player.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING);

            return Utils.addColour(Utils.addColour(messageManager.getWithPlaceholder(Message.SIGNEDITED, signID)));
        }
    }
}
