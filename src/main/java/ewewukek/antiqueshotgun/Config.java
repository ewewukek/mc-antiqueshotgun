package ewewukek.antiqueshotgun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ewewukek.antiqueshotgun.enchantment.BruteEnchantment;
import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import ewewukek.antiqueshotgun.item.AntiqueShotgunItem;
import ewewukek.antiqueshotgun.item.BuckshotAmmoItem;
import ewewukek.antiqueshotgun.item.HandmadeAmmoItem;
import ewewukek.antiqueshotgun.item.HandmadeShotgunItem;
import ewewukek.antiqueshotgun.item.RubberAmmoItem;
import ewewukek.antiqueshotgun.item.SawdoffShotgunItem;
import ewewukek.antiqueshotgun.item.ShotgunItem;
import ewewukek.antiqueshotgun.item.SlugAmmoItem;
import net.minecraftforge.fml.loading.FMLPaths;

public class Config {
    private static final Logger LOGGER = LogManager.getLogger(AntiqueShotgunMod.class);
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("antiqueshotgun.json");
    private static final int VERSION = 1;

    private static Config instance;

    public static void reload() {
        load();

        ShotgunItem.enableMagazine = instance.common.enableMagazine;

        ReloadAction.reloadFull = instance.client.reloadFull;
        ReloadAction.insertOneIfEmpty = instance.client.insertOneIfEmpty;

        AntiqueShotgunItem.magazineCapacity = instance.antique_shotgun.magazineCapacity;
        AntiqueShotgunItem.reloadDuration = instance.antique_shotgun.reloadDuration;
        AntiqueShotgunItem.shellInsertDuration = instance.antique_shotgun.shellInsertDuration;

        HandmadeShotgunItem.magazineCapacity = instance.handmade_shotgun.magazineCapacity;
        HandmadeShotgunItem.reloadDuration = instance.handmade_shotgun.reloadDuration;
        HandmadeShotgunItem.shellInsertDuration = instance.handmade_shotgun.shellInsertDuration;
        HandmadeShotgunItem.misfireChance = instance.handmade_shotgun.misfireChance;
        HandmadeShotgunItem.spreadStdDevAdd = (float)Math.toRadians(instance.handmade_shotgun.spreadStdDevAdd);
        HandmadeShotgunItem.damageMultiplier = instance.handmade_shotgun.damageMultiplier;

        SawdoffShotgunItem.magazineCapacity = instance.sawd_off_shotgun.magazineCapacity;
        SawdoffShotgunItem.spreadStdDevAdd = (float)Math.toRadians(instance.sawd_off_shotgun.spreadStdDevAdd);
        SawdoffShotgunItem.damageMultiplier = instance.sawd_off_shotgun.damageMultiplier;

        HandmadeAmmoItem.pelletCount = instance.handmade_shell.pelletCount;
        HandmadeAmmoItem.spreadStdDev = (float)Math.toRadians(instance.handmade_shell.spreadStdDev);
        HandmadeAmmoItem.speed = instance.handmade_shell.speed / 20;
        HandmadeAmmoItem.range = instance.handmade_shell.range;
        HandmadeAmmoItem.damage = instance.handmade_shell.damage;
        HandmadeAmmoItem.durabilityDamage = instance.handmade_shell.durabilityDamage;
        HandmadeAmmoItem.misfireChance = instance.handmade_shell.misfireChance;
        HandmadeAmmoItem.jamChance = instance.handmade_shell.jamChance;

        BuckshotAmmoItem.pelletCount = instance.buckshot_shell.pelletCount;
        BuckshotAmmoItem.spreadStdDev = (float)Math.toRadians(instance.buckshot_shell.spreadStdDev);
        BuckshotAmmoItem.speed = instance.buckshot_shell.speed / 20;
        BuckshotAmmoItem.range = instance.buckshot_shell.range;
        BuckshotAmmoItem.damage = instance.buckshot_shell.damage;
        BuckshotAmmoItem.durabilityDamage = instance.buckshot_shell.durabilityDamage;

        SlugAmmoItem.spreadStdDev = (float)Math.toRadians(instance.slug_shell.spreadStdDev);
        SlugAmmoItem.speed = instance.slug_shell.speed / 20;
        SlugAmmoItem.range = instance.slug_shell.range;
        SlugAmmoItem.damage = instance.slug_shell.damage;
        SlugAmmoItem.durabilityDamage = instance.slug_shell.durabilityDamage;
        SlugAmmoItem.postFireDelay = instance.slug_shell.postFireDelay;

        RubberAmmoItem.spreadStdDev = (float)Math.toRadians(instance.rubber_shell.spreadStdDev);
        RubberAmmoItem.speed = instance.rubber_shell.speed / 20;
        RubberAmmoItem.range = instance.rubber_shell.range;
        RubberAmmoItem.damage = instance.rubber_shell.damage;
        RubberAmmoItem.durabilityDamage = instance.rubber_shell.durabilityDamage;
        RubberAmmoItem.knockbackForce = instance.rubber_shell.knockbackForce;
        RubberAmmoItem.slownessDuration = instance.rubber_shell.slownessDuration;
        RubberAmmoItem.slownessLevel = instance.rubber_shell.slownessLevel;
        RubberAmmoItem.weaknessDuration = instance.rubber_shell.weaknessDuration;
        RubberAmmoItem.nauseaDuration = instance.rubber_shell.nauseaDuration;

        BruteEnchantment.extraDamageBase = instance.brute_enchantment.extraDamageBase;
        BruteEnchantment.extraDamagePerLevel = instance.brute_enchantment.extraDamagePerLevel;
        BruteEnchantment.knockbackForcePerLevel = instance.brute_enchantment.knockbackForcePerLevel;

        ElderHunterEntity.magazineCapacity = instance.elder_hunter.magazineCapacity;
        ElderHunterEntity.aimDuration = instance.elder_hunter.aimDuration;
        ElderHunterEntity.reloadDuration = instance.elder_hunter.reloadDuration;
        ElderHunterEntity.shellInsertDuration = instance.elder_hunter.shellInsertDuration;
        ElderHunterEntity.shotgunDropChance = instance.elder_hunter.shotgunDropChance;
        ElderHunterEntity.raidSpawnChance = instance.elder_hunter.raidSpawnChance;
        ElderHunterEntity.patrolSpawnChance = instance.elder_hunter.patrolSpawnChance;

        System.out.println("reload complete");
    }

