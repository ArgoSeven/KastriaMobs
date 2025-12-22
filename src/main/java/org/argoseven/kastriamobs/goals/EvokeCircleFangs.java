package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.argoseven.kastriamobs.Config;

public class EvokeCircleFangs extends AbstractEvokeFangs {
    private final int numberOfFangs;
    private final int numberOfCircles;
    private final float radiusStep;

    public EvokeCircleFangs(MobEntity caster, int activationRange, int maxCooldown, int numberOfFangs, int numberOfCircles , float radiusStep) {
        super(caster, activationRange, maxCooldown);
        this.numberOfFangs = numberOfFangs;
        this.numberOfCircles = numberOfCircles;
        this.radiusStep = radiusStep;
    }

    public EvokeCircleFangs(MobEntity caster, Config.FangAttackConfig fangAttackConfig) {
        super(caster, fangAttackConfig.range_of_activation, fangAttackConfig.max_cooldown);
         numberOfFangs = fangAttackConfig.number_of_fangs;
         numberOfCircles = fangAttackConfig.number_of_circles;
         radiusStep = fangAttackConfig.radius_step;
    }

    @Override
    protected void spawnPattern(LivingEntity target, double minY, double maxY, float angle) {
        for (int circle = 0; circle < numberOfCircles; circle++) {
            float currentRadius = 1 +  circle * radiusStep;
            int fangsInCircle = numberOfFangs + circle * 2;
            float angleIncrement = (float) (Math.PI * 2.0 / fangsInCircle);

            for (int fangIndex = 0; fangIndex < fangsInCircle; fangIndex++) {
                float fangAngle = angle + fangIndex * angleIncrement;
                conjureFangs(
                        caster.getX() + MathHelper.cos(fangAngle) * currentRadius,
                        caster.getZ() + MathHelper.sin(fangAngle) * currentRadius,
                        minY, maxY, fangAngle, circle * 2
                );
            }
        }
    }
}
