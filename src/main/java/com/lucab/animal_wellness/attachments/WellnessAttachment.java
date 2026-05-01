package com.lucab.animal_wellness.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WellnessAttachment implements INBTSerializable<CompoundTag> {
    public boolean tracked = false;
    public float affinity;
    public long birthTime;

    public long lastFoodTime = 0;
    public long lastWaterTime = 0;

    public boolean hasManure = false;
    public long nextManureTime = 0;

    public long brushTime = 0;

    public AnimalSex sex = AnimalSex.MALE;

    public UUID partner = null;

    public boolean pregnant = false;
    public long gestationTime = 0;
    public long breedingTime = 0;

    public GeneticTraits partnerGenetics = new GeneticTraits();
    public GeneticTraits geneticTraits = new GeneticTraits();

    public boolean tamed = false;

    // Only for chicken
    public long eggTime = 0;

    // Only for cow
    public long milkTime = 0;

    // Only for sheep
    public long woolTime = 0;

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("tracked", tracked);
        tag.putFloat("affinity", affinity);
        tag.putLong("birthTime", birthTime);
        tag.putLong("lastFoodTime", lastFoodTime);
        tag.putLong("lastWaterTime", lastWaterTime);
        tag.putBoolean("hasManure", hasManure);
        tag.putLong("nextManureTime", nextManureTime);
        tag.putLong("brushTime", brushTime);
        tag.putString("sex", sex.name());
        tag.putBoolean("pregnant", pregnant);
        tag.putLong("pregnantTime", gestationTime);
        tag.putLong("breedingTime", breedingTime);
        tag.put("geneticTraits", geneticTraits.serializeNBT(provider));
        tag.putBoolean("tamed", tamed);

        // Only for chicken
        tag.putLong("eggTime", eggTime);

        // Only for cow
        tag.putLong("milkTime", milkTime);

        // Only for sheep
        tag.putLong("woolTime", woolTime);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        tracked = tag.getBoolean("tracked");
        affinity = tag.getFloat("affinity");
        birthTime = tag.getLong("birthTime");
        lastFoodTime = tag.getLong("lastFoodTime");
        lastWaterTime = tag.getLong("lastWaterTime");
        hasManure = tag.getBoolean("hasManure");
        nextManureTime = tag.getLong("nextManureTime");
        brushTime = tag.getLong("brushTime");
        sex = AnimalSex.valueOf(tag.getString("sex"));
        pregnant = tag.getBoolean("pregnant");
        gestationTime = tag.getLong("pregnantTime");
        breedingTime = tag.getLong("breedingTime");
        if (tag.contains("geneticTraits")) {
            geneticTraits.deserializeNBT(provider, tag.getCompound("geneticTraits"));
        }
        tamed = tag.getBoolean("tamed");

        // Only for chicken
        eggTime = tag.getLong("eggTime");

        // Only for cow
        milkTime = tag.getLong("milkTime");

        // Only for sheep
        woolTime = tag.getLong("woolTime");
    }
}
