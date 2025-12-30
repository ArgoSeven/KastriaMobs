package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

public abstract class AbstractEvokeFangs extends Goal {
    
    protected final MobEntity caster;
    protected int cooldown = 0;
    protected final int maxCooldown;
    protected final float activationRange;

    protected AbstractEvokeFangs(MobEntity caster, Config.FangAttackConfig config) {
        this.caster = caster;
        this.activationRange = config.range_of_activation;
        this.maxCooldown = config.max_cooldown;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) 
                && caster.squaredDistanceTo(target) < KastriaMobs.getSquared(activationRange) + 1;
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void tick() {
        if (--cooldown <= 0) {
            castSpell();
            cooldown = maxCooldown;
        }
    }

    protected void castSpell() {
        LivingEntity target = caster.getTarget();
        if (target == null) {
            return;
        }

        caster.swingHand(Hand.MAIN_HAND);
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
        
        double minY = Math.min(target.getY(), caster.getY());
        double maxY = Math.max(target.getY(), caster.getY()) + 1.0F;
        float angle = (float) MathHelper.atan2(
                target.getZ() - caster.getZ(),
                target.getX() - caster.getX()
        );

        if (caster.squaredDistanceTo(target) < KastriaMobs.getSquared(activationRange)) {
            spawnPattern(target, minY, maxY, angle);
        }
    }

    protected abstract void spawnPattern(LivingEntity target, double minY, double maxY, float angle);

    protected void conjureFangs(double x, double z, double minY, double maxY, float yaw, int warmup) {
        BlockPos pos = new BlockPos(x, minY, z);
        World world = caster.world;
        double offsetY = 0.0;

        while (pos.getY() >= MathHelper.floor(maxY) - 1) {
            BlockPos below = pos.down();
            
            if (world.getBlockState(below).isSideSolidFullSquare(world, below, Direction.UP)) {
                if (!world.isAir(pos)) {
                    VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
                    if (!shape.isEmpty()) {
                        offsetY = shape.getMax(Direction.Axis.Y);
                    }
                }

                world.spawnEntity(new EvokerFangsEntity(world, x, pos.getY() + offsetY, z, yaw, warmup, caster));
                return;
            }

            pos = below;
        }
    }
}
