package org.argoseven.kastriamobs.client.entity;

import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.AbstractKastriaEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KastriaEntityModel<T extends AbstractKastriaEntity> extends AnimatedGeoModel<T> {
    
    private final Identifier modelResource;
    private final Identifier textureResource;
    private final Identifier animationResource;

    public KastriaEntityModel(String entityName) {
        this(entityName, entityName);
    }

    public KastriaEntityModel(String entityName, String animationName) {
        this.modelResource = new Identifier(KastriaMobs.MOD_ID, "geo/" + entityName + ".geo.json");
        this.textureResource = new Identifier(KastriaMobs.MOD_ID, "textures/entity/" + entityName + ".png");
        this.animationResource = new Identifier(KastriaMobs.MOD_ID, "animations/" + animationName + "_anim.json");
    }

    @Override
    public Identifier getModelResource(T entity) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(T entity) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        return animationResource;
    }
}
