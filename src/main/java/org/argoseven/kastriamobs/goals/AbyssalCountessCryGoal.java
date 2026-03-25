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
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaMobs;

public class AbyssalCountessCryGoal extends Goal {

    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;

public <T extends MobEntity> AbyssalCountessCryGoal(T caster) {
        this.caster = caster;
        this.maxCooldown = 100;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        
        return this.caster.canTarget(target) && this.caster.canSee(target);
    }

    @Override
    public void start() {
        cooldown = maxCooldown;
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



        //CRY EFFECT
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());

        KastriaMobs.spawnAncoredEyeParticle(caster, ParticleTypes.FALLING_LAVA, 0.6, 0.15);
        KastriaMobs.spawnAncoredEyeParticle(caster, ParticleTypes.FALLING_LAVA, 0.6, -0.15);


        int red = 168;
        int green = 20;
        int blue = 28;
        int color = (red << 16) | (green << 8) | blue;

        AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity(this.caster.world, caster.getX(), caster.getY(), caster.getZ());
        areaEffectCloud.refreshPositionAndAngles(caster.getX(), caster.getY(), caster.getZ(), 0.0f, 0.0f);
        areaEffectCloud.setColor(color);
        areaEffectCloud.setRadiusGrowth(0.05F);
        areaEffectCloud.setDuration(5  * 20);
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

        caster.swingHand(Hand.MAIN_HAND);
        if (caster instanceof  MutipleAttack){
            ((MutipleAttack) caster).setAttack("cry");
        }
    }


    public static Vec3d getEyeSide(LivingEntity entity, double sideDistance, double forwardOffset, int side) {
        // Use head yaw if you want the effect to follow the head instead of the body
        double yaw = Math.toRadians(entity.getYaw());
        double pitch = Math.toRadians(entity.getPitch());
        // Forward vector with pitch
        Vec3d forward = new Vec3d(
                -Math.sin(yaw) * Math.cos(pitch),
                -Math.sin(pitch),
                Math.cos(yaw) * Math.cos(pitch)
        ).normalize();

        // Left/right vector = forward × up
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d sideDir = forward.crossProduct(up).normalize();

        // side = +1 → right, side = -1 → left
        sideDir = sideDir.multiply(side);
        Vec3d base = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
        return base.add(sideDir.multiply(sideDistance)).add(forward.multiply(forwardOffset));
    }


}
