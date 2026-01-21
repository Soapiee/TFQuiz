package me.soapiee.common.instance;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.VersionManager;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.instance.cosmetic.Hologram;
import me.soapiee.common.instance.logic.Countdown;
import me.soapiee.common.instance.logic.Procedure;
import me.soapiee.common.instance.logic.TeleportTask;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.manager.GameSignManager;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.manager.SchedulerManager;
import me.soapiee.common.manager.SettingsManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Game {

    private final TFQuiz main;
    private final MessageManager messageManager;
    private final VersionManager specManager;
    private final SchedulerManager schedulerManager;
    private final SettingsManager settingsManager;
    private final GameSignManager gameSignManager;

    //Required options
    @Getter private GameState state;
    @Getter private final int identifier, maxPlayers, minPlayers, maxRounds, countdownSeconds;
    @Getter private final Reward reward;
    @Getter private boolean physicalArena;
    private final boolean enforceSurvival;
    private boolean forceStart;
    @Getter private final boolean broadcastWinners;

    //Arena options
    @Getter private String descType;
    @Getter private boolean allowSpectators;
    @Getter private Location spawn;
    @Getter private Hologram hologram;

    //Non-arena options
    @Getter private int schedulerDelay, schedulerResetterDelay;

    @Getter private final HashSet<Player> playingPlayers = new HashSet<>(), allPlayers = new HashSet<>();
    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private final HashSet<Player> spectators = new HashSet<>();
    @Getter private Countdown countdown;
    private Procedure procedure;

    public Game(TFQuiz main, Map<String, String> settings, Reward reward) {
        this.main = main;
        messageManager = main.getMessageManager();
        specManager = main.getVersionManager();
        settingsManager = main.getSettingsManager();
        gameSignManager = main.getGameSignManager();
        schedulerManager = main.getSchedulerManager();

        identifier = Integer.parseInt(settings.get("identifier"));
        state = GameState.valueOf(settings.get("initial_state").toUpperCase());
        maxPlayers = Integer.parseInt(settings.get("max_players"));
        minPlayers = Integer.parseInt(settings.get("min_players"));
        maxRounds = Integer.parseInt(settings.get("max_rounds"));
        countdownSeconds = Integer.parseInt(settings.get("countdown_seconds"));
        this.reward = reward;
        physicalArena = Boolean.parseBoolean(settings.get("physical_arena"));
        enforceSurvival = Boolean.parseBoolean(settings.get("enforce_survival"));
        forceStart = false;
        broadcastWinners = Boolean.parseBoolean(settings.get("broadcast_winners"));
        procedure = new Procedure(main, this);
        countdown = new Countdown(main, this, countdownSeconds);
    }

    public void setUpArenaOptions(String descType, boolean allowSpectators, Location spawn, Hologram hologram) {
        this.descType = descType;
        this.allowSpectators = allowSpectators;
        this.spawn = spawn;
        this.hologram = hologram;

        if (physicalArena && spawn == null) physicalArena = false;
    }

    public void setUpNonArenaOptions(int schedulerDelay, int resetDelay) {
        this.schedulerDelay = physicalArena ? -1 : schedulerDelay;
        this.schedulerResetterDelay = physicalArena ? -1 : resetDelay;
    }

    public void announceWinners() {
        int size = playingPlayers.size();

        //If there are no playing players left (does not include spectators)
        if (size == 0) {
            if (!broadcastWinners)
                sendMessage(messageManager.getWithPlaceholder(Message.GAMEMNOWINNERBROADCAST, identifier));
            else
                Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMNOWINNERBROADCAST, identifier)));
            return;
        }

        //If there is 1 player remaining (does not include spectators)
        if (size == 1) {
            Player player = playingPlayers.iterator().next();

            if (!broadcastWinners)
                sendMessage(messageManager.getWithPlaceholder(Message.GAMEMSINGLEPLAYERBROADCAST, player.getName(), identifier));
            else
                Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMSINGLEPLAYERBROADCAST, player.getName(), identifier)));
            return;
        }

        StringBuilder winners = new StringBuilder();
        int i = 0;

        for (Player player : playingPlayers) {
            if (i == size - 1) {
                winners.append(" and ").append(player.getName());
                break;
            }
            winners.append(player.getName());
            if (i > size - 2) {
                winners.append(", ");
            }
            i++;
        }

        if (!broadcastWinners)
            sendMessage(messageManager.getWithPlaceholder(Message.GAMEMULTIPLAYERBROADCAST, winners.toString(), identifier));
        else
            Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMULTIPLAYERBROADCAST, winners.toString(), identifier)));
    }

    public void start() {
        if (getHologram() != null) getHologram().despawn();

        if (schedulerManager.getScheduler(identifier) != null) schedulerManager.getScheduler(identifier).setPlayed();

        procedure.start();
    }

    public void reset(boolean kickPlayers, boolean removedMessage) {
        if (kickPlayers) {
            for (Player player : getAllPlayers()) {
                if (removedMessage)
                    player.sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEPLAYERREMOVEDTARGET, getIdentifier())));

                if (physicalArena) {
                    if (isSpectator(player)) {
                        removeSpectator(player);
                    }

                    new TeleportTask(player, settingsManager.getLobbySpawn()).runTaskLater(main, 1);

                    if (settingsManager.isClearInv()) restoreInventory(player);
                }
            }

            allPlayers.clear();
            playingPlayers.clear();
        }
        forceStart = false;
        sendTitle("", "");
        if (getHologram() != null) getHologram().despawn();

        if (countdown != null) {
            try {
                countdown.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
        countdown = new Countdown(main, this, countdownSeconds);

        if (schedulerManager.getScheduler(identifier) != null) {
            schedulerManager.newScheduler(this);
            setState(GameState.CLOSED);
        } else setState(GameState.RECRUITING);

        procedure.onReset();
        procedure.unregister();
        procedure = new Procedure(main, this);

        if (!descType.equalsIgnoreCase("chat")) {
            if (hologram.getSpawnPoint() != null) {
                getHologram().spawn();
            }
        }
    }

    public int addPlayer(Player player) {
        if (state == GameState.CLOSED || state == GameState.LIVE) return 2;
        if (enforceSurvival) if (player.getGameMode() != GameMode.SURVIVAL) return 1;
        if (allPlayers.size() == getMaxPlayers()) return 3;

        allPlayers.add(player);
        playingPlayers.add(player);

        player.sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEJOIN, this)));
        sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEOTHERJOINED, this, player.getName())), player);

        if (physicalArena) {
            new TeleportTask(player, spawn).runTaskLater(main, 1);
            saveInventory(player);
        }

        if (!descType.equalsIgnoreCase("hologram")) {
            player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEDESC)));
        }

        if (state == GameState.RECRUITING && allPlayers.size() >= getMinPlayers()) {
            countdown.start();
        } else { //so the sign isnt updated when the player joins AND when the game state changes
            updateSigns();
        }
        return 0;
    }

    public void removePlayer(Player player) {
        allPlayers.remove(player);
        playingPlayers.remove(player);
        player.sendTitle("", "", 0, 20, 0);

        if (physicalArena) {
            new TeleportTask(player, settingsManager.getLobbySpawn()).runTaskLater(main, 1);

            if (isSpectator(player)) {
                removeSpectator(player);
            }

            restoreInventory(player);
        }

        if (state != GameState.LIVE)
            sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEOTHERLEFT, this, player.getName())));


        if (allPlayers.size() < getMinPlayers()) {
            if (state == GameState.COUNTDOWN) {

                if (forceStart && !allPlayers.isEmpty()) {
                    updateSigns();
                    return;
                }

                if (!forceStart) {
                    sendMessage(messageManager.get(Message.GAMENOTENOUGH));
                }

                if (countdown != null) {
                    try {
                        countdown.cancel();
                    } catch (IllegalStateException ignored) {
                    }
                }
                countdown = new Countdown(main, this, countdownSeconds);
                setState(GameState.RECRUITING);
                return;
            }
        }
        updateSigns();
    }

    public void forceStart() {
        forceStart = true;
    }

    public void sendMessage(String message) {
        for (Player player : allPlayers) {
            Bukkit.getPlayer(player.getUniqueId()).sendMessage(Utils.addColour(message));
        }
    }

    public void sendMessage(String message, Player excludingPlayer) {
        for (Player player : allPlayers) {
            if (player == excludingPlayer) continue;
            Bukkit.getPlayer(player.getUniqueId()).sendMessage(Utils.addColour(message));
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (Player player : playingPlayers) {
            Bukkit.getPlayer(player.getUniqueId()).sendTitle(Utils.addColour(title), Utils.addColour(subtitle), 20, 20, 20);
            Bukkit.getPlayer(player.getUniqueId()).sendTitle(Utils.addColour(title), Utils.addColour(subtitle), 20, 20, 20);
        }
    }

    public void end() {
        procedure.setCommandEnd();
    }

    public String getStateDescription() {
        return messageManager.get(state);
    }

    public void setState(GameState state) {
        this.state = state;

        if (getSigns() != null && !getSigns().isEmpty()) {
            for (GameSign sign : getSigns()) {
                sign.update(getStateDescription());
            }
        }
    }

    public void addSpectator(Player player) {
        if (!specManager.setSpectator(player)) {
            removePlayer(player);
            player.sendMessage(Utils.addColour(messageManager.get(Message.GAMESPECTATORERROR)));
            return;
        }

        spectators.add(player);
        playingPlayers.remove(player);
        updateSigns();
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);

        if (player.isOnline()) specManager.unSetSpectator(player);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public void removePlayingPlayer(Player player) {
        playingPlayers.remove(player);
    }

    public void updateSigns() {
        if (getSigns() != null && !getSigns().isEmpty()) {
            for (GameSign sign : getSigns()) {
                sign.update(playingPlayers.size());
            }
        }
    }

    private void saveInventory(Player player) {
        ItemStack[] inv = player.getInventory().getContents();

        if (settingsManager.isClearInv()) {
            inventories.put(player.getUniqueId(), inv);
//            player.getInventory().clear(); //Broken in 1.21.6 spigot

            for (int i = 0; i < 41; i++) {
                player.getInventory().clear(i);
            }
        }

        if (main.getInventoryManager() != null) {
            main.getInventoryManager().savePlayer(player, inv);
        }
    }

    public ArrayList<GameSign> getSigns() {
        return (gameSignManager.getSigns(this) == null) ? null : gameSignManager.getSigns(this);
    }

    public void restoreInventory(Player player) {
        if (!inventories.containsKey(player.getUniqueId())) return;

//        player.getInventory().setContents(inventories.get(player.getUniqueId()));  //Broken in 1.21.6 spigot

        ItemStack[] savedInv = inventories.get(player.getUniqueId());
        for (int i = 0; i < 41; i++) {
            player.getInventory().setItem(i, savedInv[i]);
        }

        inventories.remove(player.getUniqueId());

        if (main.getInventoryManager() != null)
            main.getInventoryManager().removePlayer(player);
    }

    public String getSpawnString() {
        return "World: " + spawn.getWorld().getName() + " X=" + Math.round(spawn.getX()) + ", Y=" + Math.round(spawn.getY()) + ", Z=" + Math.round(spawn.getZ());
    }

    public void setSpawn(Location location) {
        spawn = location;

        FileConfiguration config = main.getConfig();
        config.set("games." + identifier + ".arena_options.spawn_point.world", location.getWorld().getName());
        config.set("games." + identifier + ".arena_options.spawn_point.x", location.getX());
        config.set("games." + identifier + ".arena_options.spawn_point.y", location.getY());
        config.set("games." + identifier + ".arena_options.spawn_point.z", location.getZ());
        config.set("games." + identifier + ".arena_options.spawn_point.yaw", location.getYaw());
        config.set("games." + identifier + ".arena_options.spawn_point.pitch", location.getPitch());

        main.saveConfig();
    }
}