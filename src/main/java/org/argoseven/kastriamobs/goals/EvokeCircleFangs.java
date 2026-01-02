package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.ConfigProvider;

public class EvokeCircleFangs extends AbstractEvokeFangs {
    
    private static final int BASE_RADIUS = 1;
    private static final int FANGS_INCREMENT_PER_CIRCLE = 2;
    
    private final int numberOfFangs;
    private final int numberOfCircles;
    private final float radiusStep;

    public <T extends MobEntity & ConfigProvider.CircleFangsProvider> EvokeCircleFangs(T caster) {
        super(caster, caster.getCircleFangsConfig());
        Config.FangAttackConfig config = caster.getCircleFangsConfig();
        this.numberOfFangs = config.number_of_fangs;
        this.numberOfCircles = config.number_of_circles;
        this.radiusStep = config.radius_step;
    }

    protected EvokeCircleFangs(MobEntity caster, Config.FangAttackConfig config) {
        super(caster, config);
        this.numberOfFangs = config.number_of_fangs;
        this.numberOfCircles = config.number_of_circles;
        this.radiusStep = config.radius_step;
    }

    protected void handleMovement(LivingEntity target) {
    }

    @Override
    protected void spawnPattern(LivingEntity target, double minY, double maxY, float angle) {
        for (int circle = 0; circle < numberOfCircles; circle++) {
            float currentRadius = BASE_RADIUS + circle * radiusStep;
            int fangsInCircle = numberOfFangs + circle * FANGS_INCREMENT_PER_CIRCLE;
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
