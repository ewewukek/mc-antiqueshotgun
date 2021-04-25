package ewewukek.antiqueshotgun.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElderHunterRenderer extends MobRenderer<ElderHunterEntity, ElderHunterModel<ElderHunterEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(AntiqueShotgunMod.MODID + ":textures/entity/elder_hunter.png");

    public ElderHunterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ElderHunterModel<>(), 0.5f);
        addLayer(new HeadLayer<>(this));
        addLayer(new HeldItemLayer<ElderHunterEntity, ElderHunterModel<ElderHunterEntity>>(this) {
            @Override
            public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ElderHunterEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (!entitylivingbaseIn.isAggressive()) return;
                super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        });
    }

    @Override
    public ResourceLocation getEntityTexture(ElderHunterEntity entity) {
        return TEXTURE;
    }
}
