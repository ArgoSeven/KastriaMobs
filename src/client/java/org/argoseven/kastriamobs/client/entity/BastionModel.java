
package org.argoseven.kastriamobs.client.entity;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.Bastion;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BastionModel extends AnimatedGeoModel<Bastion> {
    @Override
    public Identifier getModelResource(Bastion bastion) {
        return new Identifier(KastriaMobs.MOD_ID,"geo/bastion.geo.json");
    }

    @Override
    public Identifier getTextureResource(Bastion bastion) {
        return new Identifier(KastriaMobs.MOD_ID,"textures/entity/bastion.png");
    }

    @Override
    public Identifier getAnimationResource(Bastion bastion) {
        return new Identifier(KastriaMobs.MOD_ID,"animations/bastion_anim.json");
    }
}
