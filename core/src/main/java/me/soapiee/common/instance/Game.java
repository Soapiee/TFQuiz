package me.soapiee.common.instance;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.VersionManager;
import me.soapiee.common.enums.AddPlayerResult;
import me.soapiee.common.enums.DescriptionType;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.instance.cosmetic.Hologram;
import me.soapiee.common.instance.logic.GameLifecycle;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.managers.GameSignManager;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.managers.SchedulerManager;
import me.soapiee.common.managers.SettingsManager;
import me.soapiee.common.tasks.Countdown;
import me.soapiee.common.tasks.TeleportTask;
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
    @Getter private DescriptionType descType;
    @Getter private boolean allowSpectators;
    @Getter private Location spawn;
    @Getter private Hologram hologram;

    //Non-arena options
    @Getter private int schedulerDelay, schedulerResetterDelay;

    @Getter private final Set<UUID> playingPlayers = new HashSet<>(), allPlayers = new HashSet<>();
    private final Map<UUID, ItemStack[]> inventories = new HashMap<>();
    private final Set<UUID> spectators = new HashSet<>();
    @Getter private Countdown countdown;
    private GameLifecycle gameLifecycle;

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
        gameLifecycle = new GameLifecycle(main, this);
        countdown = new Countdown(main, this, countdownSeconds);
    }

    public void setUpArenaOptions(DescriptionType descType, boolean allowSpectators, Location spawn, Hologram hologram) {
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
            UUID uuid = playingPlayers.iterator().next();

            if (!broadcastWinners)
                sendMessage(messageManager.getWithPlaceholder(Message.GAMEMSINGLEPLAYERBROADCAST, Bukkit.getPlayer(uuid).getName(), identifier));
            else
                Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMSINGLEPLAYERBROADCAST, Bukkit.getPlayer(uuid).getName(), identifier)));
            return;
        }

        StringBuilder winners = new StringBuilder();
        int i = 0;

        for (UUID uuid : playingPlayers) {
            if (i == size - 1) {
                winners.append(" and ").append(Bukkit.getPlayer(uuid).getName());
                break;
            }
            winners.append(Bukkit.getPlayer(uuid).getName());
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

        gameLifecycle.start();
    }

    public void reset(boolean kickPlayers, boolean removedMessage) {
        if (kickPlayers) {
            for (UUID uuid : getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (removedMessage)
                    player.sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEPLAYERREMOVEDTARGET, getIdentifier())));

                if (physicalArena) {
                    if (isSpectator(uuid)) {
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

        gameLifecycle.onReset();
        gameLifecycle.unregister();
        gameLifecycle = new GameLifecycle(main, this);

        if (descType != DescriptionType.CHAT) {
            if (hologram.getSpawnPoint() != null) {
                getHologram().spawn();
            }
        }
    }

    public AddPlayerResult addPlayer(UUID uuid) {
        if (state == GameState.CLOSED || state == GameState.LIVE) return AddPlayerResult.GAME_CLOSED;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return AddPlayerResult.PLAYER_NOT_FOUND;

        if (enforceSurvival) if (player.getGameMode() != GameMode.SURVIVAL) return AddPlayerResult.NOT_SURVIVAL;
        if (allPlayers.size() == getMaxPlayers()) return AddPlayerResult.GAME_FULL;

        allPlayers.add(uuid);
        playingPlayers.add(uuid);

//        player.sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEJOIN, this)));
        sendMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEOTHERJOINED, this, player.getName())), uuid);

        if (physicalArena) {
            new TeleportTask(player, spawn).runTaskLater(main, 1);
            saveInventory(player);
        }

        if (descType != DescriptionType.HOLOGRAM)
            player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEDESC)));

        if (state == GameState.RECRUITING && allPlayers.size() >= getMinPlayers()) {
            countdown.start();
        } else { //so the sign isnt updated when the player joins AND when the game state changes
            updateSigns();
        }

        return AddPlayerResult.SUCCESS;
    }

    public void removePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        allPlayers.remove(uuid);
        playingPlayers.remove(uuid);
        player.sendTitle("", "", 0, 20, 0);

        if (physicalArena) {
            new TeleportTask(player, settingsManager.getLobbySpawn()).runTaskLater(main, 1);

            if (isSpectator(uuid)) {
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
        for (UUID uuid : allPlayers) {
            Bukkit.getPlayer(uuid).sendMessage(Utils.addColour(message));
        }
    }

    // Test excluding player UUID comparison
    public void sendMessage(String message, UUID excludingPlayer) {
        for (UUID uuid : allPlayers) {
            if (uuid == excludingPlayer) continue;
            Bukkit.getPlayer(uuid).sendMessage(Utils.addColour(message));
        }
    }

    // Why is the title sent twice?
    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : playingPlayers) {
            Bukkit.getPlayer(uuid).sendTitle(Utils.addColour(title), Utils.addColour(subtitle), 20, 20, 20);
//            player.sendTitle(Utils.addColour(title), Utils.addColour(subtitle), 20, 20, 20);
        }
    }

    public void end() {
        gameLifecycle.setCommandEnd();
    }

    public String getStateDescription() {
        return messageManager.get(state);
    }

    public void setState(GameState state) {
        this.state = state;

        if (!getSigns().isEmpty())
            for (GameSign sign : getSigns()) sign.update(getStateDescription());
    }

    public void addSpectator(Player player) {
        UUID uuid = player.getUniqueId();
        if (!specManager.setSpectator(player)) {
            removePlayer(uuid);
            player.sendMessage(Utils.addColour(messageManager.get(Message.GAMESPECTATORERROR)));
            return;
        }

        spectators.add(uuid);
        playingPlayers.remove(uuid);
        updateSigns();
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        if (player.isOnline()) specManager.unSetSpectator(player);
    }

    public boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public void removePlayingPlayer(UUID uuid) {
        playingPlayers.remove(uuid);
    }

    public void updateSigns() {
        if (!getSigns().isEmpty())
            for (GameSign sign : getSigns()) sign.update(playingPlayers.size());
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

    public List<GameSign> getSigns() {
        return (gameSignManager.getSigns(this) == null) ? Collections.emptyList() : gameSignManager.getSigns(this);
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