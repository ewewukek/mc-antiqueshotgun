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

        ShotgunItem.insertOneIfEmpty = instance.common.insertOneIfEmpty;

        AntiqueShotgunItem.magazineCapacity = instance.antique_shotgun.magazineCapacity;
        AntiqueShotgunItem.reloadDuration = instance.antique_shotgun.reloadDuration;
        AntiqueShotgunItem.shellInsertDuration = instance.antique_shotgun.shellInsertDuration;

        HandmadeShotgunItem.magazineCapacity = instance.handmade_shotgun.magazineCapacity;
        HandmadeShotgunItem.reloadDuration = instance.handmade_shotgun.reloadDuration;
        HandmadeShotgunItem.shellInsertDuration = instance.handmade_shotgun.shellInsertDuration;

        SawdoffShotgunItem.magazineCapacity = instance.sawd_off_shotgun.magazineCapacity;

        HandmadeAmmoItem.pelletCount = instance.handmade_shell.pelletCount;
        HandmadeAmmoItem.spreadStdDev = (float)Math.toRadians(instance.handmade_shell.spreadStdDev);
        HandmadeAmmoItem.speed = instance.handmade_shell.speed / 20;
        HandmadeAmmoItem.range = instance.handmade_shell.range;
        HandmadeAmmoItem.damage = instance.handmade_shell.damage;

        BuckshotAmmoItem.pelletCount = instance.buckshot_shell.pelletCount;
        BuckshotAmmoItem.spreadStdDev = (float)Math.toRadians(instance.buckshot_shell.spreadStdDev);
        BuckshotAmmoItem.speed = instance.buckshot_shell.speed / 20;
        BuckshotAmmoItem.range = instance.buckshot_shell.range;
        BuckshotAmmoItem.damage = instance.buckshot_shell.damage;

        SlugAmmoItem.spreadStdDev = (float)Math.toRadians(instance.slug_shell.spreadStdDev);
        SlugAmmoItem.speed = instance.slug_shell.speed / 20;
        SlugAmmoItem.range = instance.slug_shell.range;
        SlugAmmoItem.damage = instance.slug_shell.damage;

        RubberAmmoItem.spreadStdDev = (float)Math.toRadians(instance.rubber_shell.spreadStdDev);
        RubberAmmoItem.speed = instance.rubber_shell.speed / 20;
        RubberAmmoItem.range = instance.rubber_shell.range;
        RubberAmmoItem.damage = instance.rubber_shell.damage;

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

    private AntiqueShotgun antique_shotgun = new AntiqueShotgun();
    private HandmadeShotgun handmade_shotgun = new HandmadeShotgun();
    private SawdoffShotgun sawd_off_shotgun = new SawdoffShotgun();

    private HandmadeShell handmade_shell = new HandmadeShell();
    private BuckshotShell buckshot_shell = new BuckshotShell();
    private SlugShell slug_shell = new SlugShell();
    private RubberShell rubber_shell = new RubberShell();

    private ElderHunter elder_hunter = new ElderHunter();

    private static class Common {
        public boolean insertOneIfEmpty = false;
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
    }

    private static class SawdoffShotgun {
        public int magazineCapacity = 1;
    }

    private static class HandmadeShell {
        public int pelletCount = 9;
        public float spreadStdDev = 2;
        public float speed = 400;
        public float range = 15;
        public float damage = 20;
    }

    private static class BuckshotShell {
        public int pelletCount = 9;
        public float spreadStdDev = 1.5f;
        public float speed = 400;
        public float range = 15;
        public float damage = 25;
    }

    private static class SlugShell {
        public float spreadStdDev = 0;
        public float speed = 400;
        public float range = 25;
        public float damage = 25;
    }

    public static class RubberShell {
        public float spreadStdDev = 0.5f;
        public float speed = 200;
        public float range = 20;
        public float damage = 1;
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
