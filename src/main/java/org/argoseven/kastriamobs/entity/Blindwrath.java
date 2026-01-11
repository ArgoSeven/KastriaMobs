package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.goals.SonicBeam;

import java.util.List;

public class Blindwrath extends AbstractKastriaEntity implements ConfigProvider.SonicBeamProvider {

    public Blindwrath(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Config.SonicAttackConfig getSonicBeamConfig() {
        return Config.data.blindwrath.sonicbeam;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SonicBeam(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return createAttributes(Config.data.blindwrath);
    }

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

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.playSound(SoundEvents.ENTITY_DONKEY_DEATH, 1.0F, 0.50F);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);

        if (this.getWorld().isClient || !result) {
            return result;
        }

        if (source.getAttacker() instanceof PlayerEntity target) {
            KastriaMobs.LOGGER.info("Alerting nearby mobs");
            alertNearbyMobs(target, Config.data.blindwrath.alert_range);
        }

        return true;
    }
}
