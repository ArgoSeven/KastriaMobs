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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.goals.BloodBeam;
import org.argoseven.kastriamobs.goals.SummonCursedBullet;
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

public class Bard extends HostileEntity implements IAnimatable, RangedAttackMob {
    private final String animation_prefix = "";
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
        this.goalSelector.add(1,  new MeleeAttackGoal(this, 1, true));
        this.goalSelector.add(2,  new BloodBeam( this, 20, 10, 1,1));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(2,  new SummonCursedBullet(this, 40, 10 ,20));
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
    public void tick() {
        super.tick();
        if (this.getTarget()!=null && this.getTarget() instanceof  PlayerEntity player){
            player.sendMessage(Text.of(String.valueOf(Config.data.blindwrath.generic_movement_speed)),true);
        }
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
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "walk", ILoopType.EDefaultLoopTypes.LOOP));
        } else if (this.isDead()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix +"idle", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        } else if (this.isAttacking() && event.isMoving()) {
            return PlayState.STOP;
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation_prefix + "idle", ILoopType.EDefaultLoopTypes.LOOP));
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
    protected ActionResult interactMob(PlayerEntity pPlayer, Hand pHand) {
        ItemStack item = pPlayer.getStackInHand(pHand);
        if (item != null && !item.isEmpty() && !this.world.isClient()) {
            if (item.getItem() instanceof ArmorItem) {
                ArmorItem ai = (ArmorItem)item.getItem();
                this.equipStack(ai.getSlotType(), item);
            } else if (item.getItem() instanceof BlockItem && ((BlockItem)item.getItem()).getBlock() instanceof AbstractSkullBlock) {
                this.equipStack(EquipmentSlot.HEAD, item);
            } else {
                this.setStackInHand(pHand, item);
            }

            pPlayer.sendMessage(Text.literal("Equipped item: " + item.getItem().getTranslationKey() + "!"));
            return ActionResult.SUCCESS;
        } else {
            return super.interactMob(pPlayer, pHand);
        }
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * (double)0.2F, f, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(persistentProjectileEntity);
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
    }

}
