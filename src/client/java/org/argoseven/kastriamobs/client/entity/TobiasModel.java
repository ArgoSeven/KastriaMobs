package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Bard;
import org.argoseven.kastriamobs.entity.Tobias;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class TobiasModel extends AnimatedGeoModel<Tobias> {
    @Override
    public Identifier getModelResource(Tobias tobias) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/tobias.geo.json");
    }

    @Override
    public Identifier getTextureResource(Tobias tobias) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/tobias.png");
    }

    @Override
    public Identifier getAnimationResource(Tobias tobias) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/bard_anim.json");
    }

    @Override
    public void setCustomAnimations(Tobias animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
    }
}
