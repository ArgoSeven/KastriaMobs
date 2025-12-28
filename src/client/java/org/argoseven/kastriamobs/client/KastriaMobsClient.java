package org.argoseven.kastriamobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.RegistryKastriaEntity;
import org.argoseven.kastriamobs.client.entity.*;
import org.argoseven.kastriamobs.client.entity.BastionRender;
import org.argoseven.kastriamobs.client.entity.BlindwrathRender;
import org.argoseven.kastriamobs.client.entity.HollowseerRender;
import org.argoseven.kastriamobs.client.entity.ReaverRender;
import org.argoseven.kastriamobs.client.entity.StalkerRender;
import org.argoseven.kastriamobs.client.particles.BloodBeamParticle;
import org.argoseven.kastriamobs.client.particles.MagicCircle;
import org.argoseven.kastriamobs.client.particles.Notes;

import java.util.Random;

public class KastriaMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RegistryKastriaEntity.PLAGUEBRUTE, PlagueRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.RED_BLOOD_MAGE, RedBloodMageRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.BARD, BardRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.BARD, BardRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.TOBIAS, TobiasRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.BASTION, BastionRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.BLINDWRATH, BlindwrathRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.HOLLOWSEER, HollowseerRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.REAVER, ReaverRender::new);
        EntityRendererRegistry.register(RegistryKastriaEntity.STALKER, StalkerRender::new);

        ParticleFactoryRegistry.getInstance().register(KastriaParticles.BLOOD_BEAM_PARTICLE, BloodBeamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(KastriaParticles.NOTES_PARTICLE, Notes.Factory::new);
        ParticleFactoryRegistry.getInstance().register(KastriaParticles.MAGIC_CIRCLE, MagicCircle.Factory::new);
    }


    public static int[] randomColor(){
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new int[]{red, green, blue};
    }
}
