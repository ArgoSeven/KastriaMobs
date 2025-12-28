package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.CursedBullet;

import java.util.EnumSet;

public class SummonCursedBullet extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private final int maxCooldown;
    private final float activationRange;
    private final float maxRangeAttack;
    private final StatusEffect statusEffect;
    private final int duration;
    private final int amplifier;



    public SummonCursedBullet(MobEntity caster, int maxCooldown , int activationRange, int maxRangeAttack, Identifier statusEffect, int duration, int amplifier) {
        this.caster = caster;
        this.maxCooldown = maxCooldown;
        this.activationRange = activationRange;
        this.maxRangeAttack = maxRangeAttack;
        this.statusEffect = statusEffect !=null ? Registry.STATUS_EFFECT.get(statusEffect) : StatusEffects.BLINDNESS;
        this.duration = duration;
        this.amplifier = amplifier;
        this.setControls(EnumSet.of(Goal.Control.LOOK, Control.TARGET));
    }

    public SummonCursedBullet(MobEntity caster, Config.CursedBulletConfig cursedBulletConfig) {
        this.caster = caster;
        this.maxCooldown = cursedBulletConfig.max_cooldown;
        this.activationRange = cursedBulletConfig.range_of_activation;
        this.maxRangeAttack = cursedBulletConfig.max_range_of_attack;
        this.statusEffect = cursedBulletConfig.status_effect != null ? Registry.STATUS_EFFECT.get(new Identifier(cursedBulletConfig.status_effect)) : StatusEffects.BLINDNESS ;
        this.duration = cursedBulletConfig.effect_duration;
        this.amplifier = cursedBulletConfig.effect_amplifier;
        this.setControls(EnumSet.of(Goal.Control.LOOK, Control.TARGET));
    }




    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && (this.caster.squaredDistanceTo(target) > KastriaMobs.getSquared(activationRange) );
    }

    /*
    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.squaredDistanceTo(target) > KastriaMobs.getSquared(activationRange) ;
    }*/

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
            LivingEntity target = caster.getTarget();
            if (target != null) {
                caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                caster.swingHand(Hand.MAIN_HAND);
                double d = caster.squaredDistanceTo(target);
                if (d < KastriaMobs.getSquared(maxRangeAttack)) {
                    if (this.cooldown <= 0) {
                        this.cooldown = maxCooldown + caster.getRandom().nextInt() * maxCooldown / 2;
                        caster.world.spawnEntity(new CursedBullet(caster.world, caster, target, caster.getMovementDirection().getAxis(), statusEffect, duration, amplifier));
                        caster.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    caster.setTarget((LivingEntity)null);
                }
            }
        }
    }
}

