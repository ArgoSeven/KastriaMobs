
package org.argoseven.kastriamobs.client.entity;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Blindwrath;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BlindwrathModel extends AnimatedGeoModel<Blindwrath> {
    @Override
    public Identifier getModelResource(Blindwrath blindwrath) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/blindwrath.geo.json");
    }

    @Override
    public Identifier getTextureResource(Blindwrath blindwrath) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/blindwrath.png");
    }

    @Override
    public Identifier getAnimationResource(Blindwrath blindwrath) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/blindwrath_anim.json");
    }
}
