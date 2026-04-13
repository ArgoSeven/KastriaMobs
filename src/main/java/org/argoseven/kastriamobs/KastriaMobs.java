package org.argoseven.kastriamobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.command.KastriaReload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class KastriaMobs implements ModInitializer {
    
    public static final String MOD_ID = "kastriamobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path configPath;



    @Override
    public void onInitialize() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("kastriamobs").resolve("kastriamobs.toml");
        Config.init();
        RegistryKastriaEntity.registerEntityAttributes();
        KastriaParticles.registerParticles();
        CommandRegistrationCallback.EVENT.register(KastriaReload::register);
        RegistryKastriaItems.registerItems();

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

    public static void spawnAncoredEyeParticle(LivingEntity entity, ParticleEffect particle, Double forwardOffset, Double horizontalOffset) {
        float yaw = (float) Math.toRadians(entity.getYaw());
        float pitch = (float) Math.toRadians(entity.getPitch());

        Vec3d eyePos = entity.getEyePos();

        // Forward direction
        double xDir = -Math.sin(yaw) * Math.cos(pitch);
        double yDir = -Math.sin(pitch);
        double zDir =  Math.cos(yaw) * Math.cos(pitch);

        // Right direction (yaw + 90°) — uses only yaw so it's horizontal
        double rightX = -Math.sin(yaw + Math.PI / 2.0);
        double rightZ =  Math.cos(yaw + Math.PI / 2.0);
        double rightY = 0.0;

       // double forwardDist = 0.5; // how far in front of eyes
       // double sideOffset = 0.15; // positive = right eye, negative = left eye

        Vec3d offsetPos = eyePos.add(xDir * forwardOffset, yDir * forwardOffset, zDir * forwardOffset)
                .add(rightX * horizontalOffset, 0, rightZ * horizontalOffset);

        if (entity.getWorld() instanceof ServerWorld serverWorld){
            serverWorld.spawnParticles(particle,
                    offsetPos.x, offsetPos.y, offsetPos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }


}
