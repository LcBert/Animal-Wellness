package com.lucab.animal_wellness.item;

import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.text.DecimalFormat;

public class AnimalInspector extends Item {
    public AnimalInspector() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (player.level().isClientSide) return InteractionResult.CONSUME;
        if (interactionTarget instanceof Animal animal) {
            WellnessHelper helper = WellnessHelper.getInstance(animal);
            if (helper.isTracked()) {
                showAnimalInfo(player, animal);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void showAnimalInfo(Player player, Animal animal) {
        WellnessConfig.Config config = WellnessConfig.config;
        if (!config.info.enabled) return;
        WellnessHelper helper = WellnessHelper.getInstance(animal);
        MutableComponent newLine = Component.literal("\n - ").withStyle(ChatFormatting.YELLOW);
        MutableComponent component = Component.translatable("message.animal_wellness.animal_inspector.info");
        if (config.info.type)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.type", animal.getType().getDescription().getString()).withStyle(ChatFormatting.YELLOW));
        if (config.info.affinity)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.affinity", new DecimalFormat("#.##").format(helper.getAffinity())).withStyle(ChatFormatting.YELLOW));
        if (config.info.age) {
            String translatableAge = "message.animal_wellness.animal_inspector.age_";
            if (helper.isBaby()) translatableAge += "baby";
            if (helper.isAdult()) translatableAge += "adult";
            if (helper.isOld()) translatableAge += "old";
            component.append(newLine).append(Component.translatable(translatableAge).withStyle(ChatFormatting.YELLOW));
        }
        if (config.info.sex)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.sex", helper.getSex().toString()).withStyle(ChatFormatting.YELLOW));
        if (config.info.food) {
            String translatableFood = "message.animal_wellness.animal_inspector.food.";
            component.append(newLine).append(Component.translatable(translatableFood + String.valueOf(helper.isFed()))).withStyle(ChatFormatting.YELLOW);
        }
        if (config.info.hydration) {
            String translatableHydration = "message.animal_wellness.animal_inspector.hydration.";
            component.append(newLine).append(Component.translatable(translatableHydration + String.valueOf(helper.isHydrated()))).withStyle(ChatFormatting.YELLOW);
        }

        if (helper.isFemale()) {
            if (config.info.breedingInfo.pregnancy)
                component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.breeding.pregnant", String.valueOf(helper.isPregnant())).withStyle(ChatFormatting.YELLOW));
            if (config.info.breedingInfo.gestationCooldown)
                component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.breeding.gestation_cooldown", helper.getRemainingGestation()).withStyle(ChatFormatting.YELLOW));
        }
        if (config.info.breedingInfo.breedingCooldown)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.breeding.breeding_cooldown", helper.getRemainingBreeding()).withStyle(ChatFormatting.YELLOW));

        player.displayClientMessage(component, false);
    }
}
