package dev.tourmi.svmm.config;

import dev.tourmi.svmm.utils.PredicateUtils;
import net.minecraft.world.entity.player.Player;

import java.text.MessageFormat;
import java.util.function.Predicate;

public enum TriggerActions {
    SHIFT_HELD("while holding shift", Player::isShiftKeyDown),
    SHIFT_NOT_HELD("while not holding shift", SHIFT_HELD.triggerCondition.negate()),
    ALWAYS(PredicateUtils::always)
    ;

    private final Predicate<Player> triggerCondition;
    public final String description;

    TriggerActions(Predicate<Player> triggerCondition) {
        this("", triggerCondition);
    }

    TriggerActions(String description, Predicate<Player> triggerCondition) {
        this.triggerCondition = triggerCondition;
        this.description = description;
    }

    public boolean shouldTrigger(Player player) {
        return triggerCondition.test(player);
    }

    public String formatConditionText(String baseMessage, String addedText) {
        if (description.isEmpty()) {
            return MessageFormat.format(baseMessage, "");
        }

        return MessageFormat.format(baseMessage, MessageFormat.format(addedText, description));
    }
}