    private static void load() {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
            instance = new Gson().fromJson(reader, Config.class);
        } catch (NoSuchFileException e) {
            instance = new Config();
            save();
            LOGGER.info("Configuration file not found, default created");
        } catch (IOException e) {
            LOGGER.warn("Could not read configuration file: ", e);
        }
        if (instance.version < VERSION) {
            LOGGER.info("Configuration file belongs to an older version, updating");
            save();
        }
    }

    private static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(instance, writer);
        } catch (IOException e) {
            LOGGER.warn("Could not save configuration file: ", e);
        }
    }

    private int version = VERSION;

    private Common common = new Common();

    private Client client = new Client();

    private AntiqueShotgun antique_shotgun = new AntiqueShotgun();
    private HandmadeShotgun handmade_shotgun = new HandmadeShotgun();
    private SawdoffShotgun sawd_off_shotgun = new SawdoffShotgun();

    private HandmadeShell handmade_shell = new HandmadeShell();
    private BuckshotShell buckshot_shell = new BuckshotShell();
    private SlugShell slug_shell = new SlugShell();
    private RubberShell rubber_shell = new RubberShell();

    private BruteEnchantmentConfig brute_enchantment = new BruteEnchantmentConfig();

    private ElderHunter elder_hunter = new ElderHunter();

    private static class Common {
        public boolean enableMagazine = true;
    }

    private static class Client {
        public boolean reloadFull = true;
        public boolean insertOneIfEmpty = true;
    }

    private static class AntiqueShotgun {
        public int magazineCapacity = 7;
        public int reloadDuration = 12;
        public int shellInsertDuration = 10;
    }

    private static class HandmadeShotgun {
        public int magazineCapacity = 4;
        public int reloadDuration = 16;
        public int shellInsertDuration = 14;
        public float misfireChance = 0.025f;
        public float spreadStdDevAdd = 0.5f;
        public float damageMultiplier = 0.9f;
    }

    private static class SawdoffShotgun {
        public int magazineCapacity = 1;
        public float spreadStdDevAdd = 1;
        public float damageMultiplier = 0.8f;
    }

    private static class HandmadeShell {
        public int pelletCount = 9;
        public float spreadStdDev = 2;
        public float speed = 400;
        public float range = 15;
        public float damage = 20;
        public int durabilityDamage = 3;
        public float misfireChance = 0.025f;
        public float jamChance = 0.005f;
    }

    private static class BuckshotShell {
        public int pelletCount = 9;
        public float spreadStdDev = 1.5f;
        public float speed = 400;
        public float range = 15;
        public float damage = 25;
        public int durabilityDamage = 1;
    }

    private static class SlugShell {
        public float spreadStdDev = 0;
        public float speed = 400;
        public float range = 25;
        public float damage = 25;
        public int durabilityDamage = 1;
        public int postFireDelay = 3;
    }

    public static class RubberShell {
        public float spreadStdDev = 0.5f;
        public float speed = 200;
        public float range = 20;
        public float damage = 1;
        public int durabilityDamage = 2;
        public float knockbackForce = 0.6f;
        public float slownessDuration = 10;
        public int slownessLevel = 1;
        public float weaknessDuration = 10;
        public float nauseaDuration = 5;
    }

    public static class BruteEnchantmentConfig {
        public float extraDamageBase = 1;
        public float extraDamagePerLevel = 1;
        public float knockbackForcePerLevel = 0.5f;
    }

    public static class ElderHunter {
        public int magazineCapacity = 3;
        public int aimDuration = 20;
        public int reloadDuration = 14;
        public int shellInsertDuration = 12;
        public float shotgunDropChance = 0.001f;
        public float raidSpawnChance = 0.1f;
        public float patrolSpawnChance = 0.05f;
    }
}
