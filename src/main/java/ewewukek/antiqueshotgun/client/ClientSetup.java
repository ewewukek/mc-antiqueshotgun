package ewewukek.antiqueshotgun.client;

import org.lwjgl.glfw.GLFW;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.ReloadAction;
import ewewukek.antiqueshotgun.item.ShotgunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientSetup {
    private static final KeyBinding reloadKey = new KeyBinding("key.antiqueshotgun.reload", GLFW.GLFW_KEY_R, "key.antiqueshotgun.category");

    public static void init(final FMLClientSetupEvent event) {
        // TODO: find a way to use registerGlobalProperty
        IItemPropertyGetter countGetter = (stack, world, player) -> {
            return stack.getCount();
        };
        ItemModelsProperties.register(AntiqueShotgunMod.HANDMADE_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.BUCKSHOT_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.SLUG_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.RUBBER_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.THERMITE_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.WITHER_SHELL, new ResourceLocation("count"), countGetter);

        IItemPropertyGetter slideBackGetter = (stack, world, player) -> {
            return ShotgunItem.isSlideBack(stack) ? 1 : 0;
        };
        ItemModelsProperties.register(AntiqueShotgunMod.ANTIQUE_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.HANDMADE_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);
        ItemModelsProperties.register(AntiqueShotgunMod.SAWD_OFF_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);

        ItemModelsProperties.register(AntiqueShotgunMod.ANTIQUE_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "durability_negative"), (stack, world, player) -> {
            return stack.getDamageValue() - stack.getMaxDamage();
        });

        RenderingRegistry.registerEntityRenderingHandler(AntiqueShotgunMod.BULLET_ENTITY_TYPE, BulletRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AntiqueShotgunMod.ELDER_HUNTER_ENTITY_TYPE, ElderHunterRenderer::new);

        ClientRegistry.registerKeyBinding(reloadKey);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderHandEvent(final RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof ShotgunItem) {
            Minecraft mc = Minecraft.getInstance();
            ShotgunItem shotgun = (ShotgunItem)stack.getItem();
            if ((event.getHand() == Hand.MAIN_HAND || shotgun.canBeUsedFromOffhand(mc.player)) && !shotgun.almostBroken(stack)) {
                FirstPersonRenderHelper.renderFirstPersonShotgun(
                    mc.getItemInHandRenderer(), mc.player,
                    event.getHand(), event.getPartialTicks(), event.getInterpolatedPitch(),
                    event.getSwingProgress(), event.getEquipProgress(), stack,
                    event.getMatrixStack(), event.getBuffers(), event.getLight());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTickEvent(final ClientTickEvent event) {
        ReloadAction.clientTick(reloadKey.isDown());
    }
}
