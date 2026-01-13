package me.soapiee.common.enums;

public enum Message {

    //                    --->    GENERAL MESSAGES    <---
    CONSOLEUSAGEERROR("console_usage_error", "&cYou must be a player to use this command"),
    NOPERMISSION("no_permission", "&cYou do not have permission to use this command"),
    PLAYERNOTFOUND("player_not_found", "&cPlayer not found"),

    //                    --->    ADMIN COMMAND MESSAGES    <---
    UPDATEAVAILABLE("update_available", "&aThere is an update available for TFQuiz"),
    UNSUPPORTEDVERSION("unsupported_version", "&cUnsupported version detected. Plugin may not function correctly. Contact the developer to get your version supported"),
    DISABLESPECWARNING("disable_spectators_warning", "&c[TFQuiz] Unsupported NMS version detected. The Spectator system will be diminished." +
            "\nIts recommended that you disable it in the config. All other features will work as normal"),
    SPECTATORSYSTEMERROR("spectator_system_error", "&cERROR: The spectator system is not working. Contact the developer"),
    UNSUPPORTEDPLATFORMSPIGOT("unsupported_platform_spigot", "&c[TFQuiz] Incorrect server platform detected. This plugin is designed to be ran on Spigot."),
    UNSUPPORTEDPLATFORMPAPER("unsupported_platform_paper", "&c[TFQuiz] Incorrect server platform detected. This plugin is designed to be ran on Paper."),
    DOWNLOADSPIGOTJAR("download_spigotr_jar", "&c&c[TFQuiz] Please download the Spigot jar instead."),
    DOWNLOADPAPERJAR("download_paper_jar", "&c[TFQuiz] Please download the Paper jar instead."),
    ADMINCMDUSAGE("admin_command_usage",
            "&#01d54a--------- Admin Help ---------"
                    + "\n#01d54a/tf reload &7- Reloads the plugin"
                    + "\n#01d54a/tf list &7- Lists all loaded games"
                    + "\n#01d54a/tf setspawn &7- Sets the lobby spawn"
//                    + "\n#01d54a/tf addGame "
                    + "\n#01d54a/tf game <gameID> setSpawn &7- Sets the spawn point of a specified game"
                    + "\n#01d54a/tf game <gameID> setHoloSpawn &7- Sets the hologram location of a specified game"
                    + "\n#01d54a/tf game <gameID> open|close &7- Manually opens or closes a game"
                    + "\n#01d54a/tf game <gameID> start &7- Starts the countdown of a game"
                    + "\n#01d54a/tf game <gameID> end [-without] &7- Ends a game. Use the flag \"-without\" to instantly end without winners."
                    + "\n#01d54a/tf game <gameID> addPlayer|removePlayer &7- Adds|Removes a player from the game"
                    + "\n#01d54a/tf game <gameID> info &7- &7- Displays information based on the games setup"
//                    + "\n#01d54a/tf game <gameID> delete &7- Deletes a game"
                    + "\n#01d54a/tf sign add <gameID> &7- Adds a new game sign for the specified game"
                    + "\n#01d54a/tf sign remove <signID> &7- Deletes an game sign"
                    + "\n#01d54a/tf sign edit <signID> <lineNo> text... &7- Edits the text on the game sign "),
    ADMINRELOADCMDUSAGE("admin_reload_command_usage", "&cUsage: /tf reload"),
    ADMINRELOADSUCCESS("admin_reload_success", "&aSuccessfully reloaded TFQuiz"),
    ADMINRELOADERROR("admin_reload_error", "&cRan into an error whilst reloading TFQuiz"),
    ADMINRELOADINPROGRESS("admin_reload_inprogress", "&eReloading configuration..."),
    RELOADCONVOCANCEL("reload_convo_cancel", "&cYou have cancelled the reload"),
    RELOADCONVOINVALID("reload_convo_invalid", "&cPlease type 'CONFIRM' or 'CANCEL'"),
    RELOADCONVOSTART("reload_convo_start",
            "&cThis will force reset all games, all scheduled games, and return all players to the lobby spawn"
                    + "\n&cType 'confirm' to confirm the reload"
                    + "\n&cThis will time out in 10 seconds"),
    INVALIDNUMBER("invalid_number_input", "&c%input% is not a valid number"),
    NOGAMESSET("games_error", "&cPlease set up some games in the config.yml"),
    NOQUESTIONSSET("questions_error", "&cCould not find any questions inside questions.yml"),
    QUESTIONSFILEERROR("questions_file_error", "&cThe questions.yml could not be loaded"),
    LOGGERFILEERROR("logger_file_error", "&cThe logger.log file could not be created"),
    LOGGERLOGSUCCESS("logger_log_success", "&cAn error was added to the logger.log file"),
    LOGGERLOGERROR("logger_log_error", "&cThe logger.log file could not be created"),
    INVENTORIESFILEERROR("inventories_file_error", "&cThe playerInventories.yml could not be loaded"),
    INVENTORIESSAVEERROR("inventories_saving_error", "&cThere was an error saving %player%'s inventory to playerInventories.yml"),
    INVENTORIESREMOVEERROR("inventories_removing_error", "&cThere was an error removing %player%'s inventory to playerInventories.yml"),
    MESSAGESFILEERROR("messages_file_error", "&cThe messages.yml could not be loaded"),
    MESSAGESFIELDERROR("messages_field_error", "&cCould not add new fields to messages.yml"),
    LOADSETTINGSERROR("settings_error", "&cThe config field &e%field% &cis not set. Defaulting to &e%default_value%"),
    INVALIDLOBBYSPAWN("lobby_spawn_error", "&cYou must set a lobby spawn point in the config"),
    INVALIDREWARDTYPE("invalid_reward_type", "&cInvalid reward type for game &e%game_ID%&c. No reward will be given."),
    INVALIDREWARDMESSAGE("missing_reward_message", "&cThe reward message for game &e%game_ID%&c is missing. No message will be sent to the player."),
    INVALIDVAULTHOOK("missing_vault_hook", "&cThe reward for game &e%game_ID%&c requires Vault to be installed."),
    INVALIDREWARDNUMBER("invalid_reward_number", "&cYou must input a number for the reward amount of game &e%game_ID%"),
    INVALIDREWARDAMOUNT("invalid_reward_amount", "&cThe amount for the reward of game &e%game_ID% &cis to low"),
    INVALIDREWARD("missing_reward", "&cThere is no reward defined for game &e%game_ID%"),
    INVALIDREWARDMATERIAL("invalid_reward_material", "&cThe reward for game &e%game_ID%&c has an invalid material"),
    INVALIDREWARDITEM("invalid_reward_item", "&cThe reward for game &e%game_ID%&c has an invalid item. Follow this format: \"apple, 5\" (material, quantity)"),
    INVALIDGAMESPAWN("invalid_game_spawn", "&cThe spawn point for game &e%game_ID% &cis invalid."),
    ADMINLISTCMDUSAGE("admin_list_command_usage", "&cUsage: /tf list"),
    ADMINSETLOBBYSPAWNCMDUSAGE("admin_set_lobbyspawn_command_usage", "&cUsage: /tf setspawn"),
    ADMINSETLOBBYSPAWN("admin_lobby_spawn_set", "&aThe Lobby spawn has been set"),
    GAMEADMINCMDUSAGE("game_admin_command_usage",
            "&#01d54a--------- Game Help ---------"
                    + "\n#01d54a/tf game <gameID> setSpawn &7- Sets the spawn point of a specified game"
                    + "\n#01d54a/tf game <gameID> setHoloSpawn &7- Sets the hologram location of a specified game"
                    + "\n#01d54a/tf game <gameID> open|close &7- Manually opens or closes a game"
                    + "\n#01d54a/tf game <gameID> start &7- Starts the countdown of a game"
                    + "\n#01d54a/tf game <gameID> end [-without] &7- Ends a game. Use the flag \"-without\" to instantly end without winners."
                    + "\n#01d54a/tf game <gameID> addPlayer|removePlayer <player> &7- Adds|Removes a player from the game"
                    + "\n#01d54a/tf game <gameID> info &7- &7- Displays information based on the games setup"),
    GAMEFORCESTARTED("game_force_started", "&aGame &e(%game_ID%) &acountdown has begun!"),
    GAMEFORCESTARTERROR("game_force_start_error", "&cError trying to start this game. It is already in play"),
    GAMESTARTSCHEDULERERROR("game_start_scheduler_error", "&cYou cannot manually start a game that has an active scheduler"),
    GAMESTARTCLOSEDERROR("game_start_closed_error", "&cError trying to start this game. It is closed. Use /tf game <ID> open"),
    GAMESTARTEMPTYERROR("game_start_empty_error", "&cError trying to start this game. There are no players in this game"),
    GAMEFORCEENDED("game_force_ended", "&aGame &e(%game_ID%) &ahas been ended"),
    GAMEFORCEENDEDWITHWINNERS("game_force_ended_with_winners", "&aGame &e(%game_ID%) &awill end at the end of the round"),
    GAMEFORCEENDERROR("game_force_end_error", "&cError trying to end this game. It is not currently in play"),
    GAMEENDSCHEDULERERROR("game_end_scheduler_error", "&cYou cannot manually end a game that has an active scheduler"),
    GAMEPLAYERREMOVEDTARGET("game_player_removed_target", "&cYou have been removed from game %game_ID%"),
    GAMESPAWNSET("game_spawn_set", "&aThe spawn has been set for this game"),
    GAMEHOLOSPAWNSET("game_holo_spawn_set", "&aYou set the hologram spawn point"),
    //    GAMEHOLODESPAWNED("game_holo_despawned", "&a%holo_count% holograms were removed"),
//    GAMEHOLOSPAWNREMOVED("game_holo_spawn_removed", "&aYou have removed the hologram spawn point"),
    GAMEOPENED("game_opened", "&aGame %game_ID% has been opened to players"),
    GAMEOPENEDERROR("game_opened_error", "&cGame %game_ID% is already active"),
    GAMEOPENEDERROR2("game_opened_scheduler_error", "&cYou cannot manually open a game that has an active scheduler"),
    GAMECLOSED("game_closed", "&cGame %game_ID% is no longer accepting players"),
    GAMECLOSEDERROR("game_closed_error", "&cGame %game_ID% cannot be closed right now"),
    GAMECLOSEDERROR2("game_closed_scheduler_error", "&cYou cannot manually close a game that has an active scheduler"),
    GAMEPLAYERADDED("game_player_added", "&e%player% &ahas been added to game %game_ID%"),
    GAMEPLAYERADDEDERROR("game_player_added_error", "&cThis game is not currently accepting new players"),
    GAMEINVALIDGAMEMODEOTHER("game_invalid_gamemode_error_other", "&cThe player must be in survival mode to join this game"),
    GAMEPLAYERALREADYINGAME("game_player_already_in_game", "&e%player% &cis already in an game"),
    GAMEPLAYERREMOVED("game_player_removed", "&e%player% &ahas been removed from game %game_ID%"),
    GAMEPLAYERNOTINGAME("game_player_not_in_game", "&e%player% &cis not in that game"),
    GAMEINFO("game_info",
            "#01d54a--------- Game %game_ID% Info ---------"
                    + "\n#01d54aCurrent Status: &7%game_players%/%game_maxplayers% - %game_status%"
                    + "\n#01d54aMin required players: &7%game_minplayers%"
                    + "\n#01d54aCountdown: &7%game_countdown%"
                    + "\n#01d54aMax rounds: &7%game_maxrounds%"
                    + "\n#01d54aBroadcasts winner: &7%game_doesbroadcast%"
                    + "\n#01d54aReward: &7%game_reward%"
                    + "\n#01d54a "
                    + "\n#01d54aArena Specifics"
                    + "\n#01d54aHas arena: &7%game_hasarena%"
                    + "\n#01d54aHas scheduler: &7%game_hasscheduler% &7%game_schedulerseconds%"
                    + "\n#01d54aDescription option: &7%game_desc%"
                    + "\n#01d54aSpectators: &7%game_doesspectators%"
                    + "\n#01d54aHologram: &7%game_holocoordinates%"
                    + "\n#01d54aSpawn point: &7%game_spawncoordinates%"),

