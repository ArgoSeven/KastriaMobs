package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.RedBloodMage;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;


public class RedBloodMageModel extends AnimatedGeoModel<RedBloodMage> {
    @Override
    public Identifier getModelResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/stalker.geo.json");
    }

    @Override
    public Identifier getTextureResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/stalker.png");
    }

    @Override
    public Identifier getAnimationResource(RedBloodMage redbloodmage) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/stalker_anim.json");
    }

}
