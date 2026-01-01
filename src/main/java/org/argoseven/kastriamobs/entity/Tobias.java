package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaParticles;

public class Tobias extends AbstractKastriaEntity implements RangedAttackMob {
    
    private static final double SLOWNESS_RANGE = 8.0;
    private static final double WEAKNESS_RANGE = 3.0;
    private static final float WEAKNESS_CHANCE = 0.25f;
    private static final float POISON_HEALTH_THRESHOLD = 8.0f;

    public Tobias(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return createAttributes(Config.data.bard);
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
    public void attack(LivingEntity target, float pullProgress) {
        Vec3d velocity = target.getVelocity();
        double deltaX = target.getX() + velocity.x - this.getX();
        double deltaY = target.getEyeY() - 1.1 - this.getY();
        double deltaZ = target.getZ() + velocity.z - this.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        Potion potion = selectPotionForTarget(target, horizontalDistance);
        this.swingHand(Hand.MAIN_HAND);
        PotionEntity potionEntity = new PotionEntity(this.world, this);
        potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionEntity.setPitch(potionEntity.getPitch() + 20.0f);
        potionEntity.setVelocity(deltaX, deltaY + horizontalDistance * 0.2, deltaZ, 0.75f, 8.0f);
        
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), 
                    SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 
                    1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
        this.world.spawnEntity(potionEntity);
    }
    
    private Potion selectPotionForTarget(LivingEntity target, double distance) {
        if (target instanceof RaiderEntity) {
            this.setTarget(null);
            return target.getHealth() <= 4.0f ? Potions.HEALING : Potions.REGENERATION;
        }
        
        if (distance >= SLOWNESS_RANGE && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
            return Potions.SLOWNESS;
        }
        if (target.getHealth() >= POISON_HEALTH_THRESHOLD && !target.hasStatusEffect(StatusEffects.POISON)) {
            return Potions.POISON;
        }
        if (distance <= WEAKNESS_RANGE && !target.hasStatusEffect(StatusEffects.WEAKNESS) 
                && this.random.nextFloat() < WEAKNESS_CHANCE) {
            return Potions.WEAKNESS;
        }
        if (this.world instanceof ServerWorld serverWorld) { serverWorld.spawnParticles( KastriaParticles.MAGIC_CIRCLE, this.getX(), this.getY() + 0.1, this.getZ(), 1, 0.0, 0.0, 0.0, 0 ); }
        return Potions.HARMING;
    }
    @Override
    protected void updatePostDeath() {
        this.remove(RemovalReason.KILLED);
    }
}
