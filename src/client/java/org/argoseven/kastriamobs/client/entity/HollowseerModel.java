
package org.argoseven.kastriamobs.client.entity;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Hollowseer;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HollowseerModel extends AnimatedGeoModel<Hollowseer> {
    @Override
    public Identifier getModelResource(Hollowseer hollowseer) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/hollowseer.geo.json");
    }

    @Override
    public Identifier getTextureResource(Hollowseer hollowseer) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/hollowseer.png");
    }

    @Override
    public Identifier getAnimationResource(Hollowseer hollowseer) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/hollowseer_anim.json");
    }
}
