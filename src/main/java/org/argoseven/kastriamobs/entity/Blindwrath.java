package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.goals.SonicBeam;
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

public class Blindwrath extends HostileEntity implements IAnimatable  {
    private final String animation_prefix = "";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;
    private int deathTicks = 0;


    public Blindwrath(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(false);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }


    @Override
    protected void initGoals() {
        // Priority 1: Melee Attack
        //this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.add(1, new SonicBeam(this, Config.data.blindwrath.sonicbeam));
        this.goalSelector.add(2, new WanderAroundGoal(this, (double)1.0F));

        // Priority 2-6: Target goals
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new RevengeGoal(this));

        // Priority 7-8: Movement goals
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, Config.data.blindwrath.generic_max_health)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, Config.data.blindwrath.generic_movement_speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, Config.data.blindwrath.generic_attack_damage)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, Config.data.blindwrath.generic_follow_range)
                .add(EntityAttributes.GENERIC_ARMOR, Config.data.blindwrath.generic_armor)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, Config.data.blindwrath.generic_armor_toughness)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Config.data.blindwrath.generic_knockback_resistance);
    }


    // Sound events using your available sounds
    // Sound events using your available sounds
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_STRAY_STEP, 0.15F, 0.90F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_STRAY_AMBIENT, 0.15F, 0.69F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_DONKEY_HURT, 1.0F, 0.58F);
    }

    public void playCustomDeathSound(){
        this.playSound(SoundEvents.ENTITY_DONKEY_DEATH, 1.0F, 0.50F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        playCustomDeathSound();
    }


    // Animation methods
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.setAnimationSpeed(1.0F);
        if (this.isDead()) {
            controller.setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        if (this.swinging || this.handSwinging) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            double speed = this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            controller.setAnimationSpeed(1.0F + (float) speed * 0.5F);
            controller.setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "walk", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        controller.setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackingPredicate(AnimationEvent<E> event) {
        if(!this.isDead()) {
            if (this.getHandSwingProgress(event.getPartialTick()) > 0.0F && !this.swinging) {
                this.swinging = true;
                this.lastSwing = this.world.getTime();
            }

            if (this.swinging && this.lastSwing + 15L <= this.world.getTime()) {
                this.swinging = false;
            }

            if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
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

    @Override
    protected void updatePostDeath() {
        this.deathTicks++;
        if (this.deathTicks >= 30 && !this.world.isClient()) {
            this.remove(RemovalReason.KILLED);
        }
    }

}
