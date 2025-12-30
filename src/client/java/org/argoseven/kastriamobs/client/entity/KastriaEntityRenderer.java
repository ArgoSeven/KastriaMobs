package org.argoseven.kastriamobs.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.entity.AbstractKastriaEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KastriaEntityRenderer<T extends AbstractKastriaEntity> extends GeoEntityRenderer<T> {
    
    private static final float DEFAULT_SHADOW_RADIUS = 0.5F;

    public KastriaEntityRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = DEFAULT_SHADOW_RADIUS;
    }

    public KastriaEntityRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> model, float shadowRadius) {
        super(renderManager, model);
        this.shadowRadius = shadowRadius;
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTick, MatrixStack poseStack,
                                     VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }
}
