package org.argoseven.kastriamobs.goals;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.entity.CursedBullet;

public class HollowseerGoal extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private  int maxCooldown = 40;
    public HollowseerGoal(MobEntity caster) {
        this.caster = caster;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target);
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

        if (caster.squaredDistanceTo(target) < 9.0D) {
            for (int i = 0; i < 5; ++i) {
                float g = angle + i * (float) Math.PI * 0.4F;
                conjureFangs(caster.getX() + MathHelper.cos(g) * 1.5D,
                        caster.getZ() + MathHelper.sin(g) * 1.5D,
                        minY, maxY, g, 0);
            }
        } else {
            caster.getLookControl().lookAt(target, 180.0F, 180.0F);
            double d = caster.squaredDistanceTo(target);
            if (d < (double)400.0F) {
                if (this.cooldown <= 0) {
                    this.cooldown = maxCooldown + caster.getRandom().nextInt() * maxCooldown / 2;
                    caster.world.spawnEntity(new CursedBullet(caster.world, caster, target, caster.getMovementDirection().getAxis(), new StatusEffectInstance(StatusEffects.WITHER, 20)));
                    caster.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
                }
            } else {
                caster.setTarget((LivingEntity)null);
            }
            super.tick();
        }
    }

    private void conjureFangs(double x, double z, double minY, double maxY, float yaw, int warmup) {
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

