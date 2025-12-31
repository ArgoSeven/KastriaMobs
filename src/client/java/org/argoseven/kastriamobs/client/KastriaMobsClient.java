package org.argoseven.kastriamobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.RegistryKastriaEntity;
import org.argoseven.kastriamobs.client.debug.DebugShapeRenderer;
import org.argoseven.kastriamobs.client.entity.*;
import org.argoseven.kastriamobs.client.particles.BloodBeamParticle;
import org.argoseven.kastriamobs.client.particles.MagicCircle;
import org.argoseven.kastriamobs.client.particles.Notes;
import org.argoseven.kastriamobs.entity.*;
import org.argoseven.kastriamobs.network.DebugShapePackets;

import java.util.Random;

public class KastriaMobsClient implements ClientModInitializer {
    
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerParticles();
        registerTickHandler();
        registerDebugPacketReceivers();
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(RegistryKastriaEntity.BASTION, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<Bastion>("bastion")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.BLINDWRATH, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<Blindwrath>("blindwrath")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.HOLLOWSEER, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<Hollowseer>("hollowseer")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.PLAGUEBRUTE, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<PlagueBrute>("plaguebrute")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.REAVER, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<Reaver>("reaver")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.RED_BLOOD_MAGE, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<RedBloodMage>("red_blood_mage")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.STALKER, 
                ctx -> new KastriaEntityRenderer<>(ctx, new KastriaEntityModel<Stalker>("stalker")));
        
        EntityRendererRegistry.register(RegistryKastriaEntity.BARD, BardRender::new);
        
        EntityRendererRegistry.register(RegistryKastriaEntity.TOBIAS, TobiasRender::new);
    }

    private void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(
                KastriaParticles.BLOOD_BEAM_PARTICLE, BloodBeamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(
                KastriaParticles.NOTES_PARTICLE, Notes.Factory::new);
        ParticleFactoryRegistry.getInstance().register(
                KastriaParticles.MAGIC_CIRCLE, MagicCircle.Factory::new);
    }

    private void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            DebugShapeRenderer.tick();
        });
    }

    private void registerDebugPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(DebugShapePackets.DEBUG_BOX_PACKET, (client, handler, buf, responseSender) -> {
            double minX = buf.readDouble();
            double minY = buf.readDouble();
            double minZ = buf.readDouble();
            double maxX = buf.readDouble();
            double maxY = buf.readDouble();
            double maxZ = buf.readDouble();
            float red = buf.readFloat();
            float green = buf.readFloat();
            float blue = buf.readFloat();
            float alpha = buf.readFloat();
            long lifetimeTicks = buf.readLong();
            
            client.execute(() -> {
                DebugShapeRenderer.addBox(new Box(minX, minY, minZ, maxX, maxY, maxZ), red, green, blue, alpha, lifetimeTicks);
            });
        });
        
        ClientPlayNetworking.registerGlobalReceiver(DebugShapePackets.DEBUG_BEAM_PACKET, (client, handler, buf, responseSender) -> {
            double startX = buf.readDouble();
            double startY = buf.readDouble();
            double startZ = buf.readDouble();
            double endX = buf.readDouble();
            double endY = buf.readDouble();
            double endZ = buf.readDouble();
            float red = buf.readFloat();
            float green = buf.readFloat();
            float blue = buf.readFloat();
            float alpha = buf.readFloat();
            long lifetimeTicks = buf.readLong();
            
            client.execute(() -> {
                DebugShapeRenderer.addBeam(new Vec3d(startX, startY, startZ), new Vec3d(endX, endY, endZ), red, green, blue, alpha, lifetimeTicks);
            });
        });
    }
}
