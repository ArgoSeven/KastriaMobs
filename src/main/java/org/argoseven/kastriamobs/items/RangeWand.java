package org.argoseven.kastriamobs.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RangeWand extends Item {
    private MobEntity newTarget = null;
    public RangeWand(Settings settings) {
        super(settings);
    }


    @Override
    public boolean hasGlint(ItemStack stack) {
        if (newTarget != null) {
            return true;
        }
        return super.hasGlint(stack);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (newTarget != null && user.isSneaking()) {
            newTarget = null;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (this.newTarget != null) {
            tooltip.add(newTarget.getDisplayName());
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {

        if (!user.world.isClient()){
            if (newTarget == null && entity instanceof MobEntity mob) {
                newTarget =  mob;
                user.sendMessage(Text.literal(newTarget.getDisplayName().getString()), true);
            }else if (newTarget.isAlive() && entity instanceof MobEntity mob) {
                mob.setTarget(newTarget);
                newTarget.setTarget(mob);
                user.getItemCooldownManager().set(stack.getItem(), 10);
            }
        }

        return super.useOnEntity(stack, user, entity, hand);
    }
}
