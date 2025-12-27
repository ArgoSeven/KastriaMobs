package org.argoseven.kastriamobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;


public class KastriaMobs implements ModInitializer {
    public static final String MOD_ID = "kastriamobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path configPath;

    public void onInitialize() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("kastriamobs.toml");
        Config.init();
        RegistryModdedEntity.register();
        ModParticles.registerParticles();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return  null;
            }

            @Override
            public void reload(ResourceManager manager) {
                LOGGER.info("Reloading Kastria Config...");
                Config.init();
                RegistryModdedEntity.register();
            }
        });
    }


    public static double getSquared(double range){
        return range * range;
    }



    public static void moveAndRetreat(MobEntity caster, LivingEntity target, double maxRange){
        double squaredDistance = KastriaMobs.getSquared(maxRange);
        float flank = caster.getRandom().nextFloat() < 0.3 ? -0.5F : 0.5F;

        if(caster.squaredDistanceTo(target) >= squaredDistance && caster.getNavigation().isIdle()){
            caster.getNavigation().startMovingTo(target, 1);
        }else{
            caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
            caster.getNavigation().stop();
            caster.getMoveControl().strafeTo( -1.0f, 0);
        }
    }


    public static void debugvisualizeBox(ServerWorld world, Box box) {
        DefaultParticleType particleType = ParticleTypes.END_ROD;

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        double step = 0.3;

        // Loop over edges of the box
        for (double x = minX; x <= maxX; x += step) {
            for (double y = minY; y <= maxY; y += step) {
                world.spawnParticles(particleType, x, y, minZ, 1, 0, 0, 0, 0);
                world.spawnParticles(particleType, x, y, maxZ, 1, 0, 0, 0, 0);
            }
        }
        for (double z = minZ; z <= maxZ; z += step) {
            for (double y = minY; y <= maxY; y += step) {
                world.spawnParticles(particleType, minX, y, z, 1, 0, 0, 0, 0);
                world.spawnParticles(particleType, maxX, y, z, 1, 0, 0, 0, 0);
            }
        }
        for (double x = minX; x <= maxX; x += step) {
            for (double z = minZ; z <= maxZ; z += step) {
                world.spawnParticles(particleType, x, minY, z, 1, 0, 0, 0, 0);
                world.spawnParticles(particleType, x, maxY, z, 1, 0, 0, 0, 0);
            }
        }
    }
}
