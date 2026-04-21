package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class WellnessAttachment implements INBTSerializable<CompoundTag> {
    // Tracked
    private boolean tracked = false;

    public void setTracked() {
        this.tracked = true;
    }

    public boolean isTracked() {
        return this.tracked;
    }

    // Affinity
    private float affinity;

    public void setAffinity(float amount) {
        this.affinity = Math.clamp(amount, 0.0f, 1.0f);
    }

    public void incrementAffinity() {
        setAffinity(affinity + WellnessConfig.config.affinity.affinityRate);
    }

    public void decrementAffinity() {
        setAffinity(affinity - WellnessConfig.config.affinity.affinityRate);
    }

    public float getAffinity() {
        return this.affinity;
    }

    // Age
    private int age = 0;

    public void incrementAge() {
        this.age++;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isBaby() {
        WellnessConfig.Config config = WellnessConfig.config;
        return (float) this.age / config.age.maxAge <= config.age.babyAgeThreshold;
    }

    public boolean isAdult() {
        WellnessConfig.Config config = WellnessConfig.config;
        return !isBaby() && (float) this.age / config.age.maxAge <= config.age.adultAgeThreshold;
    }

    public boolean isOld() {
        WellnessConfig.Config config = WellnessConfig.config;
        return !isBaby() && !isAdult();
    }


    // Feed
    private int feedTick = 0;

    public void setFeed() {
        this.feedTick = WellnessConfig.config.feed.maxFeed;
    }

    public void decreaseFeedTick() {
        this.feedTick = Math.max(this.feedTick - 1, 0);
    }

    public int getFeedTick() {
        return this.feedTick;
    }

    public boolean isFed() {
        return this.feedTick > 0;
    }

    // Sickness
    private float sickness = 0.0f;

    public void setSickness(float amount) {
        this.sickness = Math.clamp(amount, 0.0f, 1.0f);
    }

    public void addSickness() {
        setSickness(this.sickness + WellnessConfig.config.sickness.sicknessRate);
    }

    public void removeSickness() {
        setSickness(this.sickness - WellnessConfig.config.sickness.sicknessRate);
    }

    public float getSickness() {
        return this.sickness;
    }

    // Breeding
    private AnimalSex sex = AnimalSex.MALE;
    private UUID partner = null;
    private int gestationCooldown = 0;
    private int breedingCooldown = 0;
    private boolean pregnant = false;

    public void setSex(AnimalSex sex) {
        this.sex = sex;
    }

    public void setRandomSex() {
        setSex(new Random().nextBoolean() ? AnimalSex.MALE : AnimalSex.FEMALE);
    }

    public AnimalSex getSex() {
        return this.sex;
    }

    public boolean isMale() {
        return getSex() == AnimalSex.MALE;
    }

    public boolean isFemale() {
        return getSex() == AnimalSex.FEMALE;
    }

    public void setPartner(UUID partner) {
        this.partner = partner;
    }

    public void removePartner() {
        this.partner = null;
    }

    public UUID getPartner() {
        return this.partner;
    }

    public void setGestation() {
        this.gestationCooldown = WellnessConfig.config.breeding.gestationTick;
    }

    public void decreaseGestation() {
        this.gestationCooldown = Math.max(this.gestationCooldown - 1, 0);
    }

    public int getGestation() {
        return this.gestationCooldown;
    }

    public void setBreadingCooldown() {
        this.breedingCooldown = WellnessConfig.config.breeding.breedingCooldown;
    }

    public void decreaseBreedingCooldown() {
        this.breedingCooldown = Math.max(this.breedingCooldown - 1, 0);
    }

    public int getBreedingCooldown() {
        return this.breedingCooldown;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
        if (pregnant) setGestation();
    }

    public boolean isPregnant() {
        return this.pregnant;
    }

    public boolean canBreeding() {
        WellnessConfig.Config config = WellnessConfig.config;
        return this.breedingCooldown == 0
                && this.gestationCooldown == 0
                && !isPregnant()
                && getPartner() == null
                && this.isAdult()
                && getAffinity() >= config.breeding.affinityThreshold
                && getSickness() <= config.breeding.sicknessThreshold;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        // Save tracked
        tag.putBoolean("tracked", this.tracked);

        // Save affinity
        tag.putFloat("affinity", this.affinity);

        // Save age
        tag.putInt("age", this.age);

        // Save feed tick
        tag.putInt("feedTick", this.feedTick);

        // Save sickness
        tag.putFloat("sickness", this.sickness);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        // Load tracked
        this.tracked = tag.getBoolean("tracked");

        // Load affinity
        this.affinity = tag.getFloat("affinity");

        // Load age
        this.age = tag.getInt("age");

        // Load feed tick
        this.feedTick = tag.getInt("feedTick");

        // Load sickness
        this.sickness = tag.getFloat("sickness");

    }
}
