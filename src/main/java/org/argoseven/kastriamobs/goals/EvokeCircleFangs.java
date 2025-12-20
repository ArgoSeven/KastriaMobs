package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class EvokeCircleFangs extends AbstractEvokeFangs {
    private int numberOfFangs = 5;
    private int numberOfCircles = 3;
    private float radiusStep = 1F;

    public EvokeCircleFangs(MobEntity caster) {
        super(caster, 2, 20);
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
