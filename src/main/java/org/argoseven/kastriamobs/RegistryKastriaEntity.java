package org.argoseven.kastriamobs;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.argoseven.kastriamobs.entity.*;

public class RegistryKastriaEntity {

    private static <T extends net.minecraft.entity.Entity> EntityType<T> registerEntity(String name, EntityType.EntityFactory<T> factory, EntityDimensions entityDimensions) {
        return Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(KastriaMobs.MOD_ID, name),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, factory)
                        .dimensions(entityDimensions)
                        .build()
        );
    }
    public static final EntityType<Blindwrath> BLINDWRATH = registerEntity("blindwrath", Blindwrath::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Hollowseer> HOLLOWSEER = registerEntity("hollowseer", Hollowseer::new, EntityDimensions.fixed(1.2f,2.0f));
    public static final EntityType<PlagueBrute> PLAGUEBRUTE = registerEntity("plaguebrute", PlagueBrute::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<RedBloodMage> RED_BLOOD_MAGE = registerEntity("red_blood_mage", RedBloodMage::new, EntityDimensions.fixed(0.6f,2.5f));
    public static final EntityType<Reaver> REAVER = registerEntity("reaver", Reaver::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Stalker> STALKER = registerEntity("stalker", Stalker::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Bastion> BASTION = registerEntity("bastion", Bastion::new, EntityDimensions.fixed(1.2f,3.0f));
    public static final EntityType<Bard> BARD = registerEntity("bard", Bard::new, EntityDimensions.fixed(0.6f,1.8f));
    public static final EntityType<Tobias> TOBIAS = registerEntity("tobias", Tobias::new, EntityDimensions.fixed(0.6f,1.8f));


    public static void registerEntityAttributes(){
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.PLAGUEBRUTE, PlagueBrute.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.RED_BLOOD_MAGE, RedBloodMage.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.BLINDWRATH, Blindwrath.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.HOLLOWSEER, Hollowseer.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.REAVER, Reaver.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.STALKER, Stalker.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.BASTION, Bastion.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.BARD, Bard.setAttribute());
        FabricDefaultAttributeRegistry.register(RegistryKastriaEntity.TOBIAS, Tobias.setAttribute());
    }


    private static <T extends net.minecraft.entity.Entity> EntityType<T> playerReg(String name, EntityType.EntityFactory<T> factory, EntityDimensions entityDimensions) {
        EntityType<T> entiy = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(KastriaMobs.MOD_ID, name),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, factory)
                        .dimensions(entityDimensions)
                        .build()
        );

        return  entiy;
    }
}
