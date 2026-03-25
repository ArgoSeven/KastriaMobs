package org.argoseven.kastriamobs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.goals.MutipleAttack;
import org.argoseven.kastriamobs.goals.AbyssalCountessCryGoal;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class AbyssalCountess extends AbstractKastriaEntity implements MutipleAttack, ConfigProvider.MeleeEffectProvider {
    private final String[] STATE_AVEIABLE = {"melee", "explosion", "cry"};
    private int RAGE_COUNTER = 0;
    private Vec3d LAST_RAGE_POS = new Vec3d(0.0D, 0.0D, 0.0D);

    private static final TrackedData<String> CURRENT_STATE = DataTracker.registerData(AbyssalCountess.class, TrackedDataHandlerRegistry.STRING);

    public AbyssalCountess(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        setAttack("melee");
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CURRENT_STATE, "melee");
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(1, new ZozinMeleeGoal(this));
        this.goalSelector.add(2, new AbyssalCountessCryGoal(this));
        //this.goalSelector.add(3, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(4, new SwimGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        
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
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() instanceof AreaEffectCloudEntity) return  false;

        if (RAGE_COUNTER >= 3 && source.getAttacker() instanceof  PlayerEntity player) {
            BlockPos attackPos = source.getAttacker().getBlockPos();

            player.sendMessage(Text.literal("Foolish mortal did you think my wings could be caged? Kneel and meet your doom").styled(style -> style.withColor(Formatting.DARK_RED) ));
            player.teleport(this.lastRenderX,  this.lastRenderY + 1, this.lastRenderZ);

            this.teleport(attackPos.getX(),attackPos.getY(),attackPos.getZ());
            player.setStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS,40 ,1), this);
            this.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, source.getAttacker().getEyePos());
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GHAST_WARN, SoundCategory.MASTER, 1.0f, 1.0f);
            RAGE_COUNTER = 0;
            return false;

        }

        if (source.getPosition() != null){
            if (LAST_RAGE_POS.isInRange(source.getPosition(), 2)){
                RAGE_COUNTER = RAGE_COUNTER + 1;
            }else {
                LAST_RAGE_POS = source.getPosition();
                RAGE_COUNTER = 0;
            }
        }

        return super.damage(source, amount);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            this.world.addParticle(KastriaParticles.BLOOD_ORB_PARTICLE, this.getEyePos().x, this.getEyePos().y + 0.6, this.getEyePos().z, 0.0, 0.0, 0.0);
        }
    }


    @Override
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
                    .addAnimation(getAttack(), ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }

        return PlayState.CONTINUE;
    }


    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (damageSource.getSource() == null ) return;
        damageSource.getSource().sendMessage(Text.of("I learned something about you"));

        this.playSound(SoundEvents.ENTITY_STRAY_DEATH, 1.0F, 0.90F);
    }

    @Override
    public Config.MeleeEffectConfig getMeleeEffect() {
        return Config.data.stalker.melee_effect;
    }

    @Override
    public void setAttack(String attack) {
       dataTracker.set(CURRENT_STATE, attack);
    }

    @Override
    public String getAttack() {
        return dataTracker.get(CURRENT_STATE);
    }
}
