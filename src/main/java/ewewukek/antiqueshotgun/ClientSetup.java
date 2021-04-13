package ewewukek.antiqueshotgun;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
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
