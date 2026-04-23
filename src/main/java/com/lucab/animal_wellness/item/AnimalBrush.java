package com.lucab.animal_wellness.item;


import com.lucab.animal_wellness.attachments.WellnessHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimalBrush extends Item {
    public AnimalBrush() {
        super(new Item.Properties().stacksTo(1).durability(256));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 15;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (player.level().isClientSide) return InteractionResult.CONSUME;
        WellnessHelper helper = WellnessHelper.getInstance(interactionTarget);
        if (helper.isConsideredAnimal() && !helper.isBrushed()) {
            helper.setBrush();
            if (!player.isCreative()) stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
            player.level().playSound(null, interactionTarget.blockPosition(), SoundEvents.BRUSH_GENERIC, SoundSource.AMBIENT, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
