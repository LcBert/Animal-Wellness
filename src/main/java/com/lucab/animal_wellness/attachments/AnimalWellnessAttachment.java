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
    private final List<UUID> affinityPlayers = new ArrayList<>();
    private boolean tracked = false;
    private int feedTick = 0;
    private float sickness = 0.0f;
    private int age = 0;

    public void addAffinityPlayer(UUID playerId) {
        affinityPlayers.add(playerId);
    }

    public List<UUID> getAffinityPlayers() {
        return affinityPlayers;
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

        // Save players UUID List
        UUIDUtil.CODEC.listOf().encodeStart(NbtOps.INSTANCE, affinityPlayers)
                .resultOrPartial(LogManager.getLogger()::error)
                .ifPresent(uuidTag -> tag.put("affinityPlayers", uuidTag));

        // Save tracked
        tag.putBoolean("tracked", tracked);

        // Save feed tick
        tag.putInt("feedTick", feedTick);

        // Save sickness
        tag.putFloat("sickness", sickness);

        // Save age
        tag.putInt("age", age);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        // Load players UUID List
        if (tag.contains("affinityPlayers")) {
            UUIDUtil.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("affinityPlayers")).resultOrPartial(LogManager.getLogger()::error).ifPresent(list -> {
                affinityPlayers.clear();
                affinityPlayers.addAll(list);
            });
        }

        // Load tracked
        tracked = tag.getBoolean("tracked");

        // Load feed tick
        feedTick = tag.getInt("feedTick");

        // Load sickness
        sickness = tag.getFloat("sickness");

        // Load age
        age = tag.getInt("age");
    }
}
