package org.argoseven.kastriamobs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.network.DebugShapePackets;

import java.util.List;

public class KastriaUtils {
    public static List<LivingEntity> findEntitiesInBeam(LivingEntity caster, Vec3d lookVec, Vec3d eyePos, Double range) {
        ServerWorld world = (ServerWorld)caster.getEntityWorld();
        Box searchBox = caster.getBoundingBox().stretch(lookVec.multiply(range));

        if (DebugShapePackets.isDebugEnabled()) {
            DebugShapePackets.sendDebugBox(world, searchBox, 0.0f, 1.0f, 0.0f, 0.5f, 20);
        }

        return world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != caster
                        && !entity.isTeammate(caster)
                        && isEntityInCone(eyePos, lookVec, entity)
                        && !entity.isSpectator()
        );
    }

    public static List<LivingEntity> findEntitiesUnion(LivingEntity caster, LivingEntity target) {
        ServerWorld world = (ServerWorld) caster.getEntityWorld();

        Box searchBox = caster.getBoundingBox().union(target.getBoundingBox());

        if (DebugShapePackets.isDebugEnabled()) {
            DebugShapePackets.sendDebugBox(world, searchBox, 1.0f, 0.0f, 0.0f, 0.5f, 20);
        }

        return world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != caster
                        && !entity.isTeammate(caster)
                        && !entity.isSpectator()
        );
    }

    public static void applyDamageAndKnockback(LivingEntity caster, LivingEntity hit, Vec3d direction, double power, int damage) {
        double knockResistance = hit.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        double verticalKnock = 1 * (1.0 - knockResistance);
        double horizontalKnock = power * (1.0 - knockResistance);
        hit.damage(DamageSource.sonicBoom(caster), damage);


        hit.addVelocity(
                direction.getX() * horizontalKnock,
                direction.getY() * verticalKnock,
                direction.getZ() * horizontalKnock
        );
        hit.velocityModified = true;
    }

    public static boolean isEntityInCone(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Vec3d toEntity = target.getPos().subtract(eyePos);
        double dot = lookVec.dotProduct(toEntity);
        double lengthSquared = toEntity.lengthSquared();
        if (dot < 1.0E-4) {
            return false;
        }
        return dot * dot > lengthSquared * (0.60 * 0.60);
    }

    public static void applyDamageAndAttraction(LivingEntity caster , List<LivingEntity> hits, Vec3d startPos,double power ,int damage) {
        for (LivingEntity hit : hits) {
            hit.damage(DamageSource.sonicBoom(caster), damage);

            Vec3d attraction = startPos.subtract(hit.getEyePos()).normalize();
            hit.addVelocity(
                    attraction.getX() *  1.5,
                    attraction.getY() *  1.5,
                    attraction.getZ() *  1.5
            );
            hit.velocityModified = true;
        }
    }
}
