package org.argoseven.kastriamobs.goals;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;

public abstract class AbstractEvokeFangs extends Goal {
    protected final MobEntity caster;
    protected int cooldown = 0;
    protected int maxCooldown;
    protected float activationRange;

    public AbstractEvokeFangs(MobEntity caster, float activationRange, int maxCooldown) {
        this.caster = caster;
        this.activationRange  = activationRange;
        this.maxCooldown = maxCooldown;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && caster.squaredDistanceTo(target) < activationRange * activationRange;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target);
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
        if (target == null) return;

        double minY = Math.min(target.getY(), caster.getY());
        double maxY = Math.max(target.getY(), caster.getY()) + 1.0F;
        float angle = (float) MathHelper.atan2(target.getZ() - caster.getZ(),
                target.getX() - caster.getX());

        if (caster.squaredDistanceTo(target) < activationRange * activationRange) {
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
            BlockState belowState = world.getBlockState(below);

            if (belowState.isSideSolidFullSquare(world, below, Direction.UP)) {
                BlockState currentState = world.getBlockState(pos);

                if (!world.isAir(pos)) {
                    VoxelShape shape = currentState.getCollisionShape(world, pos);
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

