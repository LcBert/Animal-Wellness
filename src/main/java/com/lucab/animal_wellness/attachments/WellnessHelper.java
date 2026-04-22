package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.UUID;

public class WellnessHelper {
    private Level level;
    private Entity entity;
    private WellnessAttachment wellness;

    public WellnessHelper(Level level, Entity entity, WellnessAttachment wellness) {
        this.level = level;
        this.entity = entity;
        this.wellness = wellness;
    }

    public static WellnessHelper getInstance(Entity entity) {
        WellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        return new WellnessHelper(entity.level(), entity, wellness);
    }

    public boolean isConsideredAnimal() {
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        return (WellnessConfig.config.entityList.entities.contains(entityId) == WellnessConfig.config.entityList.whitelist);
    }

    // Tracker
    public void setTracked() {
        wellness.tracked = true;
    }

    public boolean isTracked() {
        return wellness.tracked;
    }

    // Affinity
    public void setAffinity(float amount) {
        wellness.affinity = Math.clamp(amount, 0.0f, 1.0f);
    }

    public void incrementAffinity() {
        WellnessConfig.Config config = WellnessConfig.config;
        setAffinity(wellness.affinity + config.affinity.affinityRate);
    }

    public void decrementAffinity() {
        WellnessConfig.Config config = WellnessConfig.config;
        setAffinity(wellness.affinity - config.affinity.affinityRate);
    }

    public float getAffinity() {
        return wellness.affinity;
    }

    // Age
    public void setBirth() {
        wellness.birthTime = level.getGameTime();
    }

    public long getBirthElapsed() {
        return level.getGameTime() - wellness.birthTime;
    }

    public boolean isBaby() {
        WellnessConfig.Config config = WellnessConfig.config;
        long elapsed = getBirthElapsed();
        return (float) elapsed / config.age.maxAge <= config.age.babyAgeThreshold;
    }

    public boolean isAdult() {
        WellnessConfig.Config config = WellnessConfig.config;
        long elapsed = getBirthElapsed();
        return !isBaby() && (float) elapsed / config.age.maxAge <= config.age.adultAgeThreshold;
    }

    public boolean isOld() {
        return !isBaby() && !isAdult();
    }

    public boolean isDead() {
        return getBirthElapsed() >= WellnessConfig.config.age.maxAge;
    }

    // Food
    public void setFood() {
        wellness.lastFoodTime = level.getGameTime();
    }

    public long getRemainingFood() {
        WellnessConfig.Config config = WellnessConfig.config;
        long elapsed = level.getGameTime() - wellness.lastFoodTime;
        long remaining = config.feed.maxFeed - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isFed() {
        return getRemainingFood() > 0;
    }

    // Water
    public void setWater() {
        wellness.lastWaterTime = level.getGameTime();
    }

    public long getRemainingWater() {
        WellnessConfig.Config config = WellnessConfig.config;
        long elapsed = level.getGameTime() - wellness.lastWaterTime;
        long remaining = config.feed.maxWater - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isHydrated() {
        return getRemainingWater() > 0;
    }

    // Sex
    public void setRandomSex() {
        wellness.sex = new Random().nextBoolean() ? AnimalSex.MALE : AnimalSex.FEMALE;
    }

    public AnimalSex getSex() {
        return wellness.sex;
    }

    public boolean isMale() {
        return getSex() == AnimalSex.MALE;
    }

    public boolean isFemale() {
        return getSex() == AnimalSex.FEMALE;
    }

    // Breeding
    public void setPartner(UUID partner) {
        wellness.partner = partner;
    }

    public void removePartner() {
        wellness.partner = null;
    }

    public UUID getPartner() {
        return wellness.partner;
    }

    public void setPregnant(boolean pregnant) {
        wellness.pregnant = pregnant;
        if (pregnant) wellness.gestationTime = level.getGameTime();
    }

    public long getRemainingGestation() {
        WellnessConfig.Config config = WellnessConfig.config;
        if (!isPregnant()) return 0;
        long elapsed = level.getGameTime() - wellness.gestationTime;
        long remaining = config.breeding.pregnantTime - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isPregnant() {
        return wellness.pregnant;
    }

    public boolean isGestationCompleted() {
        WellnessConfig.Config config = WellnessConfig.config;
        return level.getGameTime() - wellness.gestationTime >= config.breeding.pregnantTime;
    }

    public void setBreeding() {
        wellness.breedingTime = level.getGameTime();
    }

    public long getRemainingBreeding() {
        WellnessConfig.Config config = WellnessConfig.config;
        if (isFemale() && isPregnant()) return 0;
        long elapsed = level.getGameTime() - wellness.breedingTime;
        long remaining = config.breeding.breedingTime - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isBreeding() {
        WellnessConfig.Config config = WellnessConfig.config;
        return level.getGameTime() - wellness.breedingTime < config.breeding.breedingTime;
    }

    public boolean canBreeding() {
        WellnessConfig.Config config = WellnessConfig.config;
        return !this.isBreeding() && !this.isPregnant() && this.isAdult() && this.getPartner() == null && this.getAffinity() >= config.breeding.affinityThreshold;
    }
}
