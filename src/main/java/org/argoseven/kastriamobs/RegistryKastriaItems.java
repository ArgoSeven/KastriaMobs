package org.argoseven.kastriamobs;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryKastriaItems {

    public static final Item BLINDWRATH_SPAWN_EGG = registerSpawnEgg("blindwrath_spawn_egg", RegistryKastriaEntity.BLINDWRATH, 0xC5D6D5, 0x062E37);
    public static final Item HOLLOWSEER_SPAWN_EGG = registerSpawnEgg("hollowseer_spawn_egg", RegistryKastriaEntity.HOLLOWSEER, 0x6BA0B8, 0xAB2154);
    public static final Item PLAGUEBRUTE_SPAWN_EGG = registerSpawnEgg("plaguebrute_spawn_egg", RegistryKastriaEntity.PLAGUEBRUTE, 0xD1D6B6, 0x56F3EB);
    public static final Item RED_BLOOD_MAGE_SPAWN_EGG = registerSpawnEgg("red_blood_mage_spawn_egg", RegistryKastriaEntity.RED_BLOOD_MAGE, 0x5C1B26, 0xF5DA2A);
    public static final Item REAVER_SPAWN_EGG = registerSpawnEgg("reaver_spawn_egg", RegistryKastriaEntity.REAVER, 0x3594C0, 0x716AC7);
    public static final Item STALKER_SPAWN_EGG = registerSpawnEgg("stalker_spawn_egg", RegistryKastriaEntity.STALKER, 0xD1D6B6, 0x0D1217);
    public static final Item BASTION_SPAWN_EGG = registerSpawnEgg("bastion_spawn_egg", RegistryKastriaEntity.BASTION, 0x245269, 0x9B1D5A);
    public static final Item BARD_SPAWN_EGG = registerSpawnEgg("bard_spawn_egg", RegistryKastriaEntity.BARD, 0x940505, 0xDD9E07);
    public static final Item TOBIAS_SPAWN_EGG = registerSpawnEgg("tobias_spawn_egg", RegistryKastriaEntity.TOBIAS, 0xDEDEDE, 0x6F5B75);


    private static Item registerSpawnEgg(String name, net.minecraft.entity.EntityType<?> entityType, int primaryColor, int secondaryColor) {
        return Registry.register(
                Registry.ITEM,
                new Identifier(KastriaMobs.MOD_ID, name),
                new SpawnEggItem((EntityType<? extends MobEntity>) entityType, primaryColor, secondaryColor, new FabricItemSettings().group(ItemGroup.MISC))
        );
    }

    public static void registerItem() {
    }
}
