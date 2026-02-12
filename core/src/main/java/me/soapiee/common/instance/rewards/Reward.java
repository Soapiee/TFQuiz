package me.soapiee.common.instance.rewards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.soapiee.common.enums.RewardType;
import me.soapiee.common.managers.MessageManager;

@AllArgsConstructor
public abstract class Reward implements RewardInterface {

    @Getter protected final RewardType type;
    @Getter protected final MessageManager messageManager;
    @Getter private final String message;
}
