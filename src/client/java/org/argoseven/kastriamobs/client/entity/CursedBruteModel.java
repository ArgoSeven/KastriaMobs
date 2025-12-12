package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.CursedBrute;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;


public class CursedBruteModel extends AnimatedGeoModel<CursedBrute> {
    @Override
    public Identifier getModelResource(CursedBrute eldenFap) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/cursedbrute.geo.json");
    }

    @Override
    public Identifier getTextureResource(CursedBrute cursedbrute) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/cursedbrute.png");
    }

    @Override
    public Identifier getAnimationResource(CursedBrute eldenFap) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/cursedbrute_anim.json");
    }

    @Override
    public void setCustomAnimations(CursedBrute animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX((float) (extraData.headPitch * 0.017453292519943295));
            head.setRotationY((float) (extraData.netHeadYaw * 0.017453292519943295));
        }
    }
}
