
package org.argoseven.kastriamobs.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import org.argoseven.kastriamobs.entity.Hollowseer;

public class HollowseerRender extends GeoEntityRenderer<Hollowseer> {

    public HollowseerRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HollowseerModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderLayer getRenderType(Hollowseer animatable, float partialTick, MatrixStack poseStack,
                                     VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                     int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }
}
