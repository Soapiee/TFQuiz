package me.soapiee.tfquiz.rewards.types;

import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.rewards.Reward;
import org.bukkit.entity.Player;

public class ExperienceReward extends Reward {

    private final int amount;

    public ExperienceReward(TFQuiz main, String message, int amount) {
        super(RewardType.EXPERIENCE, main.getMessageManager(), message);
        this.amount = amount;
    }

    @Override
    public void give(Player player) {
        player.giveExpLevels(amount);
        super.give(player);
    }

    @Override
    public String toString() {
        return amount + " exp level" + (amount != 1 ? "s" : "");
    }

}
