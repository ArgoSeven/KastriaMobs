package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.goals.EvokeLineFangs;
import org.argoseven.kastriamobs.goals.SummonCursedBullet;

public class Hollowseer extends AbstractKastriaEntity {

    public Hollowseer(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(2, new SummonCursedBullet(this, Config.data.hollowseer.cursed_bullet));
        this.goalSelector.add(3, new EvokeLineFangs(this, Config.data.hollowseer.evoker_fang_beam));
        this.goalSelector.add(4, new SwimGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttribute() {
        return createAttributes(Config.data.hollowseer);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 0.84F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_DROWNED_AMBIENT, 0.15F, 0.69F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_ALLAY_HURT, 1.0F, 1.69F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.playSound(SoundEvents.ENTITY_STRAY_DEATH, 1.0F, 1.0F);
    }
}
