package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
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
        boolean isAnimalInstance = entity instanceof Animal;
        boolean isInList = WellnessConfig.config.entityList.entities.contains(entityId) == WellnessConfig.config.entityList.whitelist;
        return (isAnimalInstance && isInList);
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
        float affinityRate = config.affinity.affinityRate;
        // Apply temperament modifier if genetics is enabled
        if (config.genetics.enabled) {
            affinityRate += affinityRate * getTemperamentModifier();
        }
        setAffinity(wellness.affinity + affinityRate);
    }

    public void decrementAffinity() {
        WellnessConfig.Config config = WellnessConfig.config;
        float affinityRate = config.affinity.affinityRate;
        affinityRate *= 1 - getAffinityScore();
        // Apply resistance modifier if genetics is enabled
        if (config.genetics.enabled) {
            affinityRate *= 1 - getResistanceModifier();
        }
        setAffinity(wellness.affinity - affinityRate);
    }

    private float getAffinityScore() {
        float score = 0.0f;
        if (isFed()) score += 0.34f;
        if (isHydrated()) score += 0.33f;
        if (isBrushed()) score += 0.33f;
        return score;
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
        long maxFeed = config.feed.maxFeed;
        // Apply efficiency modifier if genetics is enabled
        if (config.genetics.enabled) {
            maxFeed += (long) (maxFeed * getEfficiencyModifier());
        }
        long remaining = maxFeed - elapsed;
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
        long maxWater = config.feed.maxWater;
        // Apply efficiency modifier if genetics is enabled
        if (config.genetics.enabled) {
            maxWater += (long) (maxWater * getEfficiencyModifier());
        }
        long remaining = maxWater - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isHydrated() {
        return getRemainingWater() > 0;
    }

    // Manure
    public void setManure() {
        WellnessConfig.Config config = WellnessConfig.config;
        wellness.nextManureTime = new Random().nextLong(level.getGameTime() + config.manure.manureTimeMin, level.getGameTime() + config.manure.manureTimeMax);
        wellness.hasManure = true;
    }

    public void removeManure() {
        wellness.hasManure = false;
    }

    public boolean hasManure() {
        return wellness.hasManure;
    }

    public long getRemainingManureTime() {
        return wellness.nextManureTime - level.getGameTime();
    }

    public boolean canDropManure() {
        return wellness.hasManure && getRemainingManureTime() < 0;
    }

    // Brush
    public void setBrush() {
        wellness.brushTime = level.getGameTime();
    }

    public long getRemainingBrushTime() {
        WellnessConfig.Config config = WellnessConfig.config;
        long elapsed = level.getGameTime() - wellness.brushTime;
        long remaining = config.brush.brushTime - elapsed;
        if (remaining <= 0) return 0;
        return remaining;
    }

    public boolean isBrushed() {
        return getRemainingBrushTime() > 0;
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

    public void setPartner(UUID partner, GeneticTraits partnerGenetic) {
        wellness.partner = partner;
        wellness.partnerGenetics = partnerGenetic;
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

    // Genetics
    public void setGeneticTraits(GeneticTraits traits) {
        wellness.geneticTraits = traits;
    }

    public GeneticTraits getGeneticTraits() {
        return wellness.geneticTraits;
    }

    public GeneticTraits getPartnerGenetics() {
        return wellness.partnerGenetics;
    }

    public void inheritGenetics(GeneticTraits parent1, GeneticTraits parent2) {
        GeneticTraits inherited = GeneticTraits.inherit(parent1, parent2);
        setGeneticTraits(inherited);
    }

    public float getProductivityModifier() {
        WellnessConfig.Config config = WellnessConfig.config;
        return getGeneticTraits().productivity;
    }

    public float getResistanceModifier() {
        WellnessConfig.Config config = WellnessConfig.config;
        return getGeneticTraits().resistance;
    }

    public float getEfficiencyModifier() {
        WellnessConfig.Config config = WellnessConfig.config;
        return getGeneticTraits().efficiency;
    }

    public float getTemperamentModifier() {
        WellnessConfig.Config config = WellnessConfig.config;
        return getGeneticTraits().temperament;
    }
}
