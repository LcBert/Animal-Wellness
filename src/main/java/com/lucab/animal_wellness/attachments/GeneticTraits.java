package com.lucab.animal_wellness.attachments;

import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GeneticTraits implements INBTSerializable<CompoundTag> {
    // Tratti genetici (0.0 - 1.0 scale)
    public float productivity;     // productivity: more products
    public float efficiency;       // efficiency: food/water consumption
    public float temperament;      // temperament: affinity encreasment
    public float resistance;       // resistance: affinity reduction

    public GeneticTraits() {
        Random random = new Random();
        this.productivity = random.nextFloat(0.0f, 0.02f);
        this.efficiency = random.nextFloat(0.0f, 0.02f);
        this.temperament = random.nextFloat(0.0f, 0.02f);
        this.resistance = random.nextFloat(0.0f, 0.02f);
    }

    public GeneticTraits(float productivity, float resistance, float efficiency, float temperament) {
        this.productivity = Math.clamp(productivity, 0.0f, 1.0f);
        this.efficiency = Math.clamp(efficiency, 0.0f, 1.0f);
        this.temperament = Math.clamp(temperament, 0.0f, 1.0f);
        this.resistance = Math.clamp(resistance, 0.0f, 1.0f);
    }

    /**
     * Crea tratti genetici per un bambino basati sui genitori con mutazione
     */
    public static GeneticTraits inherit(GeneticTraits parent1, GeneticTraits parent2) {
        WellnessConfig.Config config = WellnessConfig.config;
        Random random = new Random();

        // Media dei genitori
        float productivity = (parent1.productivity + parent2.productivity) / 2.0f;
        float efficiency = (parent1.efficiency + parent2.efficiency) / 2.0f;
        float temperament = (parent1.temperament + parent2.temperament) / 2.0f;
        float resistance = (parent1.resistance + parent2.resistance) / 2.0f;

        // Applica mutazioni
        productivity += (random.nextFloat( config.genetics.mutationAmount));
        efficiency += (random.nextFloat( config.genetics.mutationAmount));
        temperament += (random.nextFloat( config.genetics.mutationAmount));
        resistance += (random.nextFloat( config.genetics.mutationAmount));

        return new GeneticTraits(productivity, resistance, efficiency, temperament);
    }

    /**
     * Calcola il punteggio complessivo dei tratti
     */
    public float getOverallScore() {
        return (productivity + resistance + efficiency + temperament) / 5.0f;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("productivity", productivity);
        tag.putFloat("efficiency", efficiency);
        tag.putFloat("temperament", temperament);
        tag.putFloat("resistance", resistance);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        productivity = tag.getFloat("productivity");
        efficiency = tag.getFloat("efficiency");
        temperament = tag.getFloat("temperament");
        resistance = tag.getFloat("resistance");
    }
}
