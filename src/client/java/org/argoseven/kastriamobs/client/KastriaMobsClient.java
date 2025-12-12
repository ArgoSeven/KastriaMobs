package org.argoseven.kastriamobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.argoseven.kastriamobs.client.entity.BardRender;
import org.argoseven.kastriamobs.client.entity.CursedBruteRender;
import org.argoseven.kastriamobs.RegistryModdedEntity;
import org.argoseven.kastriamobs.client.entity.RedBloodMageRender;

public class KastriaMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RegistryModdedEntity.CURSED_BRUTE, CursedBruteRender::new);
        EntityRendererRegistry.register(RegistryModdedEntity.RED_BLOOD_DMAGE, RedBloodMageRender::new);
        EntityRendererRegistry.register(RegistryModdedEntity.BARD, BardRender::new);

    }
}
