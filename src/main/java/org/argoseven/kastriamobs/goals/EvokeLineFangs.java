package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class EvokeLineFangs extends AbstractEvokeFangs {
    private int numberOfFangs = 8;

    public EvokeLineFangs(MobEntity caster) {
        super(caster, 8, 20);
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

