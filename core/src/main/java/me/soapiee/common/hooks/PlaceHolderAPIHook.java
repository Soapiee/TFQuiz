package me.soapiee.common.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.instance.Game;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceHolderAPIHook extends PlaceholderExpansion {

    private final TFQuiz main;

    public PlaceHolderAPIHook(TFQuiz main) {
        this.main = main;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tfquiz";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Soapiee";
    }

    @Override
    public @NotNull String getVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String identifier) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            Game game = main.getGameManager().getGame(player.getUniqueId());

            switch (identifier.toLowerCase()) {
                case "gameid":
                    return gameIDPlaceholder(game);
                case "in_game":
                    return inGamePlaceholder(game);
                case "gamestate":
                    return gameStatePlaceholder(game);
                case "countdown":
                    return countdownPlaceholder(game);
            }
        }
        return null;
    }

    private String gameIDPlaceholder(Game game) {
        if (game == null) return "null";
        else return String.valueOf(game.getIdentifier());
    }

    private String inGamePlaceholder(Game game) {
        return game == null ? "false" : "true";
    }

    private String gameStatePlaceholder(Game game) {
        if (game == null) return "null";
        return game.getStateDescription();
    }

    private String countdownPlaceholder(Game game) {
        if (game == null) return "null";
        if (game.getCountdown() == null) return "null";
        return String.valueOf(game.getCountdown().getSeconds());
    }
}
