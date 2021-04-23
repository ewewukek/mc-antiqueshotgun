package ewewukek.antiqueshotgun.client;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class ElderHunterRenderer<T extends ElderHunterEntity> extends MobRenderer<T, ElderHunterModel<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(AntiqueShotgunMod.MODID + ":textures/entity/elder_hunter.png");

    public ElderHunterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ElderHunterModel<>(), 0.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TEXTURE;
    }
}
