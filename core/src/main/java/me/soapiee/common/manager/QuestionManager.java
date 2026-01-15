package me.soapiee.common.manager;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.logic.Question;
import me.soapiee.common.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class QuestionManager {

    private final TFQuiz main;
    private final Logger customLogger;
    private final MessageManager messageManager;

    private final File file;
    @Getter private final ArrayList<Question> trueQuestions = new ArrayList<>();
    @Getter private final ArrayList<Question> falseQuestions = new ArrayList<>();

    public QuestionManager(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        file = new File(main.getDataFolder(), "questions.yml");

        load(null);
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        if (!file.exists()) {
            main.saveResource("questions.yml", false);
        }

        YamlConfiguration contents;
        try {
            contents = YamlConfiguration.loadConfiguration(file);
        } catch (Exception ex) {
            customLogger.logToPlayer(sender, ex, messageManager.get(Message.QUESTIONSFILEERROR));
            return true;
        }

        return read(sender, contents);
    }

    private boolean read(CommandSender sender, YamlConfiguration contents) {
        if (contents.getConfigurationSection("Questions.").getKeys(false).isEmpty()) {
            customLogger.logToPlayer(sender, null, messageManager.get(Message.NOQUESTIONSSET));
            return true;
        }

        for (String string : contents.getConfigurationSection("Questions").getKeys(false)) {
            String questionPath = "Questions." + string + ".Question";
            String correctPath = "Questions." + string + ".Correction_Message";
            Question question = new Question(contents.getString(questionPath), contents.getString(correctPath));

            if (contents.getBoolean("Questions." + string + ".Answer")) trueQuestions.add(question);
            else falseQuestions.add(question);
        }

        return false;
    }
}
