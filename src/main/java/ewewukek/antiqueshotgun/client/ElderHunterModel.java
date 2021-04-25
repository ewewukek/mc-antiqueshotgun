package ewewukek.antiqueshotgun.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElderHunterModel<T extends ElderHunterEntity> extends EntityModel<T> implements IHasArm, IHasHead {
    private final ModelRenderer head;
    private final ModelRenderer nose;
    private final ModelRenderer body;
    private final ModelRenderer arms_folded;
    private final ModelRenderer right_arm;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_leg;
    private final ModelRenderer left_leg;

    public ElderHunterModel() {
        textureWidth = 128;
        textureHeight = 128;

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.setTextureOffset(0, 25).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(22, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(18, 14).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 1.0F, 10.0F, 0.0F, false);
        head.setTextureOffset(28, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.1F, false);
        head.setTextureOffset(0, 43).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.2F, false);

        nose = new ModelRenderer(this);
        nose.setRotationPoint(0.0F, -2.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.setTextureOffset(32, 25).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F, false);

        arms_folded = new ModelRenderer(this);
        arms_folded.setRotationPoint(0.0F, 2.0F, 0.0F);
        arms_folded.rotateAngleX = -0.9163F;
        arms_folded.setTextureOffset(48, 14).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
        arms_folded.setTextureOffset(48, 59).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        ModelRenderer mirrored = new ModelRenderer(this);
        mirrored.setRotationPoint(0.0F, 22.0F, 0.0F);
        arms_folded.addChild(mirrored);
        mirrored.setTextureOffset(32, 59).addBox(4.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        right_arm = new ModelRenderer(this);
        right_arm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        right_arm.setTextureOffset(32, 43).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_arm = new ModelRenderer(this);
        left_arm.setRotationPoint(5.0F, 2.0F, 0.0F);
        left_arm.setTextureOffset(48, 43).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        right_leg = new ModelRenderer(this);
        right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        right_leg.setTextureOffset(0, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_leg = new ModelRenderer(this);
        left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
        left_leg.setTextureOffset(16, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float deg2rad = (float)(Math.PI / 180);
        head.rotateAngleY = netHeadYaw * deg2rad;
        head.rotateAngleX = headPitch * deg2rad;

        if (isSitting) {
            right_arm.rotateAngleX = -36 * deg2rad;
            right_arm.rotateAngleY = 0;
            right_arm.rotateAngleZ = 0;
            left_arm.rotateAngleX = -36 * deg2rad;
            left_arm.rotateAngleY = 0;
            left_arm.rotateAngleZ = 0;
            right_leg.rotateAngleX = -81 * deg2rad;
            right_leg.rotateAngleY = 18 * deg2rad;
            right_leg.rotateAngleZ = 4.5f * deg2rad;
            left_leg.rotateAngleX = -81 * deg2rad;
            left_leg.rotateAngleY = -18 * deg2rad;
            left_leg.rotateAngleZ = -4.5f * deg2rad;
        } else {
            float swingMult = MathHelper.cos(limbSwing * 0.6662f);
            right_arm.rotateAngleX = -swingMult * limbSwingAmount;
            right_arm.rotateAngleY = 0;
            right_arm.rotateAngleZ = 0;
            left_arm.rotateAngleX = swingMult * limbSwingAmount;
            left_arm.rotateAngleY = 0;
            left_arm.rotateAngleZ = 0;
            right_leg.rotateAngleX = swingMult * 0.7f * limbSwingAmount;
            right_leg.rotateAngleY = 0;
            right_leg.rotateAngleZ = 0;
            left_leg.rotateAngleX = -swingMult * 0.7f * limbSwingAmount;
            left_leg.rotateAngleY = 0;
            left_leg.rotateAngleZ = 0;
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        nose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        arms_folded.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        right_arm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        left_arm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        right_leg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        left_leg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        ModelRenderer model = sideIn == HandSide.RIGHT ? right_arm : left_arm;
        model.translateRotate(matrixStackIn);
    }

    @Override
    public ModelRenderer getModelHead() {
        return head;
    }
}
