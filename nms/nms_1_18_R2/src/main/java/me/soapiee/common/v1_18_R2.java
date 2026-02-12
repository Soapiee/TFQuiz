package me.soapiee.common;

import me.soapiee.common.enums.Message;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.versionsupport.NMSProvider;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

class v1_18_R2 implements NMSProvider {

    private CustomLogger customLogger;
    private MessageManager messageManager;

    @Override
    public void initialise(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    @Override
    public boolean setSpectator(Player player) {
        ServerPlayer p = ((CraftPlayer) player).getHandle();
        ClientboundPlayerInfoPacket info = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, p);

        ArrayList<ClientboundPlayerInfoPacket.PlayerUpdate> list = new ArrayList<>();
        list.add(new ClientboundPlayerInfoPacket.PlayerUpdate(
                p.getBukkitEntity().getProfile(),
                0,
                GameType.CREATIVE,
                null));

        Field packetField;
        try {
            packetField = info.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            packetField.set(info, list);
            player.setGameMode(GameMode.SPECTATOR);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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
    public void updateTab(Player player, Set<UUID> spectators) {
        ServerPlayer playerJoined = ((CraftPlayer) player).getHandle();

        for (UUID uuid : spectators) {
            ServerPlayer spec = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
            ClientboundPlayerInfoPacket info = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, spec);

            ArrayList<ClientboundPlayerInfoPacket.PlayerUpdate> list = new ArrayList<>();
            list.add(new ClientboundPlayerInfoPacket.PlayerUpdate(
                    spec.getBukkitEntity().getProfile(),
                    0,
                    GameType.SURVIVAL,
                    null));

            Field packetField;
            try {
                packetField = info.getClass().getDeclaredField("b");
                packetField.setAccessible(true);
                packetField.set(info, list);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return;
            }

            playerJoined.connection.send(info);
        }
    }
}
