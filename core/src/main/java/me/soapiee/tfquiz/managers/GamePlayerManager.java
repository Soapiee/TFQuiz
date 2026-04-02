package me.soapiee.tfquiz.managers;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.instance.Game;
import me.soapiee.tfquiz.internals.TabUpdate;
import org.bukkit.entity.Player;

import java.util.*;

public class GamePlayerManager {

    private final Map<Integer, Set<UUID>> playingPlayers = new HashMap<>(), allPlayers = new HashMap<>();
    private final Map<Integer, Set<UUID>> spectators = new HashMap<>();
    private final Set<UUID> allSpectators = new HashSet<>();

    public GamePlayerManager() {
    }

    public void addGame(int gameID) {
        playingPlayers.put(gameID, new HashSet<>());
        allPlayers.put(gameID, new HashSet<>());
        spectators.put(gameID, new HashSet<>());
    }

    public void addPlayer(Game game, UUID uuid) {
        int gameID = game.getIdentifier();
        playingPlayers.get(gameID).add(uuid);
        allPlayers.get(gameID).add(uuid);
    }

    public void removePlayer(Game game, UUID uuid) {
        int gameID = game.getIdentifier();
        playingPlayers.get(gameID).remove(uuid);
        allPlayers.get(gameID).remove(uuid);
    }

    public void eliminatePlayer(int gameID, UUID uuid) {
        playingPlayers.get(gameID).remove(uuid);
    }

    public void addSpectator(int gameID, UUID uuid) {
        eliminatePlayer(gameID, uuid);
        spectators.get(gameID).add(uuid);
        allSpectators.add(uuid);
    }

    public void addFakeSpectator(UUID uuid) {
        allSpectators.add(uuid);
    }

    public void removeFakeSpectator(UUID uuid) {
        allSpectators.remove(uuid);
    }

    public void removeSpectator(int gameID, UUID uuid) {
        Set<UUID> spectatorsList = spectators.get(gameID);
        spectatorsList.remove(uuid);
        allSpectators.remove(uuid);
    }

    public Set<UUID> getPlayingPlayers(int gameID) {
        return Collections.unmodifiableSet(playingPlayers.get(gameID));
    }

    public Set<UUID> getAllPlayers(int gameID) {
        return Collections.unmodifiableSet(allPlayers.get(gameID));
    }

    public boolean isSpectator(int gameID, UUID uuid) {
        return spectators.get(gameID).contains(uuid);
    }

    public boolean spectatorsExist() {
        return !allSpectators.isEmpty();
    }

    public void updateTab(TFQuiz main, Player player) {
        new TabUpdate(main.getVersionManager().getSpectatorHandler(), player, allSpectators).runTaskLater(main, 10);
    }

    private void clearAllPlayers(int gameID) {
        allPlayers.get(gameID).clear();
    }

    private void clearPlayingPlayers(int gameID) {
        playingPlayers.get(gameID).clear();
    }

    public void resetPlayers(int gameID) {
        clearAllPlayers(gameID);
        clearPlayingPlayers(gameID);
    }
}
