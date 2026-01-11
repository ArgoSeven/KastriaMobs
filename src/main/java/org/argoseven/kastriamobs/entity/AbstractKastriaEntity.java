package org.argoseven.kastriamobs.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.network.DebugShapePackets;
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

import java.util.List;

public abstract class AbstractKastriaEntity extends HostileEntity implements IAnimatable {
    
    private static final long SWING_DURATION_TICKS = 15L;
    private static final int DEATH_REMOVAL_DELAY_TICKS = 30;
    private static final float BASE_ANIMATION_SPEED = 1.0F;
    private static final float ANIMATION_SPEED_MULTIPLIER = 0.5F;
    
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwingTime;
    private int deathTicks = 0;

    protected AbstractKastriaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(false);
    }

    public static DefaultAttributeContainer.Builder createAttributes(Config.EntityStatsConfig config) {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, config.max_health)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, config.movement_speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, config.attack_damage)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, config.follow_range)
                .add(EntityAttributes.GENERIC_ARMOR, config.armor)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, config.armor_toughness)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,config.knockback_resistance);
    }

    protected <E extends IAnimatable> PlayState handleMovementAnimation(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.setAnimationSpeed(BASE_ANIMATION_SPEED);
        
        if (this.isDead()) {
            controller.setAnimation(new AnimationBuilder()
                    .addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        
        if (this.swinging || this.handSwinging) {
            return PlayState.STOP;
        }
        
        if (event.isMoving()) {
            double mob_speed = this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            controller.setAnimationSpeed(BASE_ANIMATION_SPEED + (float) mob_speed * ANIMATION_SPEED_MULTIPLIER);
            controller.setAnimation(new AnimationBuilder()
                    .addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        
        controller.setAnimation(new AnimationBuilder()
                .addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    protected <E extends IAnimatable> PlayState handleAttackAnimation(AnimationEvent<E> event) {
        if (this.isDead()) {
            return PlayState.STOP;
        }
        
        if (this.getHandSwingProgress(event.getPartialTick()) > 0.0F && !this.swinging) {
            this.swinging = true;
            this.lastSwingTime = this.world.getTime();
        }

        if (this.swinging && this.lastSwingTime + SWING_DURATION_TICKS <= this.world.getTime()) {
            this.swinging = false;
        }

        if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder()
                    .addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movement", 0, this::handleMovementAnimation));
        data.addAnimationController(new AnimationController<>(this, "attacking", 0, this::handleAttackAnimation));
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
        if (this.deathTicks >= DEATH_REMOVAL_DELAY_TICKS && !this.world.isClient()) {
            this.remove(RemovalReason.KILLED);
        }
    }
    
    protected boolean isSwinging() {
        return this.swinging;
    }
    
    protected void setSwinging(boolean swinging) {
        this.swinging = swinging;
    }
    
    protected long getLastSwingTime() {
        return this.lastSwingTime;
    }
    
    protected void setLastSwingTime(long time) {
        this.lastSwingTime = time;
    }


    protected void alertNearbyMobs(PlayerEntity target, double radius) {
        if (target.getWorld().isClient) {
            return;
        }
        List<HostileEntity> entities = getHostileMobsInRadius(radius);
        for (HostileEntity mob : entities) {
                mob.setAttacker(target);
                if (DebugShapePackets.isDebugEnabled()){
                    mob.setStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60,1), this);
                }
                mob.setTarget(target);
                mob.getNavigation().startMovingTo(target, mob.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        }
    }

    private List<HostileEntity> getHostileMobsInRadius(double radius) {
        Vec3d pos = this.getPos();
        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        /*
        return world.getEntitiesByClass(
                HostileEntity.class,
                box, LivingEntity::isAlive
        );
        */

        return world.getEntitiesByClass(
                HostileEntity.class,
                box,
                entity -> entity.isAlive() && (entity.getTarget() == null)
        );
    }
}
