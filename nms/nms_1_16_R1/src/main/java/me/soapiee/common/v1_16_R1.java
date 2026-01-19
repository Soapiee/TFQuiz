package me.soapiee.common;

import com.mojang.authlib.GameProfile;
import me.soapiee.common.enums.Message;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.CustomLogger;
import me.soapiee.common.versionsupport.NMSProvider;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

class v1_16_R1 implements NMSProvider {

    private CustomLogger customLogger;
    private MessageManager messageManager;

    @Override
    public void initialise(TFQuiz main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    @Override
    public boolean setSpectator(Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, p);

        try {
            Class<?> packetClass = Class.forName("net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo");
            Class<?> playerInfoDataClass = Class.forName("net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo$PlayerInfoData");

            Constructor<?> constructor = playerInfoDataClass.getDeclaredConstructor(
                    packetClass,
                    GameProfile.class,
                    int.class,
                    EnumGamemode.class,
                    IChatBaseComponent.class
            );

            constructor.setAccessible(true);

            Object data = constructor.newInstance(
                    info,
                    p.getBukkitEntity().getProfile(),
                    0,
                    EnumGamemode.CREATIVE,
                    new ChatComponentText("DisplayName")
            );

            ArrayList<Object> list = new ArrayList<>();
            list.add(data);

            Field packetField = info.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            packetField.set(info, list);
            player.setGameMode(GameMode.SPECTATOR);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException |
                 ClassNotFoundException e) {
            customLogger.logToFile(e, messageManager.get(Message.SPECTATORSYSTEMERROR));

            return false;
        }

        p.playerConnection.sendPacket(info);
        p.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 3));

        //send info packet to all other players on the server
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(info);
            }
        }

        return true;
    }

    @Override
    public void unSetSpectator(Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        p.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 2));
    }

    @Override
    public void updateTab(Player player, HashSet<UUID> spectators) {
        EntityPlayer playerJoined = ((CraftPlayer) player).getHandle();

        for (UUID uuid : spectators) {
            EntityPlayer spec = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
            PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, spec);

            try {
                Class<?> packetClass = Class.forName("net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo");
                Class<?> playerInfoDataClass = Class.forName("net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo$PlayerInfoData");

                Constructor<?> constructor = playerInfoDataClass.getDeclaredConstructor(
                        packetClass,
                        GameProfile.class,
                        int.class,
                        EnumGamemode.class,
                        IChatBaseComponent.class
                );

                constructor.setAccessible(true);

                Object data = constructor.newInstance(
                        info,
                        spec.getBukkitEntity().getProfile(),
                        0,
                        EnumGamemode.CREATIVE,
                        new ChatComponentText("DisplayName")
                );

                ArrayList<Object> list = new ArrayList<>();
                list.add(data);

                Field packetField = info.getClass().getDeclaredField("b");
                packetField.setAccessible(true);
                packetField.set(info, list);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | ClassNotFoundException e) {
                return;
            }

            playerJoined.playerConnection.sendPacket(info);
        }
    }
}
