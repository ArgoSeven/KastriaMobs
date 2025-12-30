package org.argoseven.kastriamobs.client.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.argoseven.kastriamobs.entity.Tobias;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class TobiasRender extends ExtendedGeoEntityRenderer<Tobias> {
    
    private static final String RIGHT_ARM_BONE = "right_arm";
    private static final String LEFT_ARM_BONE = "left_arm";
    private static final float DEFAULT_SHADOW_RADIUS = 0.5F;
    private static final float ITEM_Y_OFFSET = -0.6F;
    private static final float SHIELD_Y_OFFSET = 0.125F;
    private static final float SHIELD_Z_OFFSET = -0.25F;
    
    private ItemStack mainHandItem;
    private ItemStack offHandItem;

    public TobiasRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KastriaEntityModel<>("tobias", "bard"));
        this.shadowRadius = DEFAULT_SHADOW_RADIUS;
    }

    @Override
    public RenderLayer getRenderType(Tobias animatable, float partialTick, MatrixStack poseStack, 
                                      VertexConsumerProvider bufferSource, VertexConsumer buffer, 
                                      int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }

    @Override
    public void renderEarly(Tobias animatable, MatrixStack poseStack, float partialTick, 
                           VertexConsumerProvider bufferSource, VertexConsumer buffer, 
                           int packedLight, int packedOverlay, 
                           float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, 
                packedLight, packedOverlay, red, green, blue, partialTicks);
        this.mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
        this.offHandItem = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Override
    protected Identifier getTextureForBone(String boneName, Tobias entity) {
        return null;
    }

    @Override
    protected ItemStack getHeldItemForBone(String boneName, Tobias entity) {
        return switch (boneName) {
            case LEFT_ARM_BONE -> entity.isLeftHanded() ? this.mainHandItem : this.offHandItem;
            case RIGHT_ARM_BONE -> entity.isLeftHanded() ? this.offHandItem : this.mainHandItem;
            default -> null;
        };
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack itemStack, String boneName) {
        return switch (boneName) {
            case LEFT_ARM_BONE, RIGHT_ARM_BONE -> ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            default -> ModelTransformation.Mode.NONE;
        };
    }

    @Override
    protected BlockState getHeldBlockForBone(String boneName, Tobias entity) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack matrixStack, ItemStack itemStack, String boneName, 
                                 Tobias entity, IBone bone) {
        matrixStack.translate(0.0, ITEM_Y_OFFSET, 0.0);
        
        if (itemStack == this.mainHandItem || itemStack == this.offHandItem) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            
            if (itemStack.getItem() instanceof ShieldItem) {
                if (itemStack == this.mainHandItem) {
                    matrixStack.translate(0.0, SHIELD_Y_OFFSET, SHIELD_Z_OFFSET);
                } else {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                }
            }
        }
    }

    @Override
    protected void preRenderBlock(MatrixStack matrixStack, BlockState blockState, String boneName, Tobias entity) {
    }

    @Override
    protected void postRenderItem(MatrixStack matrixStack, ItemStack itemStack, String boneName, 
                                  Tobias entity, IBone bone) {
    }

    @Override
    protected void postRenderBlock(MatrixStack matrixStack, BlockState blockState, String boneName, Tobias entity) {
    }
}
