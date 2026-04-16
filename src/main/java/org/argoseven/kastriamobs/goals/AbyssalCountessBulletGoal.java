package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.argoseven.kastriamobs.RegistryKastriaEntity;
import org.argoseven.kastriamobs.entity.FireballProjectile;

import java.util.Random;

public class AbyssalCountessBulletGoal extends MeleeAttackGoal {

    private final MobEntity caster;

    public <T extends MobEntity> AbyssalCountessBulletGoal(PathAwareEntity caster) {
        super(caster, 1, false);
        this.caster = caster;
    }


    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.getCooldown() <= 0) {
            Random r = new Random();

            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            ServerWorld serverWorld = (ServerWorld) caster.world;

            if (caster instanceof  MutipleAttack) {
                ((MutipleAttack) caster).setAttackAnimation("melee");
            }
            for (int i = 0; i < r.nextInt(4); i++) {
                FireballProjectile bullet = new FireballProjectile(RegistryKastriaEntity.FIREBALL_PROJECTILE, this.caster.world);
                bullet.refreshPositionAndAngles(caster.getX() + r.nextInt(2), caster.getEyeY() + 0.6 + r.nextInt(2), caster.getZ() + r.nextInt(2), 0.0f, 0.0f);
                bullet.setTarget(caster.getTarget());
                bullet.setItem(Items.NETHERITE_BLOCK.getDefaultStack());
                bullet.setOwner(caster);
                caster.world.spawnEntity(bullet);
            }

            caster.swingHand(Hand.MAIN_HAND);
        }
    }
}
