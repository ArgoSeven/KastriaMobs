package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.ConfigProvider;
import org.argoseven.kastriamobs.entity.CursedBullet;

import java.util.EnumSet;

public class SummonCursedBullet extends Goal {
    
    private static final float SOUND_VOLUME = 2.0F;
    private static final int COOLDOWN_RANDOMIZATION_DIVISOR = 2;
    
    private final MobEntity caster;
    private int cooldown;
    private final int maxCooldown;
    private final float activationRange;
    private final float maxRangeAttack;
    private final StatusEffect statusEffect;
    private final int duration;
    private final int amplifier;

    public <T extends MobEntity & ConfigProvider.CursedBulletProvider> SummonCursedBullet(T caster) {
        Config.CursedBulletConfig config = caster.getCursedBulletConfig();
        this.caster = caster;
        this.maxCooldown = config.max_cooldown;
        this.activationRange = config.range_of_activation;
        this.maxRangeAttack = config.max_range_of_attack;
        this.statusEffect = resolveStatusEffect(
                config.status_effect != null ? new Identifier(config.status_effect) : null
        );
        this.duration = config.effect_duration;
        this.amplifier = config.effect_amplifier;
        this.setControls(EnumSet.of(Goal.Control.LOOK, Control.TARGET));
    }

    protected SummonCursedBullet(MobEntity caster, Config.CursedBulletConfig config) {
        this.caster = caster;
        this.maxCooldown = config.max_cooldown;
        this.activationRange = config.range_of_activation;
        this.maxRangeAttack = config.max_range_of_attack;
        this.statusEffect = resolveStatusEffect(
                config.status_effect != null ? new Identifier(config.status_effect) : null
        );
        this.duration = config.effect_duration;
        this.amplifier = config.effect_amplifier;
        this.setControls(EnumSet.of(Goal.Control.LOOK, Control.TARGET));
    }

    private StatusEffect resolveStatusEffect(Identifier effectId) {
        if (effectId == null) {
            return StatusEffects.BLINDNESS;
        }
        StatusEffect effect = Registry.STATUS_EFFECT.get(effectId);
        return effect != null ? effect : StatusEffects.BLINDNESS;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) 
                && this.caster.squaredDistanceTo(target) > KastriaMobs.getSquared(activationRange);
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

    private void summonBullets() {
        if (caster.world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        
        LivingEntity target = caster.getTarget();
        if (target == null) {
            return;
        }
        
        double distanceSquared = caster.squaredDistanceTo(target);
        if (distanceSquared >= KastriaMobs.getSquared(maxRangeAttack)) {
            caster.setTarget(null);
            return;
        }
        
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
        caster.swingHand(Hand.MAIN_HAND);
        
        int randomizedCooldown = maxCooldown + caster.getRandom().nextInt(maxCooldown / COOLDOWN_RANDOMIZATION_DIVISOR);
        this.cooldown = randomizedCooldown;
        
        CursedBullet bullet = new CursedBullet(
                caster.world, caster, target, 
                caster.getMovementDirection().getAxis(), 
                statusEffect, duration, amplifier
        );
        caster.world.spawnEntity(bullet);
        
        float pitchVariation = (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F;
        caster.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, SOUND_VOLUME, pitchVariation);
    }
}
