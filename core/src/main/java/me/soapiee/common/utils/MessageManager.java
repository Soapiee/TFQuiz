package me.soapiee.common.utils;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.handlers.ArenaHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.tasks.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final TFQuiz main;
    private final File file;
    private final YamlConfiguration contents;
    private final String language;

    public MessageManager(TFQuiz main) {
        this.main = main;

        String languageString = validateLanguage();
        if (languageString == null) language = Languages.LANG_EN.toString().toLowerCase();
        else language = languageString;

        //TODO: Revert code back in future updates
//        file = new File(main.getDataFolder() + File.separator + "language", language + ".yml");
        file = getFile();
        contents = new YamlConfiguration();

        load(null);

        if (languageString == null) Utils.consoleMsg(get(Message.INVALIDLANGUAGE));
    }

    private File getFile() {
        File newLangFile = new File(main.getDataFolder() + File.separator + "language", language + ".yml");
        if (!newLangFile.exists()) main.saveResource("language" + File.separator + language + ".yml", false);

        File legacyFile = new File(main.getDataFolder(), "messages.yml");
        if (legacyFile.exists()) {
            Utils.consoleMsg(ChatColor.RED.toString() + ChatColor.BOLD + "[IMPORTANT] " + ChatColor.RESET
                    + ChatColor.RED + "Please transfer the contents of your messages.yml file to the new language file. Located in the \"language\" folder. Then delete the messages.yml file");
            return legacyFile;
        }

        return newLangFile;
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        //TODO: Revert code back in future updates
//        if (!file.exists()) main.saveResource("language" + File.separator + language + ".yml", false);

        try {
            contents.load(file);
        } catch (Exception ex) {
            if (sender != null) {
                main.getCustomLogger().logToPlayer(sender, ex, get(Message.LANGUAGEFILEERROR));
            } else ex.printStackTrace();
            return true;
        }
        return false;
    }

    private void save() {
        try {
            contents.save(file);
        } catch (Exception ex) {
            main.getCustomLogger().logToFile(ex, get(Message.LANGUAGEFIELDERROR));
        }
    }

    private String validateLanguage() {
        String configString = main.getConfig().getString("language", "lang_en");

        Languages lang;
        try {
            lang = Languages.valueOf(configString.toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }

        return lang.toString().toLowerCase();
    }

    public String get(Message messageEnum) {
        String path = messageEnum.getPath();
        String def = messageEnum.getDefault();

        if (contents.isSet(path)) {
            String text = ((contents.isList(path)) ? String.join("\n", contents.getStringList(path)) : contents.getString(path));

            return text.isEmpty() ? null : text;
        } else {
            if (def.contains("\n")) {
                String[] list;
                list = def.split("\n");
                contents.set(path, list);
            } else {
                contents.set(path, def);
            }
            save();
            return def;
        }
    }

    public String get(GameState stateEnum) {
        String path = stateEnum.getPath();
        String def = stateEnum.getDefault();

        if (contents.isSet(path)) {
            return (contents.isList(path)) ? String.join("\n", contents.getStringList(path)) : contents.getString(path);
        } else {
            if (def.contains("\n")) {
                String[] list;
                list = def.split("\n");
                contents.set(path, list);
            } else {
                contents.set(path, def);
            }
            save();
            return def;
        }
    }

    public String getWithPlaceholder(Message messageEnum, Game game) {
        int gameID = game.getIdentifier();
        String message = get(messageEnum);

        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", String.valueOf(gameID));
        if (message.contains("%game_players%"))
            message = message.replace("%game_players%", String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()));
        if (message.contains("%game_maxplayers%"))
            message = message.replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
        if (message.contains("%game_minplayers%"))
            message = message.replace("%game_minplayers%", String.valueOf(game.getMinPlayers()));
        if (message.contains("%game_status%")) message = message.replace("%game_status%", game.getStateDescription());

        return message;
    }

    public String getInfo(Message messageEnum, Game game) {
        String message = get(messageEnum);
        Scheduler scheduler = main.getSchedulerManager().getScheduler(game.getIdentifier());
        int gameID = game.getIdentifier();
        ArenaHandler arenaHandler = game.getArenaHandler();

        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", String.valueOf(gameID));
        if (message.contains("%game_players%"))
            message = message.replace("%game_players%",
                    String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()));
        if (message.contains("%game_maxplayers%"))
            message = message.replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
        if (message.contains("%game_minplayers%"))
            message = message.replace("%game_minplayers%", String.valueOf(game.getMinPlayers()));
        if (message.contains("%game_countdown%"))
            message = message.replace("%game_countdown%",
                    String.valueOf(game.getLifeCycleHandler().getCountdown().getTotalSeconds()));
        if (message.contains("%game_maxrounds%"))
            message = message.replace("%game_maxrounds%", String.valueOf(game.getMaxRounds()));
        if (message.contains("%game_doesbroadcast%"))
            message = message.replace("%game_doesbroadcast%", String.valueOf(game.isBroadcastWinners()));
        if (message.contains("%game_reward%")) message = message.replace("%game_reward%", game.getReward().toString());
        if (message.contains("%game_hasarena%"))
            message = message.replace("%game_hasarena%", String.valueOf(game.isPhysicalArena()));
        if (message.contains("%game_hasscheduler%"))
            message = message.replace("%game_hasscheduler%", (scheduler == null) ? "false" : "true");
        if (message.contains("%game_schedulerseconds%"))
            message = message.replace("%game_schedulerseconds%",
                    (scheduler == null) ? "" : String.valueOf(scheduler.getRemainingTime()));
        if (message.contains("%game_desc%")) message = message.replace("%game_desc%", arenaHandler.getDescString());
        if (message.contains("%game_doesspectators%"))
            message = message.replace("%game_doesspectators%",
                    String.valueOf(arenaHandler.isAllowSpectators()));
        if (message.contains("%game_holocoordinates%"))
            message = message.replace("%game_holocoordinates%",
                    (arenaHandler.getHologram().getSpawnPoint() == null) ? "not set" : arenaHandler.getHologram().getLocationString());
        if (message.contains("%game_spawncoordinates%"))
            message = message.replace("%game_spawncoordinates%", arenaHandler.getSpawnString());
        if (message.contains("%game_status%")) message = message.replace("%game_status%", game.getStateDescription());

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, Game game, String string) {
        int gameID = game.getIdentifier();
        String message = get(messageEnum);

        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", String.valueOf(gameID));
        if (message.contains("%game_players%"))
            message = message.replace("%game_players%",
                    String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()));
        if (message.contains("%game_maxplayers%"))
            message = message.replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
        if (message.contains("%game_minplayers%"))
            message = message.replace("%game_minplayers%", String.valueOf(game.getMinPlayers()));
        if (message.contains("%player%")) message = message.replace("%player%", string);
        if (message.contains("%game_status%")) message = message.replace("%game_status%", game.getStateDescription());

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, String string) {
        String message = get(messageEnum);

        if (message.contains("%player%")) message = message.replace("%player%", string);
        if (message.contains("%input%")) message = message.replace("%input%", string);
        if (message.contains("%sign_ID%")) message = message.replace("%sign_ID%", string);
        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", string);
        if (message.contains("%loc_ID%")) message = message.replace("%loc_ID%", string);
        if (message.contains("%task_message%"))
            message = message.replace("%task_message%",
                    string.replaceFirst(("(\\W)(\\D)"), ""));
        if (message.contains("%question%")) message = message.replace("%question%", string);
        if (message.contains("%%correction_message%\n"))
            message = message.replace("%%correction_message%\n", (string.isEmpty()) ? "" : string + "\n");
        if (message.contains("%winners%")) message = message.replace("%winners%", string);
        if (message.contains("%winner%")) message = message.replace("%winner%", string);

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, String string, boolean defaultValue) {
        String message = get(messageEnum);

        if (message.contains("%field%")) message = message.replace("%field%", string);
        if (message.contains("%default_value%"))
            message = message.replace("%default_value%", String.valueOf(defaultValue));

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, String string, int gameID) {
        String message = get(messageEnum);

        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", String.valueOf(gameID));
        if (message.contains("%player%")) message = message.replace("%player%", string);
        if (message.contains("%sign_ID%")) message = message.replace("%sign_ID%", string);
        if (message.contains("%loc_ID%")) message = message.replace("%loc_ID%", string);
        if (message.contains("%winners%")) message = message.replace("%winners%", string);
        if (message.contains("%winner%")) message = message.replace("%winner%", string);

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, int integer) {
        String message = get(messageEnum);

        if (message.contains("%game_ID%")) message = message.replace("%game_ID%", String.valueOf(integer));
        if (message.contains("%round_countdown%"))
            message = message.replace("%round_countdown%", integer + " second" + (integer == 1 ? "" : "s"));
        if (message.contains("%countdown%"))
            message = message.replace("%countdown%", integer + " second" + (integer == 1 ? "" : "s"));
        if (message.contains("%line_number%")) message = message.replace("%line_number%", String.valueOf(integer));

        return message;
    }
}
