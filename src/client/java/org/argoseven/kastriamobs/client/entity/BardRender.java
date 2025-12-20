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
import org.argoseven.kastriamobs.entity.Bard;
import org.argoseven.kastriamobs.entity.CursedBrute;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;


public class BardRender extends ExtendedGeoEntityRenderer<Bard> {
    protected ItemStack mainHandItem;
    protected ItemStack offHandItem;
    protected ItemStack helmetItem;
    protected ItemStack chestplateItem;
    protected ItemStack leggingsItem;
    protected ItemStack bootsItem;
    static private final String rightArm = "right_arm";
    static private final String leftArm = "left_arm";
    static private final String leftleg = "left_leg";
    static private final String rightLeg = "right_leg";

    public BardRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BardModel());
        this.shadowRadius = 0.5F;
    }

    public RenderLayer getRenderType(Bard animatable, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }

    @Override
    public void renderEarly(Bard animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
        this.mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
        this.offHandItem = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
        this.helmetItem = animatable.getEquippedStack(EquipmentSlot.HEAD);
        this.chestplateItem = animatable.getEquippedStack(EquipmentSlot.CHEST);
        this.leggingsItem = animatable.getEquippedStack(EquipmentSlot.LEGS);
        this.bootsItem = animatable.getEquippedStack(EquipmentSlot.FEET);
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Override
    protected Identifier getTextureForBone(String s, Bard bard) {
        return null;
    }

    @Override
    protected ItemStack getHeldItemForBone(String s, Bard bard) {
        ItemStack var10000;
        switch (s) {
            case leftArm -> var10000 = bard.isLeftHanded() ? this.mainHandItem : this.offHandItem;
            case rightArm -> var10000 = bard.isLeftHanded() ? this.offHandItem : this.mainHandItem;
            default -> var10000 = null;
        }

        return var10000;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack itemStack, String s) {
        ModelTransformation.Mode var10000;
        switch (s) {
            case leftArm:
            case rightArm:
                var10000 = ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
                break;
            default:
                var10000 = ModelTransformation.Mode.NONE;
        }

        return var10000;
    }

    @Override
    protected BlockState getHeldBlockForBone(String s, Bard bard) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack matrixStack, ItemStack itemStack, String s, Bard bard, IBone iBone) {
        matrixStack.translate((double)0.0F, (double) -0.6F, (double)0.0F);
        if (itemStack == this.mainHandItem) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            if (itemStack.getItem() instanceof ShieldItem) {
                matrixStack.translate((double)0.0F, (double)0.125F, (double)-0.25F);
            }
        } else if (itemStack == this.offHandItem) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            if (itemStack.getItem() instanceof ShieldItem) {
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            }
        }
    }

    @Override
    protected void preRenderBlock(MatrixStack matrixStack, BlockState blockState, String s, Bard bard) {

    }

    @Override
    protected void postRenderItem(MatrixStack matrixStack, ItemStack itemStack, String s, Bard bard, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(MatrixStack matrixStack, BlockState blockState, String s, Bard bard) {

    }
}
