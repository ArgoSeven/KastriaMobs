package org.argoseven.kastriamobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class KastriaMobs implements ModInitializer {
    
    public static final String MOD_ID = "kastriamobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path configPath;

    @Override
    public void onInitialize() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("kastriamobs.toml");
        Config.init();
        RegistryKastriaEntity.registerEntityAttributes();
        KastriaParticles.registerParticles();
        RegistryKastriaItems.registerItem();

        registerConfigReloader();
    }

    private void registerConfigReloader() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier(MOD_ID, "config_reloader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        LOGGER.info("Reloading Kastria Config...");
                        Config.init();
                        RegistryKastriaEntity.registerEntityAttributes();
                    }
                }
        );
    }

    public static double getSquared(double range) {
        return range * range;
    }

    public static void moveAndRetreat(MobEntity caster, LivingEntity target, double maxRange) {
        if (target == null) {
            return;
        }

        double squaredDistance = getSquared(maxRange);
        float strafeDirection = caster.getRandom().nextFloat() < 0.3 ? -0.5F : 0.5F;

        if (caster.squaredDistanceTo(target) >= squaredDistance && caster.getNavigation().isIdle() && !caster.getMoveControl().isMoving()) {
            caster.getNavigation().startMovingTo(target, 1);
        } else if (!caster.getMoveControl().isMoving()){
            caster.getNavigation().stop();
            caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
            caster.getMoveControl().strafeTo(-1.0f, 0);
        }
    }
}
