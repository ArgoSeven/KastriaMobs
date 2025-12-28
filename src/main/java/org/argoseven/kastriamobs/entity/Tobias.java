package org.argoseven.kastriamobs.entity;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaParticles;
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

public class Tobias extends HostileEntity implements IAnimatable, RangedAttackMob {
    private final String animation_prefix = "";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;


    public Tobias(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(false);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }


    @Override
    protected void initGoals() {
        //this.goalSelector.add(1,  new MeleeAttackGoal(this, 1, true));
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
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
    public void attack(LivingEntity target, float pullProgress) {
        Vec3d vec3d = target.getVelocity();
        double d = target.getX() + vec3d.x - this.getX();
        double e = target.getEyeY() - (double)1.1f - this.getY();
        double f = target.getZ() + vec3d.z - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        Potion potion = Potions.HARMING;
        if (target instanceof RaiderEntity) {
            potion = target.getHealth() <= 4.0f ? Potions.HEALING : Potions.REGENERATION;
            this.setTarget(null);
        } else if (g >= 8.0 && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
            potion = Potions.SLOWNESS;
        } else if (target.getHealth() >= 8.0f && !target.hasStatusEffect(StatusEffects.POISON)) {
            potion = Potions.POISON;
        } else if (g <= 3.0 && !target.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25f) {
            potion = Potions.WEAKNESS;
        }
        PotionEntity potionEntity = new PotionEntity(this.world, this);
        potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionEntity.setPitch(potionEntity.getPitch() - -20.0f);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75f, 8.0f);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
        this.world.spawnEntity(potionEntity);
    }
}
