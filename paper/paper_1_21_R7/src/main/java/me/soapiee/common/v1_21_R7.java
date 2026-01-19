package me.soapiee.common;

import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.utils.Utils;
import me.soapiee.common.versionsupport.NMSProvider;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

class v1_21_R7 implements NMSProvider {

    private CustomLogger customLogger;
    private MessageManager messageManager;

    @Override
    public void initialise(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    private ServerPlayer getServerPlayer(Player player) {
        try {
            return ((CraftPlayer) player).getHandle();
        } catch (NoClassDefFoundError e) {
            customLogger.logToFile(null, messageManager.get(Message.UNSUPPORTEDPLATFORMPAPER));
            if (!Utils.IS_PAPER)
                customLogger.logToPlayer(Bukkit.getConsoleSender(), null, messageManager.get(Message.DOWNLOADSPIGOTJAR));

            return null;
        }
    }

    @Override
    public boolean setSpectator(Player player) {
        ServerPlayer p = getServerPlayer(player);
        if (p == null) return false;

        ClientboundPlayerInfoUpdatePacket info = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, p);

        ArrayList<ClientboundPlayerInfoUpdatePacket.Entry> list = new ArrayList<>();
        list.add(new ClientboundPlayerInfoUpdatePacket.Entry(
                player.getUniqueId(),
                p.getGameProfile(),
                false,
                0,
                GameType.CREATIVE,
                p.getTabListDisplayName(),
                false,
                0,
                null));

        Field packetField;
        try {
            packetField = info.getClass().getDeclaredField("entries");
            packetField.setAccessible(true);
            packetField.set(info, list);
            player.setGameMode(GameMode.SPECTATOR);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            customLogger.logToFile(e, messageManager.get(Message.SPECTATORSYSTEMERROR));
            return false;
        }

        p.connection.send(info);
        p.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 3));

        //send info packet to all other players on the server
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                ((CraftPlayer) onlinePlayer).getHandle().connection.send(info);
            }
        }

        return true;
    }

    @Override
    public void unSetSpectator(Player player) {
        ServerPlayer p = ((CraftPlayer) player).getHandle();
        p.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 2));
    }

    @Override
    public void updateTab(org.bukkit.entity.Player player, HashSet<UUID> spectators) {
        ServerPlayer playerJoined = ((CraftPlayer) player).getHandle();

        for (UUID uuid : spectators) {
            ServerPlayer spec = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
            ClientboundPlayerInfoUpdatePacket info = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, spec);

            ArrayList<ClientboundPlayerInfoUpdatePacket.Entry> list = new ArrayList<>();
            list.add(new ClientboundPlayerInfoUpdatePacket.Entry(
                    uuid,
                    spec.getGameProfile(),
                    false,
                    0,
                    GameType.SURVIVAL,
                    spec.getTabListDisplayName(),
                    false,
                    0,
                    null));

            Field packetField;
            try {
                packetField = info.getClass().getDeclaredField("entries");
                packetField.setAccessible(true);
                packetField.set(info, list);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return;
            }

            playerJoined.connection.send(info);
        }
    }
}
