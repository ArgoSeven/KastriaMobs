package org.argoseven.kastriamobs.client.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.argoseven.kastriamobs.entity.AbyssalCountess;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AbyssalCountessRender extends KastriaEntityRenderer<AbyssalCountess> {

    public AbyssalCountessRender(EntityRendererFactory.Context renderManager, AnimatedGeoModel<AbyssalCountess> model) {
        super(renderManager, model);
    }

    @Override
    public void render(AbyssalCountess animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {

        /*
        if (animatable.isFury()){
            Vec3d[] vec3ds = new Vec3d[3];
            Random r = animatable.getRandom();
            vec3ds[0] = new Vec3d(0.0, 0.0, 0.0);
            vec3ds[1] = new Vec3d(r.nextBetween(-1, 1), 1, r.nextBetween(-1, 1));;
            vec3ds[2] = new Vec3d(r.nextBetween(-1, 1), 1, r.nextBetween(-1, 1));;

            int h = 0;
            for(int j = 0; j < vec3ds.length; ++j) {
                poseStack.push();
                poseStack.translate(vec3ds[j].x + (double)MathHelper.cos((float)j + h * 0.5F) * 0.025, vec3ds[j].y + (double)MathHelper.cos((float)j + h * 0.75F) * 0.0125, vec3ds[j].z + (double)MathHelper.cos((float)j + h * 0.7F) * 0.025);
                super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                poseStack.pop();
            }
        }*/
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }

}
