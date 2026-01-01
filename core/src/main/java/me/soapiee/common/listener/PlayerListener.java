package me.soapiee.common.listener;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.conversations.SignConvo;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.cosmetic.GameSign;
import me.soapiee.common.manager.GameManager;
import me.soapiee.common.utils.Keys;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlayerListener implements Listener, ConversationAbandonedListener {

    private final TFQuiz main;
    private final GameManager gameManager;
    private boolean falldamage;
    private boolean pvpdamage;
    private boolean hunger;
    private boolean breakblocks;
    private boolean placeblocks;
    private boolean teleport;
    private final ConversationFactory convoFactory;

    public PlayerListener(TFQuiz main) {
        this.main = main;
        gameManager = main.getGameManager();
        convoFactory = new ConversationFactory(main)
                .withEscapeSequence("EXIT")
                .addConversationAbandonedListener(this);
        ruleCheck();
    }

    public void ruleCheck() { //Called in constructor and when the plugin reloads
        FileConfiguration config = main.getConfig();
        falldamage = config.getBoolean("arena_flags.allow_fall_damage");
        pvpdamage = config.getBoolean("arena_flags.allow_pvp_damage");
        hunger = config.getBoolean("arena_flags.allow_hunger");
        breakblocks = config.getBoolean("arena_flags.allow_block_break");
        placeblocks = config.getBoolean("arena_flags.allow_block_place");
        teleport = config.getBoolean("arena_flags.allow_teleport");
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (placeblocks) return;
        Player player = event.getPlayer();
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;
        Game playersGame = gameManager.getGame(player);
        if (playersGame != null && playersGame.isPhysicalArena()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void fallDamage(EntityDamageEvent event) {
        if (falldamage) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;

        Game playersGame = gameManager.getGame(player);
        if (playersGame != null) {
            if (!playersGame.isPhysicalArena()) return;
            event.setCancelled(true);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void pvpDamage(EntityDamageByEntityEvent event) {
        if (pvpdamage) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;

        Game playersGame = gameManager.getGame(player);
        if (playersGame != null && playersGame.isPhysicalArena()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void removeHunger(FoodLevelChangeEvent event) {
        if (hunger) return;
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            if (player.hasPermission("tfquiz.admin.bypassflags")) return;

            Game playersGame = gameManager.getGame(player);
            if (playersGame != null && playersGame.isPhysicalArena()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void arenaCommands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;
        Game playersGame = gameManager.getGame(player);
        if (gameManager.getGame(player) == null) return;
        if (!playersGame.isPhysicalArena()) return;

        ArrayList<String> disallowedCmds = gameManager.getDisallowedCommands();
        if (disallowedCmds.isEmpty()) return;

        String cmd = event.getMessage();

        for (String command : disallowedCmds) {
            if (cmd.contains(command)) {
                player.sendMessage(Utils.addColour(main.getMessageManager().get(Message.GAMEDISALLOWEDCMD)));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getState() instanceof Sign) {
            Sign signBlock = (Sign) block.getState();
            if (signBlock.getPersistentDataContainer().has(Keys.GAME_SIGN, PersistentDataType.STRING)) {
                if (gameManager.getSign(signBlock.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING)) == null)
                    return;
                event.setCancelled(true);
            }
        }

        if (breakblocks) return;
        Player player = event.getPlayer();
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;
        Game playersGame = gameManager.getGame(player);
        if (playersGame != null && playersGame.isPhysicalArena()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        Action action = event.getAction();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof Sign)) return;

        Sign signBlock = (Sign) block.getState();
        if (signBlock.getPersistentDataContainer().has(Keys.GAME_SIGN, PersistentDataType.STRING)) {
            Player player = event.getPlayer();
            String signID = signBlock.getPersistentDataContainer().get(Keys.GAME_SIGN, PersistentDataType.STRING);
            GameSign gameSign = gameManager.getSign(signID);
            if (gameSign == null) return;
            Game game = gameManager.getGame(signID);

            event.setCancelled(true);

            //TODO: Call sign convo
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (player.hasPermission("tfquiz.admin.signs")) {
                    //check if player is already in a conversation
                    String activeConvo = player.getPersistentDataContainer().get(Keys.ACTIVE_CONVERSATION, PersistentDataType.STRING);

                    if (activeConvo == null) {
                        //start text prompt asking them what text they want to change
                        player.getPersistentDataContainer().set(Keys.ACTIVE_CONVERSATION, PersistentDataType.STRING, "signConvo");
                        player.getPersistentDataContainer().set(Keys.GAME_SIGN, PersistentDataType.STRING, signID);
                        convoFactory.withFirstPrompt(new SignConvo(main));
                        convoFactory.buildConversation(player).begin();
                    }
                }
                return;
            }

            if (action == Action.LEFT_CLICK_BLOCK) {
                if (game != null) Bukkit.dispatchCommand(player, "game join " + game.getID());
            }
        }
    }

    private String getConvoType(Player player) {
        String convoType = player.getPersistentDataContainer().get(Keys.ACTIVE_CONVERSATION, PersistentDataType.STRING);
        player.getPersistentDataContainer().remove(Keys.ACTIVE_CONVERSATION);

        return convoType;
    }

    @Override
    public void conversationAbandoned(@NotNull ConversationAbandonedEvent abandonedEvent) {
        Conversable conversable = abandonedEvent.getContext().getForWhom();
        if (!(conversable instanceof Player)) return;
        Player player = (Player) conversable;

        String convoType = getConvoType(player);
        if (convoType == null) return;

        if (convoType.equalsIgnoreCase("signConvo")) {
            player.getPersistentDataContainer().remove(Keys.GAME_SIGN);
            conversable.sendRawMessage(Utils.addColour("&cYou have exited the GameSign editor"));
        }
        if (convoType.equalsIgnoreCase("reloadConvo"))
            if (!abandonedEvent.gracefulExit())
                conversable.sendRawMessage(Utils.addColour("&cYou have cancelled the reload"));

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (teleport) return;
        Player player = event.getPlayer();
        if (player.hasPermission("tfquiz.admin.bypassflags")) return;
        if (gameManager.getGame(player) == null) return;
        if (!gameManager.getGame(player).isPhysicalArena()) return;

        switch (event.getCause()) {
            case CHORUS_FRUIT:
            case COMMAND:
            case ENDER_PEARL:
            case SPECTATE:
//            player.sendMessage(Colour.allow("You cannot teleport when in an arena"));
                event.setCancelled(true);
        }
    }
}
