package me.soapiee.common.instance.rewards.types;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.hooks.VaultHook;
import me.soapiee.common.instance.rewards.Reward;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionReward extends Reward {

    private final VaultHook vaultHook;
    private final ArrayList<String> permissions;

    public PermissionReward(TFQuiz main, String message, ArrayList<String> permission) {
        super(RewardType.PERMISSION, main.getMessageManager(), message);
        this.vaultHook = main.getVaultHook();
        this.permissions = permission;
    }

    @Override
    public void give(Player player) {
        for (String permission : permissions) {
            vaultHook.setPermission(player, permission);
        }

        super.give(player);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 1;

        builder.append(type.toString().toLowerCase()).append("s: ");
        for (String permission : permissions) {
            builder.append(permission);
            if (permissions.size() > i) builder.append(", ");
            i++;
        }

        return builder.toString();
    }
}
