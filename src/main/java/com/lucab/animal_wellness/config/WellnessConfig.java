package com.lucab.animal_wellness.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lucab.animal_wellness.AnimalWellness;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class WellnessConfig {
    public static class Config {
        public static class EntityList {
            public boolean whitelist = true;
            public List<String> entities = List.of("minecraft:cow", "minecraft:pig", "minecraft:sheep", "minecraft:chicken");
        }

        public static class Affinity {
            public float affinityRate = 0.1f;
            public int dropTime = 6000;
        }

        public static class Age {
            public int maxAge = 20 * 60 * 60 * 24;
            public float babyAgeThreshold = 0.04f;
            public float adultAgeThreshold = 0.8f;
        }

        public static class Feed {
            public int maxFeed = 12000;
            public int maxWater = 12000;
            public int searchRange = 10;
            public int eatTime = 100;
        }

        public static class Manure {
            public boolean enabled = true;
            public int manureTimeMin = 4800;
            public int manureTimeMax = 6000;
        }

        public static class Brush {
            public int brushTime = 24000;
        }

        public static class Breeding {
            public boolean enabled = true;
            public int pregnantTime = 18000;
            public int breedingTime = 6000;
            public int searchRange = 10;
            public int loveTick = 200;
            public float affinityThreshold = 0.4f;
        }

        public static class Drop {
            public boolean affinityDrop = true;
            public float affinityThreshold = 0.3f;
            public int maxDrop = 10;
        }

        public static class Genetics {
            public boolean enabled = true;
            public float mutationAmount = 0.005f;
        }

        public static class Egg {
            public int eggTime = 18000;
            public int searchRange = 10;
            public int layTime = 200;
        }

        public static class Milk {
            public int milkTime = 18000;
        }

        public EntityList entityList = new EntityList();
        public Affinity affinity = new Affinity();
        public Age age = new Age();
        public Feed feed = new Feed();
        public Manure manure = new Manure();
        public Brush brush = new Brush();
        public Breeding breeding = new Breeding();
        public Drop drop = new Drop();
        public Genetics genetics = new Genetics();
        public Egg egg = new Egg();
        public Milk milk = new Milk();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("animal_wellness-server.json");
    public static Config config = new Config();

    public static void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            config = GSON.fromJson(reader, Config.class);
        } catch (IOException e) {
            AnimalWellness.LOGGER.error("[Animal Wellness] Failed to load config");
        }

        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            AnimalWellness.LOGGER.error("[Animal Wellness] Failed to save config");
        }
    }
}
