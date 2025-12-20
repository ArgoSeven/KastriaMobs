package org.argoseven.kastriamobs.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.argoseven.kastriamobs.entity.PlagueBrute;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;


public class PlagueRender extends GeoEntityRenderer<PlagueBrute> {

    public PlagueRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PlagueBruteModel());
        this.shadowRadius = 0.5F;
    }

    public RenderLayer getRenderType(PlagueBrute animatable, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }
}
