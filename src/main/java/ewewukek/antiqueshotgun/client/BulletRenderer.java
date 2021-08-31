package ewewukek.antiqueshotgun.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.entity.BulletEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BulletRenderer extends EntityRenderer<BulletEntity> {
    public static final ResourceLocation PELLET_TEXTURE = new ResourceLocation(AntiqueShotgunMod.MODID + ":textures/entity/pellet.png");
    public static final ResourceLocation SLUG_TEXTURE = new ResourceLocation(AntiqueShotgunMod.MODID + ":textures/entity/slug.png");
    public static final ResourceLocation RUBBER_BULLET_TEXTURE = new ResourceLocation(AntiqueShotgunMod.MODID + ":textures/entity/rubber_bullet.png");

    public BulletRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity bullet) {
        switch (bullet.ammoType) {
        case SLUG:
            return SLUG_TEXTURE;
        case RUBBER:
            return RUBBER_BULLET_TEXTURE;
        default:
            return PELLET_TEXTURE;
        }
    }

    @Override
    public void render(BulletEntity bullet, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer render, int packedLight) {
        matrixStack.pushPose();

        matrixStack.scale(0.05f, 0.05f, 0.05f);

        // billboarding
        matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));

        MatrixStack.Entry entry = matrixStack.last();
        Matrix4f positionMatrix = entry.pose();
        Matrix3f normalMatrix = entry.normal();

        IVertexBuilder builder = render.getBuffer(RenderType.entityCutout(getTextureLocation(bullet)));
        addVertex(builder, positionMatrix, normalMatrix, -1, -1, 0, 0, 1, 0, 0, 1, packedLight);
        addVertex(builder, positionMatrix, normalMatrix,  1, -1, 0, 1, 1, 0, 0, 1, packedLight);
        addVertex(builder, positionMatrix, normalMatrix,  1,  1, 0, 1, 0, 0, 0, 1, packedLight);
        addVertex(builder, positionMatrix, normalMatrix, -1,  1, 0, 0, 0, 0, 0, 1, packedLight);

        matrixStack.popPose();
    }

    private static void addVertex(IVertexBuilder builder, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, float z, float u, float v, float nx, float ny, float nz, int packedLight) {
        builder.vertex(positionMatrix, x, y, z)
               .color(255, 255, 255, 255)
               .uv(u, v)
               .overlayCoords(OverlayTexture.NO_OVERLAY)
               .uv2(packedLight)
               .normal(normalMatrix, nx, ny, nz)
               .endVertex();
    }
}
