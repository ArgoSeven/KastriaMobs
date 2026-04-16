package org.argoseven.kastriamobs.goals;

import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.KastriaUtils;
import org.argoseven.kastriamobs.entity.AbyssalCountess;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AbyssalCountessRayGoal extends Goal {

    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private double maxRange = 40;
    private double minRange = 5;

    public <T extends MobEntity> AbyssalCountessRayGoal(PathAwareEntity caster) {
        this.caster = caster;
        this.maxCooldown = 40;
    }

    @Override
    public boolean canStart() {
        return caster instanceof AbyssalCountess entity
            && entity.getTurretTarget() != null;
    }

    @Override
    public void tick() {
        if (--cooldown > 0) {return;}
        AbyssalCountess entity = (AbyssalCountess) caster;
        HashSet<LivingEntity> targets = entity.getTurretTarget();

        Iterator<LivingEntity> iterator = targets.iterator();
        while (iterator.hasNext()) {
            LivingEntity target = iterator.next();
            if (!target.isAlive()) {
                iterator.remove();
                continue;
            }

            if (caster.squaredDistanceTo(target) > maxRange * maxRange) {
                iterator.remove();
                continue;
            }

            if (caster.squaredDistanceTo(target) < minRange * minRange) {
                continue;
            }

            if (caster.world instanceof  ServerWorld serverWorld){
                Vec3d startPos = caster.getEyePos().add(0.0, 0.6, 0.0);
                caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                List<LivingEntity> hits = KastriaUtils.findEntitiesUnion(caster, target);
                KastriaUtils.applyDamageAndAttraction(caster, hits, startPos,2,1);
                spawnBeamParticles(serverWorld, startPos, target.getEyePos());
            }
        }

        cooldown = maxCooldown;
    }

    private void spawnBeamParticles(ServerWorld world, Vec3d startPos, Vec3d endPos) {
        Vec3d d = endPos.subtract(startPos).normalize();
        for (int i = 1; i <= endPos.subtract(startPos).length() + 3; i++) {
            Vec3d particlePos = startPos.add(d.multiply(i));
            world.spawnParticles(KastriaParticles.BLOOD_BEAM_PARTICLE,
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

}
