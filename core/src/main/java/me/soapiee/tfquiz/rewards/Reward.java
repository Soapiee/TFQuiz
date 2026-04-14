package me.soapiee.tfquiz.rewards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.soapiee.tfquiz.rewards.types.RewardType;
import me.soapiee.tfquiz.utils.MessageManager;

@AllArgsConstructor
public abstract class Reward implements RewardInterface {

    @Getter protected final RewardType type;
    @Getter protected final MessageManager messageManager;
    @Getter private final String message;
}
