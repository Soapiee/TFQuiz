package me.soapiee.common.utils;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomLogger {
    private final TFQuiz main;
    private final MessageManager messageManager;
    private final File logFile;

    public CustomLogger(TFQuiz main) {
        this.main = main;
        messageManager = main.getMessageManager();

        logFile = new File(main.getDataFolder() + File.separator + "logger.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Utils.consoleMsg(messageManager.get(Message.LOGGERFILEERROR));
            }
        }
    }

    public void logToFile(Exception error, String string) {
        LogType logType = (error == null) ? LogType.WARNING : LogType.SEVERE;
        if (!string.isEmpty()) Utils.consoleMsg(string);

        if (logFile == null) return;

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true), true);
            Date dt = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(dt);
            writer.write("--------------------------------------------------------------------------------------------------");
            writer.write(System.lineSeparator());
            writer.write(time + " [" + logType.name() + "] " + string);
            writer.write(System.lineSeparator());
            writer.write(messageManager.get(Message.PLUGINVERSIONSTRING) + Bukkit.getPluginManager().getPlugin("TrueFalseQuiz").getDescription().getVersion());
            writer.write(System.lineSeparator());
            writer.write(messageManager.get(Message.SERVERVERSIONSTRING) + Bukkit.getBukkitVersion());
            writer.write(System.lineSeparator());
            if (error != null) {
                writer.write(System.lineSeparator());
                error.printStackTrace(writer);
            }
            writer.write("--------------------------------------------------------------------------------------------------");
            writer.write(System.lineSeparator());
            writer.write(System.lineSeparator());
            writer.close();
            Utils.consoleMsg(messageManager.get(Message.LOGGERLOGSUCCESS));
        } catch (IOException e) {
            Utils.consoleMsg(messageManager.get(Message.LOGGERLOGERROR));
            e.printStackTrace();
        }
    }

    public void logToPlayer(CommandSender sender, Exception error, String string) {
        if (error != null) logToFile(error, string);

        if (sender == null) return;
        if (string.isEmpty()) return;

        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (sender instanceof Player && ((Player) sender).isOnline()) {
                sender.sendMessage(Utils.addColour(string));
                return;
            }

            Utils.consoleMsg(string);
        }, 20L);
    }

    private enum LogType {
        SEVERE(""),
        WARNING("");

        public final String colour;

        LogType(String colour) {
            this.colour = colour;
        }
    }
}
