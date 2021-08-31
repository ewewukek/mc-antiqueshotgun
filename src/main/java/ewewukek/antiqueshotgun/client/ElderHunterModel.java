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
        texWidth = 128;
        texHeight = 128;

        head = new ModelRenderer(this);
        head.setPos(0.0F, 0.0F, 0.0F);
        head.texOffs(0, 25).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        head.texOffs(22, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        head.texOffs(18, 14).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 1.0F, 10.0F, 0.0F, false);
        head.texOffs(28, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.1F, false);
        head.texOffs(0, 43).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.2F, false);

        nose = new ModelRenderer(this);
        nose.setPos(0.0F, -2.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        body.texOffs(32, 25).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, false);
        body.texOffs(0, 0).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F, false);

        arms_folded = new ModelRenderer(this);
        arms_folded.setPos(0.0F, 2.0F, 0.0F);
        arms_folded.xRot = -0.9163F;
        arms_folded.texOffs(48, 14).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
        arms_folded.texOffs(48, 59).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        ModelRenderer mirrored = new ModelRenderer(this);
        mirrored.setPos(0.0F, 22.0F, 0.0F);
        arms_folded.addChild(mirrored);
        mirrored.texOffs(32, 59).addBox(4.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        right_arm = new ModelRenderer(this);
        right_arm.setPos(-5.0F, 2.0F, 0.0F);
        right_arm.texOffs(32, 43).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_arm = new ModelRenderer(this);
        left_arm.setPos(5.0F, 2.0F, 0.0F);
        left_arm.texOffs(48, 43).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        right_leg = new ModelRenderer(this);
        right_leg.setPos(-2.0F, 12.0F, 0.0F);
        right_leg.texOffs(0, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_leg = new ModelRenderer(this);
        left_leg.setPos(2.0F, 12.0F, 0.0F);
        left_leg.texOffs(16, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float deg2rad = (float)(Math.PI / 180);
        head.xRot = netHeadYaw * deg2rad;
        head.xRot = headPitch * deg2rad;

        if (riding) {
            right_arm.xRot = -36 * deg2rad;
            right_arm.yRot = 0;
            right_arm.zRot = 0;
            left_arm.xRot = -36 * deg2rad;
            left_arm.yRot = 0;
            left_arm.zRot = 0;
            right_leg.xRot = -81 * deg2rad;
            right_leg.yRot = 18 * deg2rad;
            right_leg.zRot = 4.5f * deg2rad;
            left_leg.xRot = -81 * deg2rad;
            left_leg.yRot = -18 * deg2rad;
            left_leg.zRot = -4.5f * deg2rad;
        } else {
            float swingMult = MathHelper.cos(limbSwing * 0.6662f);
            right_arm.xRot = -swingMult * limbSwingAmount;
            right_arm.yRot = 0;
            right_arm.zRot = 0;
            left_arm.xRot = swingMult * limbSwingAmount;
            left_arm.yRot = 0;
            left_arm.zRot = 0;
            right_leg.xRot = swingMult * 0.7f * limbSwingAmount;
            right_leg.yRot = 0;
            right_leg.zRot = 0;
            left_leg.xRot = -swingMult * 0.7f * limbSwingAmount;
            left_leg.yRot = 0;
            left_leg.zRot = 0;
        }

        if (entityIn.isAggressive()) {
            right_arm.visible = true;
            left_arm.visible = true;
            arms_folded.visible = false;
            right_arm.xRot = head.xRot - 1.5f;
            right_arm.yRot = head.yRot;
            left_arm.xRot = head.xRot - 90 * deg2rad + 0.1f;
            left_arm.yRot = head.yRot + 0.9f;
        } else {
            right_arm.visible = true;
            left_arm.visible = true;
            arms_folded.visible = true;
            right_arm.visible = left_arm.visible = false;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
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
    public void translateToHand(HandSide sideIn, MatrixStack matrixStackIn) {
        ModelRenderer model = sideIn == HandSide.RIGHT ? right_arm : left_arm;
        model.translateAndRotate(matrixStackIn);
    }

    @Override
    public ModelRenderer getHead() {
        return head;
    }
}
