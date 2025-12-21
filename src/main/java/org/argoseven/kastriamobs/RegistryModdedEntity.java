package org.argoseven.kastriamobs;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.argoseven.kastriamobs.entity.*;

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

    public static final EntityType<PlagueBrute> PLAGUEBRUTE = registerEntity("plaguebrute", PlagueBrute::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<RedBloodMage> RED_BLOOD_DMAGE = registerEntity("red_blood_mage", RedBloodMage::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Blindwrath> BLINDWRATH = registerEntity("blindwrath", Blindwrath::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Hollowseer> HOLLOWSEER = registerEntity("hollowseer", Hollowseer::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Reaver> REAVER = registerEntity("reaver", Reaver::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Stalker> STALKER = registerEntity("stalker", Stalker::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Bastion> BASTION = registerEntity("bastion", Bastion::new, EntityDimensions.fixed(0.6f,1.8f));


    public static final EntityType<Bard> BARD = registerEntity("bard", Bard::new, EntityDimensions.fixed(0.6f,1.8f));


    public static void register(){
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.PLAGUEBRUTE, PlagueBrute.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.RED_BLOOD_DMAGE, RedBloodMage.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.BLINDWRATH, Blindwrath.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.HOLLOWSEER, Hollowseer.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.REAVER, Reaver.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.STALKER, Stalker.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.BASTION, Bastion.setAttribute());

        FabricDefaultAttributeRegistry.register(RegistryModdedEntity.BARD, Bard.setAttribute());
    }
}
