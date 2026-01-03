package org.argoseven.kastriamobs.entity;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class CursedBullet extends ShulkerBulletEntity {
    private final StatusEffect statusEffect;
    private final int duration;
    private final int amplifier;

    public CursedBullet(EntityType<? extends ShulkerBulletEntity> entityType, World world, StatusEffect statusEffect, int duration, int amplifier) {
        super(entityType, world);
        this.statusEffect = statusEffect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public CursedBullet(World world, LivingEntity owner, Entity target, Direction.Axis axis,StatusEffect statusEffect,  int duration, int amplifier) {
        super(world, owner, target, axis);
        this.statusEffect = statusEffect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.refreshPositionAndAngles(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYaw(), owner.getPitch());
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && this.getOwner() != entity && !entity.isTeammate(this);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        boolean bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), 4.0F);
        if (bl) {
            this.applyDamageEffects(livingEntity, entity);
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier), (Entity) MoreObjects.firstNonNull(entity2, this));
            }

        }
    }
}
