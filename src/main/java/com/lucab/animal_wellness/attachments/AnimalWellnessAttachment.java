package com.lucab.animal_wellness.attachments;

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
    private int feedTick = 0;

    public void addAffinityPlayer(UUID playerId) {
        affinityPlayers.add(playerId);
    }

    public List<UUID> getAffinityPlayers() {
        return affinityPlayers;
    }

    public void setFeed() {
        this.feedTick = 6000;
    }

    public void decreaseFeedTick() {
        this.feedTick = Math.max(this.feedTick - 1, 0);
    }

    public int getFeedTick() {
        return this.feedTick;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        // Save players UUID List
        UUIDUtil.CODEC.listOf().encodeStart(NbtOps.INSTANCE, affinityPlayers)
                .resultOrPartial(LogManager.getLogger()::error)
                .ifPresent(uuidTag -> tag.put("affinityPlayers", uuidTag));

        // Save feed tick
        tag.putInt("feedTick", feedTick);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
        // Load players UUID List
        if (compoundTag.contains("affinityPlayers")) {
            UUIDUtil.CODEC.listOf().parse(NbtOps.INSTANCE, compoundTag.get("affinityPlayers"))
                    .resultOrPartial(LogManager.getLogger()::error)
                    .ifPresent(list -> {
                        affinityPlayers.clear();
                        affinityPlayers.addAll(list);
                    });
        }

        // Load feed tick
        feedTick = compoundTag.getInt("feedTick");
    }
}
