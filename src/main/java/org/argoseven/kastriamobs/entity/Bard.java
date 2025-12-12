package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Bard extends HostileEntity implements IAnimatable {
    private final String prefix = "animation.skin.";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;


    public Bard(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(false);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new WanderAroundGoal(this, (double)1.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 20.0D)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.7D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1D);
    }

    // Custom damage handling - immune to arrows and fall damage
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() instanceof PersistentProjectileEntity) {
            return false;
        }
        if (source == DamageSource.FALL) {
            return false;
        }
        return super.damage(source, amount);
    }

    // Sound events using your available sounds
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PLAYER_BREATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_DEEPSLATE_STEP, 0.15F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_PLAYER_BREATH, 0.15F, 1.0F);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return true;
    }

    // Animation methods
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if ((event.isMoving())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(prefix + "walk", ILoopType.EDefaultLoopTypes.LOOP));
        } else if (this.isDead()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(prefix +"idle", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        } else if (this.isAttacking() && event.isMoving()) {
            return PlayState.STOP;
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(prefix + "idle", ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackingPredicate(AnimationEvent<E> event) {
        if (this.getHandSwingProgress(event.getPartialTick()) > 0.0F && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.world.getTime();
        }

        if (this.swinging && this.lastSwing + 15L <= this.world.getTime()) {
            this.swinging = false;
        }

        if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation(prefix + "attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.addAnimationController(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.swinging = true;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
