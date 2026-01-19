package me.soapiee.common.instance.rewards.types;


import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.instance.rewards.Reward;

public class NullReward extends Reward {

    public NullReward(TFQuiz main) {
        super(RewardType.NONE, main.getMessageManager(), null);
    }

    @Override
    public String toString() {
        return "No reward";
    }
}
