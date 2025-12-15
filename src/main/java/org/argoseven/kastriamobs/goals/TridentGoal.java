package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Hand;

public class TridentGoal<T extends HostileEntity & RangedAttackMob> extends ProjectileAttackGoal {

    protected final T mob;

    public TridentGoal(T mob, double speed, int interval, float range) {
        super(mob, speed, interval, range);
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return super.canStart();
    }

    @Override
    public void start() {
        super.start();
        mob.setAttacking(true);
        mob.setCurrentHand(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
        super.stop();
        mob.clearActiveItem();
        mob.setAttacking(false);
    }
}
