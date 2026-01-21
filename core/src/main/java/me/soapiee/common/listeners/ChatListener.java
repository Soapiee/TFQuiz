package me.soapiee.common.listeners;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.instance.Game;
import me.soapiee.common.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatListener implements Listener {

    private final GameManager gameManager;

    public ChatListener(TFQuiz main) {
        gameManager = main.getGameManager();
    }

    @EventHandler
    public void arenaChat(AsyncPlayerChatEvent event) {
        Game sendersGame = gameManager.getGame(event.getPlayer());

        Set<Player> recipients = event.getRecipients();

        if (sendersGame == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Game recipientGame = gameManager.getGame(player);
                if (recipientGame != null && (recipientGame.isPhysicalArena())) recipients.remove(player);
            }
        }

        if (sendersGame != null) {
            if (!sendersGame.isPhysicalArena()) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                Game recipientGame = gameManager.getGame(player);
                if (recipientGame == null || recipientGame != sendersGame) recipients.remove(player);
            }
        }
    }
}
