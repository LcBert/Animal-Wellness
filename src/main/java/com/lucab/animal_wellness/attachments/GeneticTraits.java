package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class GeneticTraits implements INBTSerializable<CompoundTag> {
    public enum TraitType {PRODUCTIVITY, EFFICIENCY, TEMPERAMENT, RESISTANCE;}

    private final Map<TraitType, Float> traits;

    public GeneticTraits() {
        Random random = new Random();
        this.traits = new EnumMap<>(TraitType.class);
        for (TraitType type : TraitType.values()) {
            this.traits.put(type, random.nextFloat(0.0f, 0.02f));
        }
    }

    public GeneticTraits(Map<TraitType, Float> traits) {
        this.traits = new EnumMap<>(TraitType.class);
        for (Map.Entry<TraitType, Float> entry : traits.entrySet()) {
            this.traits.put(entry.getKey(), Math.clamp(entry.getValue(), 0.0f, 1.0f));
        }
    }

    /**
     * Crea tratti genetici per un bambino basati sui genitori con mutazione
     */
    public static GeneticTraits inherit(GeneticTraits parent1, GeneticTraits parent2) {
        WellnessConfig.Config config = WellnessConfig.config;
        Random random = new Random();

        Map<TraitType, Float> childTraits = new EnumMap<>(TraitType.class);
        for (TraitType type : TraitType.values()) {
            // Media dei genitori
            float parentAvg = (parent1.getTrait(type) + parent2.getTrait(type)) / 2.0f;
            // Applica mutazione
            parentAvg += random.nextFloat(config.genetics.mutationAmount);
            childTraits.put(type, parentAvg);
        }

        return new GeneticTraits(childTraits);
    }

    /**
     * Calcola il punteggio complessivo dei tratti
     */
    public float getOverallScore() {
        float sum = 0.0f;
        for (Float value : traits.values()) {
            sum += value;
        }
        return sum / TraitType.values().length;
    }

    public float getTrait(TraitType type) {
        return traits.getOrDefault(type, 0.0f);
    }

    public void setTrait(TraitType type, float value) {
        traits.put(type, Math.clamp(value, 0.0f, 1.0f));
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (TraitType type : TraitType.values()) {
            tag.putFloat(type.name().toLowerCase(), traits.get(type));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        for (TraitType type : TraitType.values()) {
            traits.put(type, tag.getFloat(type.name().toLowerCase()));
        }
    }
}
