package me.soapiee.common.manager;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.logic.Scheduler;
import me.soapiee.common.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final TFQuiz main;
    private final Logger logger;
    private final File file;
    private final YamlConfiguration contents;

    public MessageManager(TFQuiz main) {
        this.main = main;
        logger = main.getCustomLogger();
        file = new File(main.getDataFolder(), "messages.yml");
        contents = new YamlConfiguration();

        load(null);
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        if (!file.exists()) {
            main.saveResource("messages.yml", false);
        }

        try {
            contents.load(file);
        } catch (Exception ex) {
            if (sender != null) {
                logger.logToPlayer(sender, ex, "Could not load the messages.yml file");
            }
            return true;
        }
        return false;
    }

    public void save() {
        try {
            contents.save(file);
        } catch (Exception ex) {
            logger.logToFile(ex, "Could not add new fields to messages.yml");
        }
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
        return get(messageEnum).replace("%game_ID%", String.valueOf(game.getIdentifier()))
                .replace("%game_players%", String.valueOf(game.getAllPlayers().size()))
                .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()))
                .replace("%game_minplayers%", String.valueOf(game.getMinPlayers()))
                .replace("%game_status%", game.getStateDescription());
    }

    public String getInfo(Message messageEnum, Game game) {
//        String holoLoc;
//        if (game.getHologram().getLocation() == null) holoLoc = "not set";
//        else holoLoc = "x y z";

        Scheduler scheduler = main.getSchedulerManager().getScheduler(game.getIdentifier());

        return get(messageEnum).replace("%game_ID%", String.valueOf(game.getIdentifier()))
                .replace("%game_players%", String.valueOf(game.getAllPlayers().size()))
                .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()))
                .replace("%game_minplayers%", String.valueOf(game.getMinPlayers()))
                .replace("%game_countdown%", String.valueOf(game.getCountdown().getTotalSeconds()))
                .replace("%game_maxrounds%", String.valueOf(game.getMaxRounds()))
                .replace("%game_doesbroadcast%", String.valueOf(game.isBroadcastWinners()))
                .replace("%game_reward%", game.getReward().toString())
                .replace("%game_hasarena%", String.valueOf(game.isPhysicalArena()))
                .replace("%game_hasscheduler%",
                        (scheduler == null) ? "false" : "true")
                .replace("%game_schedulerseconds%",
                        (scheduler == null) ? "" : String.valueOf(scheduler.getRemainingTime()))
                .replace("%game_desc%", game.getDescType())
                .replace("%game_doesspectators%", String.valueOf(game.isAllowSpectators()))
                .replace("%game_holocoordinates%",
                        (game.getHologram() == null || game.getHologram().getLocationString() == null) ? "not set"
                                : game.getHologram().getLocationString())
                .replace("%game_spawncoordinates%",
                        (game.getSpawn() == null) ? "not set"
                                : game.getSpawnString())
                .replace("%game_status%", game.getStateDescription());
    }

    public String getWithPlaceholder(Message messageEnum, Game game, String string) {
        return get(messageEnum).replace("%game_ID%", String.valueOf(game.getIdentifier()))
                .replace("%game_players%", String.valueOf(game.getAllPlayers().size()))
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
