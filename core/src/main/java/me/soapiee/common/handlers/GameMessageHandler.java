package me.soapiee.common.handlers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class GameMessageHandler {

    private final MessageManager messageManager;
    private final GamePlayerManager gamePlayerManager;
    private final Game game;
    private final int gameID;

    public GameMessageHandler(TFQuiz main, Game game) {
        this.game = game;
        gameID = game.getIdentifier();
        messageManager = main.getMessageManager();
        gamePlayerManager = main.getGamePlayerManager();
    }

    public void successfullyJoined(Player excludingPlayer) {
        String message = messageManager.getWithPlaceholder(Message.GAMEOTHERJOINED, game, excludingPlayer.getName());

        for (UUID uuid : gamePlayerManager.getAllPlayers(gameID)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (player == excludingPlayer) continue;
            sendMessage(player, message);
        }
    }

    public void otherPlayerLeft(Player player) {
        String message = messageManager.getWithPlaceholder(Message.GAMEOTHERLEFT, game, player.getName());
        sendMessageToAll(message);
    }

    public void gameStarted() {
        String message = messageManager.get(Message.GAMESTARTED);
        sendMessageToAll(message);
    }

    public void eliminated(Player player) {
        String message = messageManager.get(Message.GAMEELIMMESSAGE);
        sendMessage(player, message);
    }

    public void survived(Player player) {
        String message = messageManager.get(Message.GAMECONTINUEDMESSAGE);
        sendMessage(player, message);
    }

    public void removedFromGame(Player player) {
        String message = messageManager.getWithPlaceholder(Message.GAMEPLAYERREMOVEDTARGET, gameID);
        sendMessage(player, message);
    }

    public void spectatorError(Player player) {
        String message = messageManager.get(Message.GAMESPECTATORERROR);
        sendMessage(player, message);
    }

    public void sendGameDesc(Player player) {
        String message = messageManager.get(Message.GAMEDESC);
        sendMessage(player, message);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(Utils.addColour(message));
    }

    public void sendMessageToAll(String message) {
        for (UUID uuid : gamePlayerManager.getAllPlayers(gameID)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.sendMessage(Utils.addColour(message));
        }
    }

    public void sendTitleToAll(String title, String subtitle) {
        for (UUID uuid : gamePlayerManager.getPlayingPlayers(gameID)) {
            Bukkit.getPlayer(uuid).sendTitle(Utils.addColour(title), Utils.addColour(subtitle), 20, 20, 20);
        }
    }

    public void cleanTitles(Player player) {
        player.sendTitle("", "", 0, 20, 0);
    }

    public void announceWinners() {
        Set<UUID> playingPlayers = gamePlayerManager.getPlayingPlayers(gameID);
        int size = playingPlayers.size();
        int identifier = game.getIdentifier();

        //If there are no playing players left (does not include spectators)
        if (size == 0) {
            if (!game.isBroadcastWinners())
                sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEMNOWINNERBROADCAST, identifier));
            else
                Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMNOWINNERBROADCAST, identifier)));
            return;
        }

        //If there is 1 player remaining (does not include spectators)
        if (size == 1) {
            UUID uuid = playingPlayers.iterator().next();

            if (!game.isBroadcastWinners())
                sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEMSINGLEPLAYERBROADCAST, Bukkit.getPlayer(uuid).getName(), identifier));
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

        if (!game.isBroadcastWinners())
            sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEMULTIPLAYERBROADCAST, winners.toString(), identifier));
        else
            Bukkit.broadcastMessage(Utils.addColour(messageManager.getWithPlaceholder(Message.GAMEMULTIPLAYERBROADCAST, winners.toString(), identifier)));
    }
}
