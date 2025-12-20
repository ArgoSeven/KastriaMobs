package org.argoseven.kastriamobs;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.argoseven.kastriamobs.entity.Bard;
import org.argoseven.kastriamobs.entity.PlagueBrute;
import org.argoseven.kastriamobs.entity.RedBloodMage;

public class RegistryModdedEntity {

    private static <T extends net.minecraft.entity.Entity> EntityType<T> registerEntity(String name, EntityType.EntityFactory<T> factory, EntityDimensions entityDimensions) {
        return Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(KastriaMobs.MOD_ID, name),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, factory)
                        .dimensions(entityDimensions)
                        .build()
        );
    }

    public static final EntityType<PlagueBrute> CURSED_BRUTE = registerEntity("cursedbrute", PlagueBrute::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<RedBloodMage> RED_BLOOD_DMAGE = registerEntity("red_blood_mage", RedBloodMage::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Bard> BARD = registerEntity("bard", Bard::new, EntityDimensions.fixed(0.6f,1.8f));


    public static void register(){
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.CURSED_BRUTE, PlagueBrute.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.RED_BLOOD_DMAGE, RedBloodMage.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.BARD, Bard.setAttribute());
    }
}
