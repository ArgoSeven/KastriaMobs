package org.argoseven.kastriamobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.argoseven.kastriamobs.entity.CursedBrute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KastriaMobs implements ModInitializer {
    public static final String MOD_ID = "kastriamobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public void onInitialize() {
        RegistryModdedEntity.register();
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

    public static void debugvisualizeBox(World world, Box box) {
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
                world.addParticle(particleType, x, y, minZ, 0, 0, 0);
                world.addParticle(particleType, x, y, maxZ, 0, 0, 0);
            }
        }
        for (double z = minZ; z <= maxZ; z += step) {
            for (double y = minY; y <= maxY; y += step) {
                world.addParticle(particleType, minX, y, z, 0, 0, 0);
                world.addParticle(particleType, maxX, y, z, 0, 0, 0);
            }
        }
        for (double x = minX; x <= maxX; x += step) {
            for (double z = minZ; z <= maxZ; z += step) {
                world.addParticle(particleType, x, minY, z, 0, 0, 0);
                world.addParticle(particleType, x, maxY, z, 0, 0, 0);
            }
        }
    }
}
