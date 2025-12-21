package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.RedBloodMage;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class RedBloodMageModel extends AnimatedGeoModel<RedBloodMage> {
    @Override
    public Identifier getModelResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/red_blood_mage.geo.json");
    }

    @Override
    public Identifier getTextureResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/red_blood_mage.png");
    }

    @Override
    public Identifier getAnimationResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/red_blood_mage_anim.json");
    }

}
