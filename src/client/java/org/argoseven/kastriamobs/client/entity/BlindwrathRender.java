
package org.argoseven.kastriamobs.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import org.argoseven.kastriamobs.entity.Blindwrath;

public class BlindwrathRender extends GeoEntityRenderer<Blindwrath> {

    public BlindwrathRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BlindwrathModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderLayer getRenderType(Blindwrath animatable, float partialTick, MatrixStack poseStack,
                                     VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }
}
