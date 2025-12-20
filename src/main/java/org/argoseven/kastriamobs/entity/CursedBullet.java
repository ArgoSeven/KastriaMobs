package org.argoseven.kastriamobs.entity;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CursedBullet extends ShulkerBulletEntity {
    private StatusEffectInstance statusEffects  = null;

    public CursedBullet(EntityType<? extends ShulkerBulletEntity> entityType, World world, StatusEffectInstance statusEffectInstance) {
        super(entityType, world);
        this.statusEffects = statusEffectInstance;
    }

    public CursedBullet(World world, LivingEntity owner, Entity target, Direction.Axis axis, StatusEffectInstance statusEffectInstance) {
        super(world, owner, target, axis);
        this.refreshPositionAndAngles(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYaw(), owner.getPitch());
        this.statusEffects = statusEffectInstance;
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && this.getOwner() != entity;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        boolean bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), 4.0F);
        if (bl) {
            this.applyDamageEffects(livingEntity, entity);
            if (entity instanceof LivingEntity && statusEffects != null) {
                ((LivingEntity)entity).addStatusEffect(statusEffects, (Entity) MoreObjects.firstNonNull(entity2, this));
            }
        }
        super.onEntityHit(entityHitResult);
    }
}
