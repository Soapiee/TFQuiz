package me.soapiee.common.managers;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.GameFactory;
import me.soapiee.common.utils.CustomLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameManager {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    @Getter private final List<Game> games = new ArrayList<>();
    @Getter private final GamePlayerManager gamePlayerManager;
    private final GameFactory gameFactory;

    public GameManager(TFQuiz main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        gamePlayerManager = new GamePlayerManager();
        gameFactory = new GameFactory(main);
    }

    public boolean reload(CommandSender sender) {
        games.clear();
        gameFactory.reload();
        return (load(sender));
    }

    public boolean load(CommandSender sender) {
        FileConfiguration config = main.getConfig();

        if (!config.isConfigurationSection("games") || config.getConfigurationSection("games").getKeys(false).isEmpty()) {
            customLogger.logToPlayer(sender, null, messageManager.get(Message.NOGAMESSET));
            return true;
        }

        for (String id : config.getConfigurationSection("games.").getKeys(false)) {
            Game game = gameFactory.create(sender, id);
            games.add(game);

            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.GAMECREATEDSUCCESS, id));
        }

        return false;
    }

    public Game getGame(UUID uuid) {
        for (Game game : games) {
            if (game.getAllPlayers().contains(uuid)) {
                return game;
            }
        }
        return null;
    }

    public Game getGame(int id) {
        for (Game game : games) {
            if (game.getIdentifier() == id) {
                return game;
            }
        }
        return null;
    }

    //TODO:
//    public void deleteGame(Game game) {
//        // Cancel any schedulers
//        Scheduler scheduler = getScheduler(game.getID());
//        if (scheduler != null) {
//            try {
//                scheduler.setPlayed();
//                scheduler.cancel();
//            } catch (IllegalStateException ignored) {
//            }
//        }
//
//        // Remove game from hashMap
//        int gameID = game.getID();
//        games.remove(game);
//
//        // Remove it from the config
//        FileConfiguration config = main.getConfig();
//        config.set("games." + gameID, null);
//        main.saveConfig();
//    }
//
//    public void addGame() {
//
//    }
}
