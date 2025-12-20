package org.argoseven.kastriamobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.argoseven.kastriamobs.ModParticles;
import org.argoseven.kastriamobs.client.entity.BardRender;
import org.argoseven.kastriamobs.client.entity.PlagueRender;
import org.argoseven.kastriamobs.RegistryModdedEntity;
import org.argoseven.kastriamobs.client.entity.RedBloodMageRender;
import org.argoseven.kastriamobs.client.particles.BloodBeamParticle;
import org.argoseven.kastriamobs.client.particles.Notes;

import java.util.Random;

public class KastriaMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RegistryModdedEntity.CURSED_BRUTE, PlagueRender::new);
        EntityRendererRegistry.register(RegistryModdedEntity.RED_BLOOD_DMAGE, RedBloodMageRender::new);
        EntityRendererRegistry.register(RegistryModdedEntity.BARD, BardRender::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD_BEAM_PARTICLE, BloodBeamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.NOTES_PARTICLE, Notes.Factory::new);
    }


    public static int[] randomColor(){
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new int[]{red, green, blue};
    }
}
