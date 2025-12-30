package org.argoseven.kastriamobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.RegistryKastriaEntity;
import org.argoseven.kastriamobs.client.entity.*;
import org.argoseven.kastriamobs.client.particles.BloodBeamParticle;
import org.argoseven.kastriamobs.client.particles.MagicCircle;
import org.argoseven.kastriamobs.client.particles.Notes;
import org.argoseven.kastriamobs.entity.*;

import java.util.Random;

public class KastriaMobsClient implements ClientModInitializer {
    
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerParticles();
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

    public static int[] randomColor() {
        int red = RANDOM.nextInt(256);
        int green = RANDOM.nextInt(256);
        int blue = RANDOM.nextInt(256);
        return new int[]{red, green, blue};
    }
}
