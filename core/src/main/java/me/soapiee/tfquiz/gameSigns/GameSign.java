package me.soapiee.tfquiz.gameSigns;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.games.Game;
import me.soapiee.tfquiz.internals.SignHandler;
import me.soapiee.tfquiz.managers.GamePlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameSign {

    private final GamePlayerManager gamePlayerManager;
    @Getter private final Game game;
    private final int gameID;
    @Getter private final String signID;
    @Getter private final Material material;
    @Getter private final Location location;
    private final HashMap<Integer, String> text;
    @Setter @Getter private Sign signBlock;

    public GameSign(TFQuiz main, HashMap<String, String> dataValues, Location location, HashMap<Integer, String> text) {
        gamePlayerManager = main.getGamePlayerManager();
        signID = dataValues.get("sign_ID");
        gameID = Integer.parseInt(dataValues.get("game_ID"));
        material = Material.matchMaterial(dataValues.get("material"));
        this.location = location;
        this.text = text;
        game = main.getGameManager().getGame(gameID);

        spawn(dataValues.get("block_face"), main.getVersionManager().getSignHandler());
    }

    private void spawn(String stringFace, SignHandler signHandler) {
        BlockFace blockFace = BlockFace.valueOf(stringFace);
        signHandler.spawn(this, blockFace);
    }

    public void setNewText(int lineIndex, String line) {
        text.put(lineIndex + 1, line);
    }

    public List<String> getFormattedText() {
        ArrayList<String> listCopy = new ArrayList<>();

        for (String line : text.values()) {
            String newLine = line;

            if (line.contains("%")) {
                if (line.contains("%game_ID%")) newLine = newLine.replace("%game_ID%", String.valueOf(gameID));
                if (line.contains("%game_id%")) newLine = newLine.replace("%game_id%", String.valueOf(gameID));
                if (line.contains("%game_state%"))
                    newLine = newLine.replace("%game_state%", game.getStateDescription());
                if (line.contains("%game_players%"))
                    newLine = newLine.replace("%game_players%", String.valueOf(gamePlayerManager.getPlayingPlayers(gameID).size()));
                if (line.contains("%game_maxplayers%"))
                    newLine = newLine.replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
            }

            listCopy.add(newLine);
        }

        return listCopy;
    }

    public List<String> getText() {
        return new ArrayList<>(text.values());
    }
}
