package com.lucab.animal_wellness.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lucab.animal_wellness.AnimalWellness;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class WellnessConfig {
    public static class Config {
        public static class Info {
            public boolean type = true;
            public boolean age = true;
            public boolean feed = true;
            public boolean sickness = true;
        }

        public static class Age {
            public int maxAge = 20 * 60 * 60 * 24;
            public float babyAgeThreshold = 0.2f;
            public float adultAgeThreshold = 0.8f;
        }

        public static class Feed {
            public int maxFeed = 6000;
        }

        public static class Sickness {
            public boolean enabled = true;
            public float sicknessRate = 0.001f;
            public float sicknessThreshold = 1.0f;
            public int hurtTickRate = 100;
        }

        public Info info = new Info();
        public Age age = new Age();
        public Feed feed = new Feed();
        public Sickness sickness = new Sickness();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("animal_wellness_server.json");
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
