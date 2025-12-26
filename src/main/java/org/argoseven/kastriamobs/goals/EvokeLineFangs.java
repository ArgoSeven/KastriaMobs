package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

import java.util.EnumSet;

public class EvokeLineFangs extends AbstractEvokeFangs {
    private int numberOfFangs = 8;

    public EvokeLineFangs(MobEntity caster, int activationRange, int maxCooldown, int numberOfFangs) {
        super(caster, activationRange, maxCooldown);
        this.numberOfFangs = numberOfFangs;
    }

    public EvokeLineFangs(MobEntity caster, Config.FangAttackConfig fangAttackConfig) {
        super(caster, fangAttackConfig.range_of_activation, fangAttackConfig.max_cooldown);
        numberOfFangs = fangAttackConfig.number_of_fangs;
    }

    @Override
    public void tick() {
        super.tick();
        KastriaMobs.moveAndRetreat(caster, caster.getTarget(), activationRange);
    }

    @Override
    protected void spawnPattern(LivingEntity target, double minY, double maxY, float angle) {
        for (int i = 0; i < numberOfFangs; ++i) {
            double h = 1.25D * (i + 1);
            conjureFangs(caster.getX() + MathHelper.cos(angle) * h,
                    caster.getZ() + MathHelper.sin(angle) * h,
                    minY, maxY, angle, i);
        }
    }
}

