package ewewukek.antiqueshotgun;

import java.util.function.Supplier;

import ewewukek.antiqueshotgun.client.ClientSetup;
import ewewukek.antiqueshotgun.enchantment.BruteEnchantment;
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
import ewewukek.antiqueshotgun.item.ThermiteAmmoItem;
import ewewukek.antiqueshotgun.item.WitherAmmoItem;
import ewewukek.antiqueshotgun.item.crafting.SawdoffShotgunRecipe;
import ewewukek.antiqueshotgun.item.crafting.UnloadShotgunRecipe;
import ewewukek.antiqueshotgun.item.crafting.conditions.CraftingEnabled;
import ewewukek.antiqueshotgun.world.TreasureLootModifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
    @ObjectHolder(MODID + ":thermite_shell")
    public static AmmoItem THERMITE_SHELL;
    @ObjectHolder(MODID + ":wither_shell")
    public static AmmoItem WITHER_SHELL;

    @ObjectHolder(MODID + ":shotgun_fire")
    public static SoundEvent SOUND_SHOTGUN_FIRE;
    @ObjectHolder(MODID + ":shotgun_fire_slug")
    public static SoundEvent SOUND_SHOTGUN_FIRE_SLUG;
    @ObjectHolder(MODID + ":shotgun_dry_fire")
    public static SoundEvent SOUND_SHOTGUN_DRY_FIRE;
    @ObjectHolder(MODID + ":shotgun_pump_back")
    public static SoundEvent SOUND_SHOTGUN_PUMP_BACK;
    @ObjectHolder(MODID + ":shotgun_pump_forward")
    public static SoundEvent SOUND_SHOTGUN_PUMP_FORWARD;
    @ObjectHolder(MODID + ":shotgun_pump_jammed")
    public static SoundEvent SOUND_SHOTGUN_PUMP_JAMMED;
    @ObjectHolder(MODID + ":shotgun_inserting_shell")
    public static SoundEvent SOUND_SHOTGUN_INSERTING_SHELL;
    @ObjectHolder(MODID + ":brute_hit")
    public static SoundEvent SOUND_BRUTE_HIT;

    @ObjectHolder(MODID + ":bullet")
    public static EntityType<BulletEntity> BULLET_ENTITY_TYPE;

    @ObjectHolder(MODID + ":elder_hunter")
    public static EntityType<ElderHunterEntity> ELDER_HUNTER_ENTITY_TYPE;

    public static final EnchantmentType ENCHANTMENT_TYPE_SHOTGUN = EnchantmentType.create(MODID + ":shotgun", (item) -> {
        return item instanceof ShotgunItem && ((ShotgunItem)item).getEnchantmentValue() != 0;
    });

    @ObjectHolder(MODID + ":brute")
    public static Enchantment BRUTE_ENCHANTMENT;

    public AntiqueShotgunMod() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        });
        CraftingHelper.register(CraftingEnabled.SERIALIZER);
        NETWORK_CHANNEL.registerMessage(1, ReloadStatePacket.class, ReloadStatePacket::encode, ReloadStatePacket::new, ReloadStatePacket::handle);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                new AntiqueShotgunItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "antique_shotgun"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "antique_barrel"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "antique_gears"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "antique_stock"),
                new HandmadeShotgunItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "handmade_shotgun"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "handmade_barrel"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "handmade_stock"),
                new SawdoffShotgunItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "sawd_off_shotgun"),
                new BuckshotAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "buckshot_shell"),
                new SlugAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "slug_shell"),
                new HandmadeAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "handmade_shell"),
                new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(MODID, "rubber"),
                new RubberAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "rubber_shell"),
                new ThermiteAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "thermite_shell"),
                new WitherAmmoItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT)).setRegistryName(MODID, "wither_shell")
            );
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().registerAll(
                EntityType.Builder.<BulletEntity>createNothing(EntityClassification.MISC)
                    .setCustomClientFactory(BulletEntity::new).sized(0.25f, 0.25f)
                    .setTrackingRange(64).setUpdateInterval(5).setShouldReceiveVelocityUpdates(false)
                    .build(MODID + ":bullet").setRegistryName(MODID, "bullet"),

                EntityType.Builder.<ElderHunterEntity>of(ElderHunterEntity::new, EntityClassification.MONSTER)
                    .setTrackingRange(8).setUpdateInterval(3)
                    .sized(0.6f, 1.95f).setShouldReceiveVelocityUpdates(true)
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
                new SoundEvent(new ResourceLocation(MODID, "shotgun_fire_slug")).setRegistryName(MODID, "shotgun_fire_slug"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_dry_fire")).setRegistryName(MODID, "shotgun_dry_fire"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_pump_back")).setRegistryName(MODID, "shotgun_pump_back"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_pump_forward")).setRegistryName(MODID, "shotgun_pump_forward"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_pump_jammed")).setRegistryName(MODID, "shotgun_pump_jammed"),
                new SoundEvent(new ResourceLocation(MODID, "shotgun_inserting_shell")).setRegistryName(MODID, "shotgun_inserting_shell"),
                new SoundEvent(new ResourceLocation(MODID, "brute_hit")).setRegistryName(MODID, "brute_hit")
            );
        }

        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            event.getRegistry().registerAll(
                SawdoffShotgunRecipe.SERIALIZER.setRegistryName(MODID, "sawd_off_shotgun_recipe"),
                UnloadShotgunRecipe.SERIALIZER.setRegistryName(MODID, "unload_shotgun_recipe")
            );
        }

        @SubscribeEvent
        public static void onLootModifierRegistry(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(
                TreasureLootModifier.SERIALIZER.setRegistryName(MODID, "treasure_loot_modifier")
            );
        }

        @SubscribeEvent
        public static void onEnchantmentRegistry(final RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().register(
                new BruteEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND).setRegistryName(MODID, "brute")
            );
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandlers {
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                ReloadAction.activeStack = null;
                ItemStack stack = event.player.getItemInHand(Hand.MAIN_HAND);
                if (stack.getItem() instanceof ShotgunItem) {
                    ReloadAction.activeStack = stack;
                    ((ShotgunItem)stack.getItem()).update(event.player, stack);
                } else {
                    ItemStack offhandStack = event.player.getItemInHand(Hand.OFF_HAND);
                    if (offhandStack.getItem() instanceof ShotgunItem) {
                        ShotgunItem shotgun = (ShotgunItem)offhandStack.getItem();
                        if (shotgun.canBeUsedFromOffhand(event.player)) {
                            ReloadAction.activeStack = offhandStack;
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
            Config.reload();
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
                if (evoker.hasActiveRaid() && world.random.nextFloat() < ElderHunterEntity.raidSpawnChance) {
                    Raid raid = evoker.getCurrentRaid();
                    int wave = evoker.getWave();
                    BlockPos pos = evoker.blockPosition().offset(0, -1, 0); // compensate +1 from joinRaid
                    raid.removeFromRaid(evoker, true);

                    ElderHunterEntity hunter = ELDER_HUNTER_ENTITY_TYPE.create(world);
                    raid.joinRaid(wave, hunter, pos, false);

                    event.setCanceled(true);
                }
            }

            if (entity.getType() == EntityType.PILLAGER) {
                PillagerEntity pillager = (PillagerEntity)entity;
                if (pillager.isPatrolLeader() && lastPatrolSpawnTime != world.getGameTime()) {
                    lastPatrolSpawnTime = world.getGameTime();
                    if (world.random.nextFloat() < ElderHunterEntity.patrolSpawnChance) {
                        ElderHunterEntity hunter = ELDER_HUNTER_ENTITY_TYPE.create(world);
                        hunter.setPos(pillager.blockPosition().getX(), pillager.blockPosition().getY(), pillager.blockPosition().getZ());
                        hunter.finalizeSpawn((IServerWorld)world, world.getCurrentDifficultyAt(pillager.blockPosition()), SpawnReason.PATROL, null, null);
                        hunter.setOnGround(true);
                        world.addFreshEntity(hunter);
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAttackEntityEvent(final AttackEntityEvent event) {
            PlayerEntity player = event.getPlayer();
            Entity target = event.getTarget();
            if (target.isAttackable() && !target.skipAttackInteraction(player)) {
                float knockback = BruteEnchantment.knockbackForcePerLevel * EnchantmentHelper.getEnchantmentLevel(BRUTE_ENCHANTMENT, player);
                if (knockback > 0) {
                    float angle = player.yRot * (float)(Math.PI / 180);
                    if (target instanceof LivingEntity) {
                        ((LivingEntity)target).knockback(knockback, MathHelper.sin(angle), -MathHelper.cos(angle));
                    } else {
                        target.push(-MathHelper.sin(angle) * knockback, 0.1, MathHelper.cos(angle) * knockback);
                    }
                    player.setSprinting(false);
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), AntiqueShotgunMod.SOUND_BRUTE_HIT, SoundCategory.PLAYERS, 1, 1);
                }
            }
        }

        @SubscribeEvent
        public static void onAnvilUpdateEvent(final AnvilUpdateEvent event) {
            ItemStack leftStack = event.getLeft();
            ItemStack rightStack = event.getRight();
            if (leftStack.getItem() == ANTIQUE_SHOTGUN && leftStack.getDamageValue() > 0 && leftStack.getItem().isValidRepairItem(leftStack, rightStack)) {
                ItemStack output = leftStack.copy();
                output.setDamageValue(0);
                event.setOutput(output);
                event.setCost(1 + (int)Math.ceil(4 * leftStack.getDamageValue() / leftStack.getMaxDamage()));
                event.setMaterialCost(1);
            }
        }
    }

    public static class ReloadStatePacket {
        private boolean isReloading;

        public ReloadStatePacket(boolean isReloading) {
            this.isReloading = isReloading;
        }

        ReloadStatePacket(PacketBuffer buf) {
            isReloading = buf.readByte() != 0;
        }

        void encode(PacketBuffer buf) {
            buf.writeByte(isReloading ? 1 : 0);
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = ctx.get().getSender();
                ReloadAction.setIsReloading(player, isReloading);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
