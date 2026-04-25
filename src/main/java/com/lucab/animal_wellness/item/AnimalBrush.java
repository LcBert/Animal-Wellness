package com.lucab.animal_wellness.item;


import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && entity.tickCount % 20 == 0 && isSelected && entity instanceof Player player) {
            ServerLevel serverLevel = (ServerLevel) level;
            AABB box = entity.getBoundingBox().inflate(10);

            List<Entity> entities = level.getEntities(player, box, checkEntity -> {
                WellnessHelper helper = WellnessHelper.getInstance(checkEntity);
                return helper.isConsideredAnimal() && !helper.isBrushed();
            });

            entities.forEach(checkEntity -> {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        checkEntity.getX(), checkEntity.getY() + 0.5, checkEntity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.1);
            });
        }
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