    //                    --->    GAME MESSAGES    <---
    GAMELISTHEADER("game_list_header", "--------- Games List ---------"),
    GAMELIST("game_list_format", "&e> %game_ID% &7(%game_players%/%game_maxplayers%) &e- %game_status%"),
    GAMELISTHOVER("game_list_hover_text", "&eClick to join"),
    GAMEOPENEDSCHEDULER("game_opened_scheduler", "&eA new quiz game has opened. Use &a/game join %game_ID% &e to join the fun"),
    GAMESOPENSCHEDULERHOVER("game_open_scheduler_hover", "&eClick to join"),
    GAMECLOSEDSCHEDULER("game_closed_scheduler", "&eThe quiz &7(%game_ID%) &eis now closed as not enough players joined."),
    GAMEJOIN("game_join", "&aYou have joined game %game_ID% &7(%game_players%/%game_minplayers%)"),
    GAMEOTHERJOINED("game_other_player_join", "&a%player% joined the game &7(%game_players%/%game_minplayers%)"),
    GAMEINVALIDGAMEID("game_invalid_gameID", "&cYou must enter a valid gameID"),
    GAMEINVALIDSTATE("game_invalid_state_error", "&cThis game is not currently accepting players"),
    GAMEINVALIDGAMEMODE("game_invalid_gamemode_error", "&cYou must be in survival mode to join this game"),
    FORCEDGAMELEAVE("forced_game_leave", "&cAn admin removed you from game %game_ID%"),
    GAMEFULL("game_full_error", "&cThis game is full"),
    GAMENOTNULL("already_in_game_error", "&cYou are already in a game"),
    GAMELEAVE("game_leave", "&cYou have left the game"),
    GAMEOTHERLEFT("game_other_player_left", "&c%player% left the game &7(%game_players%/%game_minplayers%)"),
    GAMELEFTERROR("error_game_leave", "&cYou are not in an game"),
    GAMECOUNTDOWNSTART("game_countdown_start", "&aThe game is starting in %countdown%"),
    GAMECOUNTDOWNTITLEPREFIX("game_countdown_title_prefix", "&a%countdown%"),
    GAMECOUNTDOWNTITLESUFFIX("game_countdown_title_suffix", "&euntil the game starts"),
    GAMESTARTED("game_started", "&a&lTrue/False Game started\""),
    GAMEDESC("game_description", "Answer the questions correctly with either true or false."),
    GAMEHOLODESC("game_hologram_desc",
            "&e-----------------------------------"
                    + "\n&eAnswer the questions correctly"
                    + "\n&eYou can only answer with True or False"
                    + "\n&e-----------------------------------"),
    GAMEROUNDCOUNTDOWN("game_round_countdown_message", "&aYou have %round_countdown% to answer the question"),
    GAMEROUNDCOUNTDOWNTITLEPREFIX("game_round_countdown_title_prefix", "&a%round_countdown%"),
    GAMEROUNDCOUNTDOWNTITLESUFFIX("game_round_countdown_title_suffix", "&euntil the round ends"),
    GAMENOTENOUGH("game_not_enough_players", "&cThere are not enough players. Countdown has stopped"),
    GAMEBELOWMIN("game_below_min_required", "&cThe game has ended as too many players have left"),
    GAMEDISALLOWEDCMD("game_disallowed_command", "&cYou cannot use that command when in a game with an arena"),
    GAMEPROMPT("game_prompt",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&6&lTrue or False? %question%"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMETRUEOUTCOME("game_true_outcome_message",
            "&e-----------------------------------"
                    + "\n&eThe correct answer was"
                    + "\n"
                    + "\n&aTRUE"
                    + "\n&e%correction_message%"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEFALSEOUTCOME("game_false_outcome_message",
            "&e-----------------------------------"
                    + "\n&eThe correct answer was"
                    + "\n"
                    + "\n&cFALSE"
                    + "\n&e%correction_message%"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEELIMMESSAGE("game_eliminate_message",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&cYou have been eliminated!"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMECONTINUEDMESSAGE("game_continued_message",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&aWell done! You continue to the next round.."
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEWINMESSAGE("game_win_message",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&aCongratulations! You have won!"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEMULTIPLAYERBROADCAST("game_multi_player_broadcast",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&a%winners% have all won &7(Game: %game_ID%)&a!"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEMSINGLEPLAYERBROADCAST("game_single_player_broadcast",
            "&e-----------------------------------"
                    + "\n "
                    + "\n&a%winner% has won &7(Game: %game_ID%)&a!"
                    + "\n "
                    + "\n&e-----------------------------------"),
    GAMEMNOWINNERBROADCAST("game_no_winner_broadcast", "&e-----------------------------------"
            + "\n "
            + "\n&cThere were no winners as all players have been eliminated"
            + "\n&cThe game &7(%game_ID%) &chas ended"
            + "\n "
            + "\n&e-----------------------------------"),
    GAMEITEMWINERROR("game_item_win_error", "&cYou didn't have enough space in your inventory to receive your reward, so it was dropped on the floor."),
    GAMECCMDUSAGE("game_command_usage", "&cUsage: /game <join | leave | list> <gameID>"),
    GAMEJOINCMDUSAGE("game_join_command_usage", "&cUsage: /game join <gameID>"),
    GAMESPECTATORERROR("game_spectator_error", "&cThere was an error setting you as a spectator. You have been removed from the arena"),
    GAMECREATEDSUCCESS("game_created", "&aGame &e%game_ID% &awas successfully created"),

    //                    --->    SIGN MESSAGES    <---
    SIGNSUNSUPPORTEDVERSION("signs_unsupported_version", "Game signs are unsupported on this version. Please remove them"),
    SIGNADMINCMDUSAGE("sign_admin_command_usage",
            "#01d54a--------- Sign Help ---------"
                    + "\n#01d54a/tf sign add <gameID> &7- Adds a new game sign for the specified game"
                    + "\n#01d54a/tf sign remove &7- Deletes a game sign that you are looking at"
                    + "\n#01d54a/tf sign remove <signID> &7- Deletes a game sign with that specified sign ID"
                    + "\n#01d54a/tf sign edit <lineNo> \"Text...\" &7- Edits the text on the game sign you are looking at"
                    + "\n#01d54a/tf sign edit <signID> <lineNo> \"Text...\" &7- Edits the text on the game sign with that specified sign ID"),
    SIGNLISTHEADER("sign_list_header", "--------- Signs List ---------"),
    SIGNLISTFORMAT("sign_list_format", "&e> (Game ID: %game_ID%) Sign ID: %sign_ID%"),
    SIGNLISTHOVER("sign_list_hover", "&eClick to teleport to sign"),
    SIGNEDITIDCMDUSAGE("sign_id_edit_command_usage", "#01d54a/tf sign edit <signID> <line> \"new text..\""),
    SIGNEDITCMDUSAGE("sign_edit_command_usage", "#01d54a/tf sign edit <line> \"new text..\""),
    SIGNADDED("sign_added", "&aYou added a new game sign &7(to game: %game_ID%)"),
    SIGNREMOVED("sign_removed", "&aYou removed an game sign &7(signID: %sign_ID%)"),
    SIGNEDITED("sign_edited", "&aYou sucessfully edited an game sign &7(signID: %sign_ID%)"),
    SIGNINVALIDLINENUM("sign_invalid_line_number", "&cEnter a valid line number (1-4)"),
    SIGNINVALIDSIGNID("sign_invalid_signID", "&cYou must input a valid signID"),
    SIGNNOTLOOKINGATGAMESIGN("not_looking_at_game_sign", "&cYou are not looking at an game sign"),
    SIGNNOTLOOKINGATSIGN("not_looking_at_sign", "&cYou are not looking at a sign"),
    SIGNALREADYEXISTS("sign_already_exists", "&cThis sign is already an game sign"),
    INVALIDSIGNGAME("sign_game_id_error", "&cSkipping sign with ID &e%sign_ID% &cdue to invalid game ID"),
    INVALIDSIGNMATERIAL("sign_material_error", "&cInvalid sign material for sign &e%sign_ID%&c. Defaulting to OAK_SIGN"),
    INVALIDSIGNLOCATION("sign_location_error", "&cSkipping sign with ID &e%sign_ID% &cdue to invalid world"),
    SIGNCREATEDSUCCESS("sign_created", "&aGame sign &e%sign_ID% &awas successfully created"),

    SIGNCONVOSTART("sign_convo_start", "&eWelcome to the GameSign text editor."),
    SIGNCONVOLINEPROMPT("sign_convo_line_prompt", "&eWhich line would you like to edit? Options: 1 - 4 &7(Type 'EXIT' to exit the editor)"),
    SIGNCONVOLINEINVALID("sign_convo_line_invalid", "&cInput must be between 1 and 4"),
    SIGNCONVOTEXTPROMPT("sign_convo_text_prompt", "&ePlease state the text you want to put on line &6&l%line_number%");

    public final String path;
    private final String defaultText;

    Message(String path, String defaultText) {
        this.path = path;
        this.defaultText = defaultText;
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return defaultText;
    }
}
