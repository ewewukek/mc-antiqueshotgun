package ewewukek.antiqueshotgun;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import ewewukek.antiqueshotgun.client.ClientSetup;
import ewewukek.antiqueshotgun.entity.BulletEntity;
import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import ewewukek.antiqueshotgun.item.AmmoItem;
import ewewukek.antiqueshotgun.item.AntiqueShotgunItem;
import ewewukek.antiqueshotgun.item.BuckshotAmmoItem;
import ewewukek.antiqueshotgun.item.HandmadeAmmoItem;
import ewewukek.antiqueshotgun.item.HandmadeShotgunItem;
import ewewukek.antiqueshotgun.item.RubberAmmoItem;
import ewewukek.antiqueshotgun.item.SawdoffShotgunItem;
import ewewukek.antiqueshotgun.item.ShotgunItem;
import ewewukek.antiqueshotgun.item.SlugAmmoItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
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

    @ObjectHolder(AntiqueShotgunMod.MODID + ":antique_shotgun")
    public static Item ANTIQUE_SHOTGUN;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":handmade_shotgun")
    public static Item HANDMADE_SHOTGUN;
    @ObjectHolder(AntiqueShotgunMod.MODID + ":sawd_off_shotgun")
    public static Item SAWD_OFF_SHOTGUN;

    @ObjectHolder(MODID + ":handmade_shell")
    public static AmmoItem HANDMADE_SHELL;
    @ObjectHolder(MODID + ":buckshot_shell")
    public static AmmoItem BUCKSHOT_SHELL;
    @ObjectHolder(MODID + ":slug_shell")
    public static AmmoItem SLUG_SHELL;
    @ObjectHolder(MODID + ":rubber_shell")
    public static AmmoItem RUBBER_SHELL;

    @ObjectHolder(MODID + ":shotgun_fire")
    public static SoundEvent SOUND_SHOTGUN_FIRE;
    @ObjectHolder(MODID + ":shotgun_pump_back")
    public static SoundEvent SOUND_SHOTGUN_PUMP_BACK;
    @ObjectHolder(MODID + ":shotgun_pump_forward")
    public static SoundEvent SOUND_SHOTGUN_PUMP_FORWARD;
    @ObjectHolder(MODID + ":shotgun_inserting_shell")
    public static SoundEvent SOUND_SHOTGUN_INSERTING_SHELL;

    @ObjectHolder(MODID + ":bullet")
    public static EntityType<BulletEntity> BULLET_ENTITY_TYPE;

    @ObjectHolder(MODID + ":elder_hunter")
    public static EntityType<ElderHunterEntity> ELDER_HUNTER_ENTITY_TYPE;

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
                new SawdoffShotgunItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "sawd_off_shotgun"),
                new BuckshotAmmoItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "buckshot_shell"),
                new SlugAmmoItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "slug_shell"),
                new HandmadeAmmoItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "handmade_shell"),
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MODID, "rubber"),
                new RubberAmmoItem(new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName(MODID, "rubber_shell")
            );
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().registerAll(
                EntityType.Builder.<BulletEntity>create(EntityClassification.MISC)
                    .setCustomClientFactory(BulletEntity::new).size(0.25f, 0.25f)
                    .setTrackingRange(64).setUpdateInterval(5).setShouldReceiveVelocityUpdates(false)
                    .build(MODID + ":bullet").setRegistryName(MODID, "bullet"),

                EntityType.Builder.<ElderHunterEntity>create(ElderHunterEntity::new, EntityClassification.MONSTER)
                    .setTrackingRange(8).setUpdateInterval(3)
                    .size(0.6f, 1.95f).setShouldReceiveVelocityUpdates(true)
                    .build(MODID + ":elder_hunter").setRegistryName(MODID, "elder_hunter")
            );
        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(final EntityAttributeCreationEvent event) {
            event.put(ELDER_HUNTER_ENTITY_TYPE, ElderHunterEntity.createEntityAttributes());
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
            if (event.phase == TickEvent.Phase.START) {
                ItemStack stack = event.player.getHeldItem(Hand.MAIN_HAND);
                if (stack.getItem() instanceof ShotgunItem) {
                    ((ShotgunItem)stack.getItem()).update(event.player, stack);
                } else {
                    ItemStack offhandStack = event.player.getHeldItem(Hand.OFF_HAND);
                    if (offhandStack.getItem() instanceof ShotgunItem) {
                        ShotgunItem shotgun = (ShotgunItem)offhandStack.getItem();
                        if (shotgun.canBeUsedFromOffhand(event.player)) {
                            shotgun.update(event.player, offhandStack);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onWorldTick(final TickEvent.WorldTickEvent event) {
            if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
                DamageQueue.apply();
            }
        }

        @SubscribeEvent
        public static void onAddReloadListenerEvent(final AddReloadListenerEvent event) {
            event.addListener(new IFutureReloadListener() {
                @Override
                public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager,
                    IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
                    Executor gameExecutor) {

                    return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
                        Config.reload();
                    }, gameExecutor);
                }
            });
        }

        private static long lastPatrolSpawnTime = 0;

        @SubscribeEvent
        public static void onEntityJoin(final EntityJoinWorldEvent event) {
            World world = event.getWorld();
            Entity entity = event.getEntity();

            if (entity.getType() == EntityType.VILLAGER) {
                VillagerEntity villager = (VillagerEntity)entity;
                villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, ElderHunterEntity.class, 16, 0.7, 0.7));
            }

            if (entity.getType() == EntityType.EVOKER) {
                EvokerEntity evoker = (EvokerEntity)entity;
                if (evoker.isRaidActive() && world.rand.nextFloat() < ElderHunterEntity.raidSpawnChance) {
                    Raid raid = evoker.getRaid();
                    int wave = evoker.getWave();
                    BlockPos pos = evoker.getPosition().add(0, -1, 0); // compensate +1 from joinRaid
                    raid.leaveRaid(evoker, true);

                    ElderHunterEntity hunter = ELDER_HUNTER_ENTITY_TYPE.create(world);
                    raid.joinRaid(wave, hunter, pos, false);

                    event.setCanceled(true);
                }
            }

            if (entity.getType() == EntityType.PILLAGER) {
                PillagerEntity pillager = (PillagerEntity)entity;
                if (pillager.isLeader() && lastPatrolSpawnTime != world.getGameTime()) {
                    lastPatrolSpawnTime = world.getGameTime();
                    if (world.rand.nextFloat() < ElderHunterEntity.patrolSpawnChance) {
                        ElderHunterEntity hunter = ELDER_HUNTER_ENTITY_TYPE.create(world);
                        hunter.setPosition(pillager.getPosition().getX(), pillager.getPosition().getY(), pillager.getPosition().getZ());
                        hunter.onInitialSpawn((IServerWorld)world, world.getDifficultyForLocation(pillager.getPosition()), SpawnReason.PATROL, null, null);
                        hunter.setOnGround(true);
                        world.addEntity(hunter);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static class ReloadKeyChangedPacket {
        private boolean isDown;

        public ReloadKeyChangedPacket(boolean isDown) {
            this.isDown = isDown;
        }

        ReloadKeyChangedPacket(PacketBuffer buf) {
            isDown = buf.readByte() != 0;
        }

        void encode(PacketBuffer buf) {
            buf.writeByte(isDown ? 1 : 0);
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = ctx.get().getSender();
                KeyState.setReloadKeyDown(player, isDown);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
