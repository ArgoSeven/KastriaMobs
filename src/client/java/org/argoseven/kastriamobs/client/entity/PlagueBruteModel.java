package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.PlagueBrute;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;


public class PlagueBruteModel extends AnimatedGeoModel<PlagueBrute> {
    @Override
    public Identifier getModelResource(PlagueBrute plagueBrute ) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/plaguebrute.geo.json");
    }

    @Override
    public Identifier getTextureResource(PlagueBrute plagueBrute) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/plaguebrute.png");
    }

    @Override
    public Identifier getAnimationResource(PlagueBrute plagueBrute) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/plaguebrute_anim.json");
    }

    @Override
    public void setCustomAnimations(PlagueBrute animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX((float) (extraData.headPitch * 0.017453292519943295));
            head.setRotationY((float) (extraData.netHeadYaw * 0.017453292519943295));
        }
    }
}
