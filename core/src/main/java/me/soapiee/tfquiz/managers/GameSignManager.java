package me.soapiee.tfquiz.managers;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.utils.Message;
import me.soapiee.tfquiz.gameSigns.GameSignFactory;
import me.soapiee.tfquiz.games.Game;
import me.soapiee.tfquiz.gameSigns.GameSign;
import me.soapiee.tfquiz.internals.SignHandler;
import me.soapiee.tfquiz.utils.CustomLogger;
import me.soapiee.tfquiz.utils.MessageManager;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameSignManager {

    private final TFQuiz main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final SignHandler signHandler;
    private final GameManager gameManager;
    private final GameSignFactory gameSignFactory;

    private final Map<Game, ArrayList<GameSign>> signs = new HashMap<>();

    public GameSignManager(TFQuiz main, GameManager gameManager) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        signHandler = main.getVersionManager().getSignHandler();
        this.gameManager = gameManager;
        this.gameSignFactory = new GameSignFactory(main);
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    public boolean load(CommandSender sender) {
        FileConfiguration config = main.getConfig();

        if (!config.isConfigurationSection("signs.")) return false;
        if (config.getConfigurationSection("signs.") == null) return false;

        for (String signID : config.getConfigurationSection("signs.").getKeys(false)) {
            String inputGameID = config.getString("signs." + signID + ".game_ID", "a");
            Game game = validateGame(sender, inputGameID, signID);
            if (game == null) continue;

            GameSign gameSign = gameSignFactory.create(sender, signID, game.getIdentifier());
            if (gameSign == null) continue;

            addToMap(game, gameSign);
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.SIGNCREATEDSUCCESS, signID));
        }

        return false;
    }

    public ArrayList<GameSign> getSigns(Game game) {
        return signs.get(game);
    }

    public GameSign getSign(String signID) {
        for (Game game : signs.keySet()) {
            for (GameSign gameSign : signs.get(game)) {
                if (gameSign.getSignID().equalsIgnoreCase(signID)) {
                    return gameSign;
                }
            }
        }
        return null;
    }

    private void addToMap(Game game, GameSign sign) {
        if (signs.containsKey(game)) {
            signs.get(game).add(sign);
        } else {
            ArrayList<GameSign> list = new ArrayList<>();
            list.add(sign);
            signs.put(game, list);
        }
    }

    private void removeFromMap(Game game, GameSign sign) {
        if (!signs.containsKey(game)) return;
        signs.get(game).remove(sign);
    }

    private int validateGameID(CommandSender sender, String inputGameID, String signID) {
        int gameID;

        try {
            gameID = Integer.parseInt(inputGameID);
        } catch (IllegalArgumentException error) {
            gameID = -1;
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDSIGNGAME, signID));
        }

        return gameID;
    }

    private Game validateGame(CommandSender sender, String inputGameID, String signID) {
        int gameID = validateGameID(sender, inputGameID, signID);
        if (gameID < 0) return null;

        Game game = gameManager.getGame(gameID);
        if (game == null) {
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDSIGNGAME, signID));
            return null;
        }

        return game;
    }

    public void createSign(Sign block, Game game) { //saves a newly created (block)sign (created via command)
        GameSign gameSign = gameSignFactory.create(block, game.getIdentifier());
        addToMap(game, gameSign);
    }

    public void saveNewText(GameSign sign) { //updates the text on an already existing ArenaSign in the config
        FileConfiguration config = main.getConfig();
        String signID = sign.getSignID();

        config.set("signs." + signID + ".format", sign.getText());

        main.saveConfig();
    }

    public void deleteSign(GameSign gameSign) {
        FileConfiguration config = main.getConfig();

        config.set("signs." + gameSign.getSignID(), null);
        main.saveConfig();

        signHandler.despawn(gameSign);
        removeFromMap(gameSign.getGame(), gameSign);
    }

}
