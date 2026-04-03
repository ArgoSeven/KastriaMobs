package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.AbyssalCountess;

import java.util.EnumSet;


public class AbyssalCountessCryGoal extends Goal {

    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;

    private int areaEffectSize = 10;
    private int areaEffectDuration = 5 * 20;


public <T extends MobEntity> AbyssalCountessCryGoal(T caster) {
            this.caster = caster;
            this.maxCooldown = 100;
            this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
}

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) && this.caster.canSee(target) && this.caster.distanceTo(target) < areaEffectSize;
    }

    @Override
    public void start() {
       // cooldown = 0;
    }

    @Override
    public void tick() {
        if (--cooldown <= 0) {
            cry();
            cooldown = maxCooldown;
        }
    }

    private void cry() {
        LivingEntity target = caster.getTarget();
        if (target == null) {
            return;
        }


        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
        KastriaMobs.spawnAncoredEyeParticle(caster, ParticleTypes.FALLING_LAVA, 0.6, 0.15);
        KastriaMobs.spawnAncoredEyeParticle(caster, ParticleTypes.FALLING_LAVA, 0.6, -0.15);


        AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity(this.caster.world, caster.getX(), caster.getY(), caster.getZ());
        areaEffectCloud.refreshPositionAndAngles(caster.getX(), caster.getY(), caster.getZ(), 0.0f, 0.0f);
        areaEffectCloud.setColor(ColorHelper.Argb.getArgb(1,168, 20,28));
        areaEffectCloud.setRadiusGrowth((float) areaEffectSize / areaEffectDuration);
        areaEffectCloud.setDuration(areaEffectDuration);
        areaEffectCloud.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE));
        areaEffectCloud.setOwner(caster);
        caster.world.spawnEntity(areaEffectCloud);
        caster.setStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 254), null);


        // Create bullets high in the sky
        /*
        for (int i = 0; i < 10; i++) {
            SmallFireballEntity bullet = new SmallFireballEntity(this.caster.world, target, 0, -1, 0 ); // Shoot straight down
            // Set the fireball's position to the defined sky height
            bullet.refreshPositionAndAngles(baseX, skyHeight, baseZ, 0.0f, 0.0f);
             // Spawn the bullet in the world
            caster.world.spawnEntity(bullet);
        }*/



        if (caster instanceof MutipleAttack ){
            ((MutipleAttack) caster).setAttackAnimation("cry");
        }
        caster.swingHand(Hand.MAIN_HAND);
    }
}
