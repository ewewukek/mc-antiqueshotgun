package ewewukek.antiqueshotgun;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AntiqueShotgunMod.MODID)
public class AntiqueShotgunMod {
    public static final String MODID = "antiqueshotgun";

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "antique_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "antique_barrel"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "handmade_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "handmade_barrel"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "handmade_stock"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "sawd_off_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "buckshot_shell"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "slug_shell"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "handmade_shell"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "rubber"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "rubber_shell")
            );
        }
    }
}
