package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;

public class SummonShulker extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private int maxCooldown =  60;


    public SummonShulker(MobEntity caster) {
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
            summonBullets();
            cooldown = maxCooldown;
        }
    }

    protected void summonBullets() {
        if (caster.world.getDifficulty() != Difficulty.PEACEFUL) {
            --this.cooldown;
            LivingEntity livingEntity = caster.getTarget();
            if (livingEntity != null) {
                caster.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
                double d = caster.squaredDistanceTo(livingEntity);
                if (d < (double)400.0F) {
                    if (this.cooldown <= 0) {
                        this.cooldown = 20 + caster.getRandom().nextInt() * 20 / 2;
                        caster.world.spawnEntity(new ShulkerBulletEntity(caster.world, caster, livingEntity, caster.getMovementDirection().getAxis()));
                        caster.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    caster.setTarget((LivingEntity)null);
                }

                super.tick();
            }
        }
    }
}

