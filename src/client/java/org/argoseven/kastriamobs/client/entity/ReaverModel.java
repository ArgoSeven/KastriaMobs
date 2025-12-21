
package org.argoseven.kastriamobs.client.entity;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Reaver;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReaverModel extends AnimatedGeoModel<Reaver> {
    @Override
    public Identifier getModelResource(Reaver reaver) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/reaver.geo.json");
    }

    @Override
    public Identifier getTextureResource(Reaver reaver) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/reaver.png");
    }

    @Override
    public Identifier getAnimationResource(Reaver reaver) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/reaver_anim.json");
    }
}
