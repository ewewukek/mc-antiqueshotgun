package ewewukek.antiqueshotgun;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

@Mod(AntiqueShotgunMod.MODID)
public class AntiqueShotgunMod {
    public static final String MODID = "antiqueshotgun";
    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    @ObjectHolder(MODID + ":handmade_shell")
    public static Item HANDMADE_SHELL;
    @ObjectHolder(MODID + ":buckshot_shell")
    public static Item BUCKSHOT_SHELL;
    @ObjectHolder(MODID + ":slug_shell")
    public static Item SLUG_SHELL;
    @ObjectHolder(MODID + ":rubber_shell")
    public static Item RUBBER_SHELL;

    @ObjectHolder(MODID + ":shotgun_fire")
    public static SoundEvent SOUND_SHOTGUN_FIRE;
    @ObjectHolder(MODID + ":shotgun_pump_back")
    public static SoundEvent SOUND_SHOTGUN_PUMP_BACK;
    @ObjectHolder(MODID + ":shotgun_pump_forward")
    public static SoundEvent SOUND_SHOTGUN_PUMP_FORWARD;
    @ObjectHolder(MODID + ":shotgun_inserting_shell")
    public static SoundEvent SOUND_SHOTGUN_INSERTING_SHELL;

    public AntiqueShotgunMod() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        });
        NETWORK_CHANNEL.registerMessage(1, ReloadKeyChangedPacket.class, ReloadKeyChangedPacket::encode, ReloadKeyChangedPacket::new, ReloadKeyChangedPacket::handle);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                new AntiqueShotgunItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "antique_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "antique_barrel"),
                new HandmadeShotgunItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "handmade_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "handmade_barrel"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "handmade_stock"),
                new ShotgunItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "sawd_off_shotgun"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "buckshot_shell"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "slug_shell"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "handmade_shell"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "rubber"),
                new Item(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "rubber_shell")
            );
        }

        @SubscribeEvent
        public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().registerAll(
                new SoundEvent(new ResourceLocation(MODID, "shotgun_fire")).setRegistryName(MODID, "shotgun_fire"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_pump_back")).setRegistryName(MODID, "shotgun_pump_back"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_pump_forward")).setRegistryName(MODID, "shotgun_pump_forward"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_inserting_shell")).setRegistryName(MODID, "shotgun_inserting_shell")
            );
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandlers {
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            ItemStack stack = event.player.getHeldItem(Hand.MAIN_HAND);
            if (stack.getItem() instanceof ShotgunItem) {
                ((ShotgunItem)stack.getItem()).update(event.player, stack);
            }
        }
    }

    public static class ReloadKeyChangedPacket {
        private boolean isDown;

        ReloadKeyChangedPacket(boolean isDown) {
            this.isDown = isDown;
        }

        ReloadKeyChangedPacket(PacketBuffer buf) {
            isDown = buf.readByte() != 0;
        }

        void encode(PacketBuffer buf) {
            buf.writeByte(isDown ? 1 : 0);
        }

        void handle(Supplier<NetworkEvent.Context> context) {
            if (isDown) {
                System.out.println("key pressed");
            } else {
                System.out.println("key released");
            }
        }
    }
}
