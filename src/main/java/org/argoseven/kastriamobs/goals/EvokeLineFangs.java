package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

import java.util.EnumSet;

public class EvokeLineFangs extends AbstractEvokeFangs {
    
    private static final double FANG_SPACING = 1.25;
    
    private final int numberOfFangs;

    public EvokeLineFangs(MobEntity caster, int activationRange, int maxCooldown, int numberOfFangs) {
        super(caster, activationRange, maxCooldown);
        this.numberOfFangs = numberOfFangs;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public EvokeLineFangs(MobEntity caster, Config.FangAttackConfig config) {
        super(caster, config.range_of_activation, config.max_cooldown);
        this.numberOfFangs = config.number_of_fangs;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = caster.getTarget();
        if (target != null) {
            KastriaMobs.moveAndRetreat(caster, target, activationRange);
        }
    }

    @Override
    protected void spawnPattern(LivingEntity target, double minY, double maxY, float angle) {
        for (int i = 0; i < numberOfFangs; ++i) {
            double distance = FANG_SPACING * (i + 1);
            conjureFangs(
                    caster.getX() + MathHelper.cos(angle) * distance,
                    caster.getZ() + MathHelper.sin(angle) * distance,
                    minY, maxY, angle, i
            );
        }
    }
}
