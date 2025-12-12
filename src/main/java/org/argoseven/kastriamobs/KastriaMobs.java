package org.argoseven.kastriamobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.argoseven.kastriamobs.entity.CursedBrute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KastriaMobs implements ModInitializer {
    public static final String MOD_ID = "kastriamobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public void onInitialize() {
        RegistryModdedEntity.register();
    }
}
