
package org.argoseven.kastriamobs.client.entity;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Stalker;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StalkerModel extends AnimatedGeoModel<Stalker> {
    @Override
    public Identifier getModelResource(Stalker stalker) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/stalker.geo.json");
    }

    @Override
    public Identifier getTextureResource(Stalker stalker) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/stalker.png");
    }

    @Override
    public Identifier getAnimationResource(Stalker stalker) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/stalker_anim.json");
    }
}
