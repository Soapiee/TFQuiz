package me.soapiee.tfquiz.command.adminCmds.signSubs;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.command.adminCmds.AbstractAdminSub;
import me.soapiee.tfquiz.enums.Message;
import me.soapiee.tfquiz.instance.GameSign;
import me.soapiee.tfquiz.utils.Keys;
import me.soapiee.tfquiz.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SignRemoveSub extends AbstractAdminSub {

    private final String IDENTIFIER = "signremove";

    public SignRemoveSub(TFQuiz main) {
        super(main, "tfquiz.admin.signs", 2, 3);
    }

    // /tf sign remove (Looking at sign)
    // /tf sign remove <signID>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, label, args)) return;

        if (isConsole(sender, false) && (args.length == 2)) {
            sendMessage(sender, messageManager.get(Message.SIGNINVALIDSIGNID));
            return;
        }

        GameSign gameSign = getGameSign(sender, args);
        if (gameSign == null) return;

        if (!isConsole(sender, false))
            gameSign.getLocation().getWorld().dropItem(gameSign.getLocation(), new ItemStack(getSignMaterial(gameSign)));

        String signID = gameSign.getSignID();
        gameSignManager.deleteSign(gameSign);
        sendMessage(sender, messageManager.getWithPlaceholder(Message.SIGNREMOVED, signID));
    }

    private Material getSignMaterial(GameSign gameSign) {
        Material material = gameSign.getMaterial();

        if (material.toString().toLowerCase().contains("wall")) {
            material = Material.valueOf(material.toString().replace("WALL_", ""));
        }

        return material;
    }

    private Sign getSignBlock(Player player) {
        Block blockTarget = player.getTargetBlock(null, 5);

        Sign signBlock = null;
        if (blockTarget.getState() instanceof Sign) signBlock = (Sign) blockTarget.getState();
        if (signBlock == null) player.sendMessage(Utils.addColour(messageManager.get(Message.SIGNNOTLOOKINGATSIGN)));

        return signBlock;
    }

    private String getDataContainer(Sign signBlock, Player player) {
        String dataContainer = signBlock.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING);

        if (dataContainer == null) sendMessage(player, messageManager.get(Message.SIGNNOTLOOKINGATSIGN));

        return dataContainer;
    }

    private GameSign getGameSign(CommandSender sender, String[] args) {
        if (args.length == 3) return getSign(sender, args[2]);

        Player player = (Player) sender;

        Sign signBlock = getSignBlock(player);
        if (signBlock == null) return null;

        String dataContainer = getDataContainer(signBlock, player);
        if (dataContainer == null) return null;

        return getSign(player, dataContainer);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    public String getIDENTIFIER() {
        return IDENTIFIER;
    }
}
