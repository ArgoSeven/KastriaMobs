package org.argoseven.kastriamobs.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;


public class FireballProjectile extends AbstractFireballEntity {
    private int maxAge = 126;
    private LivingEntity target;

    public FireballProjectile(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
        this.setVelocity(0, 0, 0);
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.age >= this.getMaxAge()) {
            if ( target == null || !target.isAlive()){
                this.setVelocity(0, -1, 0, 1f, 1f);
                this.velocityModified = true;
            }

            double f = target.getX() - this.getX();
            double g = target.getBodyY(0.5) - (this.getBodyY(0.5D));
            double h = target.getZ() - this.getZ();
            this.setVelocity(f, g, h, 1f, 1f);
            this.velocityModified = true;
        }
    }


    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        int i = entity.getFireTicks();
        if (entity2 instanceof LivingEntity) {
            this.applyDamageEffects((LivingEntity)entity2, entity);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return !(entity instanceof AbyssalCountess);
    }
    
    @Override
    protected boolean isBurning() {
        return false;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }
}
