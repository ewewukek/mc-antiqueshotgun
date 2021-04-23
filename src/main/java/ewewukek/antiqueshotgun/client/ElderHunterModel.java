package ewewukek.antiqueshotgun.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ElderHunterModel<T extends ElderHunterEntity> extends EntityModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer nose;
    private final ModelRenderer body;
    private final ModelRenderer arms;
    private final ModelRenderer mirrored;
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

        arms = new ModelRenderer(this);
        arms.setRotationPoint(0.0F, 2.0F, 0.0F);
        setRotationAngle(arms, -0.9163F, 0.0F, 0.0F);
        arms.setTextureOffset(48, 14).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
        arms.setTextureOffset(48, 59).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        mirrored = new ModelRenderer(this);
        mirrored.setRotationPoint(0.0F, 22.0F, 0.0F);
        arms.addChild(mirrored);
        mirrored.setTextureOffset(32, 59).addBox(4.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        right_leg = new ModelRenderer(this);
        right_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
        right_leg.setTextureOffset(16, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_leg = new ModelRenderer(this);
        left_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        left_leg.setTextureOffset(0, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
        float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
        float red, float green, float blue, float alpha) {
        head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        nose.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        arms.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        right_leg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        left_leg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
