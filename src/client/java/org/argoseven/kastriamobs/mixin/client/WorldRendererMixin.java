package org.argoseven.kastriamobs.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.argoseven.kastriamobs.client.debug.DebugShapeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    
    /**
     * Injects after entity rendering to add our custom debug shapes.
     * This only renders when F3+B hitbox rendering is enabled.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderEnd(MatrixStack matrices, float tickDelta, long limitTime, 
                             boolean renderBlockOutline, Camera camera, 
                             GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                             net.minecraft.util.math.Matrix4f positionMatrix, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Only render debug shapes when hitboxes are enabled (F3+B)
        if (!client.getEntityRenderDispatcher().shouldRenderHitboxes()) {
            return;
        }
        
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertices = immediate.getBuffer(RenderLayer.getLines());
        
        DebugShapeRenderer.render(
                matrices, 
                vertices, 
                camera.getPos().x, 
                camera.getPos().y, 
                camera.getPos().z
        );
    }
}
