package com.lucab.animal_wellness.item;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.lucab.animal_wellness.network.OpenAnimalScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.network.PacketDistributor;

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
                WellnessAttachment attachment = animal.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
                PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenAnimalScreenPacket(animal.getId(), attachment.serializeNBT(animal.level().registryAccess())));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
