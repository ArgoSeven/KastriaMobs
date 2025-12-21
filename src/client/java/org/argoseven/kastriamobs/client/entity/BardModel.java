package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Bard;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;


public class BardModel extends AnimatedGeoModel<Bard> {
    @Override
    public Identifier getModelResource(Bard bard) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/bard.geo.json");
    }

    @Override
    public Identifier getTextureResource(Bard bard) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/bard.png");
    }

    @Override
    public Identifier getAnimationResource(Bard bard) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/bard_anim.json");
    }

    @Override
    public void setCustomAnimations(Bard animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
    }
}
