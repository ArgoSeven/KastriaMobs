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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;

public class Stalker extends AbstractKastriaEntity implements ConfigProvider.MeleeEffectProvider {

    public Stalker(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return createAttributes(Config.data.stalker);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 0.15F, 1.2F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_STRAY_AMBIENT, 0.15F, 0.69F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_STRAY_HURT, 1.0F, 0.90F);
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
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.playSound(SoundEvents.ENTITY_STRAY_DEATH, 1.0F, 0.90F);
    }

    @Override
    public Config.MeleeEffectConfig getMeleeEffect() {
        return Config.data.stalker.melee_effect;
    }
}
