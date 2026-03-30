package me.soapiee.tfquiz.instance.rewards.types;


import me.soapiee.tfquiz.TFQuiz;
import me.soapiee.tfquiz.enums.RewardType;
import me.soapiee.tfquiz.instance.rewards.Reward;

public class NullReward extends Reward {

    public NullReward(TFQuiz main) {
        super(RewardType.NONE, main.getMessageManager(), null);
    }

    @Override
    public String toString() {
        return "No reward";
    }
}
