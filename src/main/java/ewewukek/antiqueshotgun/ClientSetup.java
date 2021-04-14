package ewewukek.antiqueshotgun;

import net.minecraft.client.Minecraft;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientSetup {
    // TODO: find a way to use registerGlobalProperty
    @ObjectHolder(AntiqueShotgunMod.MODID + ":handmade_shell")
    public static Item HANDMADE_SHELL;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":buckshot_shell")
    public static Item BUCKSHOT_SHELL;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":slug_shell")
    public static Item SLUG_SHELL;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":rubber_shell")
    public static Item RUBBER_SHELL;

    @ObjectHolder(AntiqueShotgunMod.MODID + ":antique_shotgun")
    public static Item ANTIQUE_SHOTGUN;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":handmade_shotgun")
    public static Item HANDMADE_SHOTGUN;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":sawd_off_shotgun")
    public static Item SAWD_OFF_SHOTGUN;

    public static void init(final FMLClientSetupEvent event) {
        IItemPropertyGetter countGetter = (stack, world, player) -> {
            return stack.getCount();
        };
        ItemModelsProperties.registerProperty(HANDMADE_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.registerProperty(BUCKSHOT_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.registerProperty(SLUG_SHELL, new ResourceLocation("count"), countGetter);
        ItemModelsProperties.registerProperty(RUBBER_SHELL, new ResourceLocation("count"), countGetter);
        IItemPropertyGetter slideBackGetter = (stack, world, player) -> {
            return ((ShotgunItem)stack.getItem()).isSlideBack(stack) ? 1 : 0;
        };
        ItemModelsProperties.registerProperty(ANTIQUE_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);
        ItemModelsProperties.registerProperty(HANDMADE_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);
        ItemModelsProperties.registerProperty(SAWD_OFF_SHOTGUN, new ResourceLocation(AntiqueShotgunMod.MODID, "slide_back"), slideBackGetter);
    }

    @SubscribeEvent
    public static void onRenderHandEvent(final RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof ShotgunItem) {
            Minecraft mc = Minecraft.getInstance();
            FirstPersonRenderHelper.renderFirstPersonShotgun(
                mc.getFirstPersonRenderer(), mc.player,
                event.getHand(), event.getPartialTicks(), event.getInterpolatedPitch(),
                event.getSwingProgress(), event.getEquipProgress(), stack,
                event.getMatrixStack(), event.getBuffers(), event.getLight());
            event.setCanceled(true);
        }
    }
}
