package me.soapiee.common.instance.cosmetic;

import lombok.Getter;
import me.soapiee.common.TFQuiz;
import me.soapiee.common.VersionManager;
import me.soapiee.common.instance.Game;
import me.soapiee.common.utils.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameSign {

    private final VersionManager spectatorManager;
    @Getter private final Game game;
    @Getter private final String signID;
    @Getter private final Material material;
    @Getter private final Location location;
    private final HashMap<Integer, String> text;
    private Sign signBlock;

    public GameSign(TFQuiz main, HashMap<String, String> dataValues, Location location, HashMap<Integer, String> text) {
        spectatorManager = main.getVersionManager();
        signID = dataValues.get("sign_ID");
        game = main.getGameManager().getGame(Integer.parseInt(dataValues.get("game_ID")));

        this.location = location;
        material = Material.matchMaterial(dataValues.get("material"));
        this.text = text;

        BlockFace blockFace = BlockFace.valueOf(dataValues.get("block_face"));
        spawn(blockFace);
    }

    public void spawn(BlockFace facing) {
        Block block = location.getBlock();
        if (block.getType() != material) block.setType(material);

        block = location.getBlock();

        if (block.getBlockData() instanceof WallSign) {
            Sign blockState = (Sign) block.getState();
            WallSign wallData = (WallSign) block.getBlockData();
            wallData.setFacing(facing);
            blockState.setBlockData(wallData);
            block.setBlockData(blockState.getBlockData());
        } else {
            org.bukkit.block.data.type.Sign signdata = (org.bukkit.block.data.type.Sign) block.getBlockData(); //1.20
            signdata.setRotation(facing);
            block.setBlockData(signdata);
        }

        signBlock = (Sign) block.getState();
//        this.signBlock.setWaxed(true);
        signBlock.getPersistentDataContainer().set(Keys.GAME_SIGN, PersistentDataType.STRING, signID);

        int i = 0;
        for (String line : text.values()) {
            if (line.contains("%")) {
                line = line.replace("%game_ID%", String.valueOf(game.getIdentifier()))
                        .replace("%game_state%", game.getStateDescription())
                        .replace("%game_players%", String.valueOf(game.getPlayingPlayers().size()))
                        .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
            }

            spectatorManager.setText(signBlock, i, line);
            i++;
        }
        signBlock.update();
    }

    public void despawn() {
        location.getBlock().setType(Material.AIR);
    }

    public void update(String state) { //called when the game state changes
        for (int key : text.keySet()) {
            String line = text.get(key);
            if (line.contains("%")) {
                line = line.replace("%game_state%", state)
                        .replace("%game_ID%", String.valueOf(game.getIdentifier()))
                        .replace("%game_players%", String.valueOf(game.getPlayingPlayers().size()))
                        .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
                spectatorManager.setText(signBlock, key - 1, line);
            }
        }
        signBlock.update();
    }

    public void update(int playerCount) { //called when the player count changes
        for (int key : text.keySet()) {
            String line = text.get(key);
            if (line.contains("%")) {
                line = line.replace("%game_ID%", String.valueOf(game.getIdentifier()))
                        .replace("%game_state%", game.getStateDescription())
                        .replace("%game_players%", String.valueOf(playerCount))
                        .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
                spectatorManager.setText(signBlock, key - 1, line);
            }
        }
        signBlock.update();
    }

    public void update(int lineIndex, String line) { //called when the text is changed on an existing sign
        text.put(lineIndex + 1, line);

        if (line.contains("%")) {
            line = line.replace("%game_ID%", String.valueOf(game.getIdentifier()))
                    .replace("%game_state%", game.getStateDescription())
                    .replace("%game_players%", String.valueOf(game.getPlayingPlayers().size()))
                    .replace("%game_maxplayers%", String.valueOf(game.getMaxPlayers()));
        }
        spectatorManager.setText(signBlock, lineIndex, line);

        signBlock.update();
    }

    public List<String> getText() {
        return new ArrayList<>(text.values());
    }
}
