package me.soapiee.common.managers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Languages;
import me.soapiee.common.enums.Message;
import me.soapiee.common.handlers.ArenaHandler;
import me.soapiee.common.instance.Game;
import me.soapiee.common.tasks.Scheduler;
import org.bukkit.Bukkit;
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
        language = validateLanguage();
        file = new File(main.getDataFolder() + File.separator + "language", language + ".yml");
        contents = new YamlConfiguration();

        load(null);
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        if (!file.exists()) {
            main.saveResource("language" + File.separator + language + ".yml", false);
        }

        try {
            contents.load(file);
        } catch (Exception ex) {
            if (sender != null) {
//                Utils.consoleMsg(get(Message.LANGUAGEFILEERROR));
                Bukkit.getLogger().throwing("Message Manager", "load()", ex);
            }
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
        String configString = main.getConfig().getString("language", "null");

        Languages lang;
        try {
            lang = Languages.valueOf(configString.toUpperCase());
        } catch (IllegalArgumentException error) {
//            Utils.consoleMsg(get(Message.INVALIDLANGUAGE));
            lang = Languages.LANG_EN;
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
        return get(messageEnum).replace("%game_ID%", String.valueOf(gameID))
                .replace("%game_players%", String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()))
                .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()))
                .replace("%game_minplayers%", String.valueOf(game.getMinPlayers()))
                .replace("%game_status%", game.getStateDescription());
    }

    public String getInfo(Message messageEnum, Game game) {
        Scheduler scheduler = main.getSchedulerManager().getScheduler(game.getIdentifier());
        int gameID = game.getIdentifier();
        ArenaHandler arenaHandler = game.getArenaHandler();

        return get(messageEnum).replace("%game_ID%", String.valueOf(gameID))
                .replace("%game_players%", String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()))
                .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()))
                .replace("%game_minplayers%", String.valueOf(game.getMinPlayers()))
                .replace("%game_countdown%", String.valueOf(game.getLifeCycleHandler().getCountdown().getTotalSeconds()))
                .replace("%game_maxrounds%", String.valueOf(game.getMaxRounds()))
                .replace("%game_doesbroadcast%", String.valueOf(game.isBroadcastWinners()))
                .replace("%game_reward%", game.getReward().toString())
                .replace("%game_hasarena%", String.valueOf(game.isPhysicalArena()))
                .replace("%game_hasscheduler%",
                        (scheduler == null) ? "false" : "true")
                .replace("%game_schedulerseconds%",
                        (scheduler == null) ? "" : String.valueOf(scheduler.getRemainingTime()))
                .replace("%game_desc%", arenaHandler.getDescString())
                .replace("%game_doesspectators%", String.valueOf(arenaHandler.isAllowSpectators()))
                .replace("%game_holocoordinates%",
                        (arenaHandler.getHologram().getSpawnPoint() == null) ? "not set"
                                : arenaHandler.getHologram().getLocationString())
                .replace("%game_spawncoordinates%", arenaHandler.getSpawnString())
                .replace("%game_status%", game.getStateDescription());
    }

    public String getWithPlaceholder(Message messageEnum, Game game, String string) {
        int gameID = game.getIdentifier();
        return get(messageEnum).replace("%game_ID%", String.valueOf(gameID))
                .replace("%game_players%", String.valueOf(main.getGamePlayerManager().getAllPlayers(gameID).size()))
                .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()))
                .replace("%game_minplayers%", String.valueOf(game.getMinPlayers()))
                .replace("%player%", string)
                .replace("%game_status%", game.getStateDescription());
    }

    public String getWithPlaceholder(Message messageEnum, String string) {
        return get(messageEnum).replace("%player%", string)
                .replace("%input%", string)
                .replace("%sign_ID%", string)
                .replace("%game_ID%", string)
                .replace("%loc_ID%", string)
                .replace("%task_message%", string.replaceFirst(("(\\W)(\\D)"), ""))
                .replace("%question%", string)
                .replace("%correction_message%\n", (string.isEmpty()) ? "" : string + "\n")
                .replace("%winners%", string)
                .replace("%winner%", string);
    }

    public String getWithPlaceholder(Message messageEnum, String string, boolean defaultValue) {
        return get(messageEnum).replace("%field%", string)
                .replace("%default_value%", String.valueOf(defaultValue));
    }

    public String getWithPlaceholder(Message messageEnum, String string, int gameID) {
        return get(messageEnum).replace("%player%", string)
                .replace("%sign_ID%", string)
                .replace("%%loc_ID%", string)
                .replace("%game_ID%", String.valueOf(gameID))
                .replace("%winners%", string)
                .replace("%winner%", string);
    }

    public String getWithPlaceholder(Message messageEnum, int integer) {
        String replacement = integer + " second" + (integer == 1 ? "" : "s");

        return get(messageEnum).replace("%countdown%", replacement)
                .replace("%round_countdown%", replacement)
                .replace("%game_ID%", String.valueOf(integer))
                .replace("%line_number%", String.valueOf(integer));
    }
}
