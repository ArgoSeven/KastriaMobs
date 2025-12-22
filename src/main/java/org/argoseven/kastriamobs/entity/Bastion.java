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

public class Bastion extends HostileEntity implements IAnimatable  {
    private final String animation_prefix = "";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;
    private int deathTicks = 0;
    public Bastion(EntityType<? extends HostileEntity> entityType, World world) {
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
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false));
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, Config.data.bastion.generic_max_health)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, Config.data.bastion.generic_movement_speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, Config.data.bastion.generic_attack_damage)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, Config.data.bastion.generic_follow_range)
                .add(EntityAttributes.GENERIC_ARMOR, Config.data.bastion.generic_armor)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, Config.data.bastion.generic_armor_toughness)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Config.data.bastion.generic_knockback_resistance);
    }

    // Sound events using your available sounds
    @Override
    protected SoundEvent getAmbientSound() {return SoundEvents.ENTITY_DROWNED_STEP;}
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return SoundEvents.ENTITY_ELDER_GUARDIAN_HURT;}
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(getAmbientSound(), 0.15F, 0.69F);
    }



    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return true;
    }

    // Animation methods
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {

        if (this.isDead()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix +"death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return  PlayState.CONTINUE;
        }

        if ((event.isMoving())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "walk", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.isAttacking() && event.isMoving()) {
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "idle", ILoopType.EDefaultLoopTypes.LOOP));
        }
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
