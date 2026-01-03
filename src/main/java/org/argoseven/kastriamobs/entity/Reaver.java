package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.goals.EvokeCircleFangs;

public class Reaver extends AbstractKastriaEntity implements ConfigProvider.CircleFangsProvider, ConfigProvider.MeleeEffectProvider {

    public Reaver(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Config.FangAttackConfig getCircleFangsConfig() {
        return Config.data.reaver.evoker_fang_circle;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new EvokeCircleFangs(this));
        this.goalSelector.add(4, new SwimGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return createAttributes(Config.data.reaver);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (target instanceof LivingEntity && (getMeleeEffect() != null)) {
            StatusEffect effectId = Registry.STATUS_EFFECT.get(new Identifier(getMeleeEffect().status_effect));
            if (effectId == null) return bl;
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(effectId, getMeleeEffect().effect_duration, getMeleeEffect().effect_amplifier), this);
        }
        return  bl;
    }


    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 0.84F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_DROWNED_AMBIENT, 0.15F, 0.8F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_DROWNED_HURT, 1.0F, 1.69F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.playSound(SoundEvents.ENTITY_GHAST_DEATH, 1.0F, 0.50F);
    }

    @Override
    public Config.MeleeEffectConfig getMeleeEffect() {
        return Config.data.reaver.melee_effect;
    }
}
