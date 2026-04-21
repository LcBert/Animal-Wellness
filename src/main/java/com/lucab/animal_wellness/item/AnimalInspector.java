package com.lucab.animal_wellness.item;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
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
import java.util.UUID;

public class AnimalInspector extends Item {
    public AnimalInspector() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (player.level().isClientSide) return InteractionResult.CONSUME;
        if (interactionTarget instanceof Animal animal) {
            WellnessAttachment wellness = animal.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (wellness.isTracked()) {
                showAnimalInfo(player, animal);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void showAnimalInfo(Player player, Animal animal) {
        WellnessConfig.Config config = WellnessConfig.config;
        WellnessAttachment wellness = animal.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        MutableComponent newLine = Component.literal("\n - ").withStyle(ChatFormatting.YELLOW);
        MutableComponent component = Component.translatable("message.animal_wellness.animal_inspector.info");
        if (config.info.type)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.type", animal.getType().getDescription().getString())
                    .withStyle(ChatFormatting.YELLOW));
        if (config.info.affinity)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.affinity", new DecimalFormat("#.##").format(wellness.getAffinity()))
                    .withStyle(ChatFormatting.YELLOW));
        if (config.info.age)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.age", wellness.getAge())
                    .withStyle(ChatFormatting.YELLOW));
        if (config.info.feed)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.feed", wellness.getFeedTick())
                    .withStyle(ChatFormatting.YELLOW));
        if (config.info.sickness)
            component.append(newLine).append(Component.translatable("message.animal_wellness.animal_inspector.sickness", new DecimalFormat("#.##").format(wellness.getSickness()))
                    .withStyle(ChatFormatting.YELLOW));

        component.append(newLine).append(wellness.getSex().toString());
        component.append(newLine).append(animal.getUUID().toString());
        UUID partner = wellness.getPartner();
        component.append(newLine).append(partner == null ? "null" : partner.toString());

        player.displayClientMessage(component, false);
    }
}
