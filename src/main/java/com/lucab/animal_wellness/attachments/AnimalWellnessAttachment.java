package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnimalWellnessAttachment implements INBTSerializable<CompoundTag> {
    private float affinity;
    private boolean tracked = false;
    private int feedTick = 0;
    private float sickness = 0.0f;
    private int age = 0;

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

    public void setFeed() {
        this.feedTick = WellnessConfig.config.feed.maxFeed;
    }

    public void decreaseFeedTick() {
        this.feedTick = Math.max(this.feedTick - 1, 0);
    }

    public int getFeedTick() {
        return this.feedTick;
    }

    public boolean isFeeded() {
        return this.feedTick > 0;
    }

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

    public void setTracked() {
        this.tracked = true;
    }

    public boolean isTracked() {
        return this.tracked;
    }

    public int getAge() {
        return this.age;
    }

    public void incrementAge() {
        this.age++;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        // Save affinity
        tag.putFloat("affinity", this.affinity);

        // Save tracked
        tag.putBoolean("tracked", this.tracked);

        // Save feed tick
        tag.putInt("feedTick", this.feedTick);

        // Save sickness
        tag.putFloat("sickness", this.sickness);

        // Save age
        tag.putInt("age", this.age);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        // Load affinity
        this.affinity = tag.getFloat("affinity");

        // Load tracked
        this.tracked = tag.getBoolean("tracked");

        // Load feed tick
        this.feedTick = tag.getInt("feedTick");

        // Load sickness
        this.sickness = tag.getFloat("sickness");

        // Load age
        this.age = tag.getInt("age");
    }
}
