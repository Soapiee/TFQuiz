package me.soapiee.tfquiz.instance.rewards.types;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.enums.RewardType;
import me.soapiee.tfquiz.hooks.VaultHook;
import me.soapiee.tfquiz.instance.rewards.Reward;
import org.bukkit.entity.Player;

public class CurrencyReward extends Reward {

    private final VaultHook vaultHook;
    private final double amount;

    public CurrencyReward(TFQuiz main, String message, double amount) {
        super(RewardType.CURRENCY, main.getMessageManager(), message);
        this.vaultHook = main.getVaultHook();
        this.amount = amount;
    }

    @Override
    public void give(Player player) {
        vaultHook.deposit(player, amount);
        super.give(player);
    }

    @Override
    public String toString() {
        return amount + (vaultHook == null ? "" : vaultHook.getCurrencyName());
    }
}
