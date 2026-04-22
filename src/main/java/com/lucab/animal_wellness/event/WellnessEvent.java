package com.lucab.animal_wellness.event;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.lucab.animal_wellness.block.manure.ManureBlock;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class WellnessEvent {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        WellnessHelper helper = WellnessHelper.getInstance(entity);
        if (entity instanceof Animal && helper.isConsideredAnimal() && !event.getLevel().isClientSide) {
            if (!helper.isTracked()) {
                helper.setTracked();
                helper.setRandomSex();
                helper.setBirth();
                helper.setManure();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) return;
        WellnessConfig.Config config = WellnessConfig.config;
        WellnessHelper helper = WellnessHelper.getInstance(entity);

        if (entity instanceof Animal animal && helper.isConsideredAnimal()) {
            // Age
            if (helper.isBaby()) {
                animal.setBaby(true);
                animal.setAge(-10);
            } else {
                animal.setBaby(false);
            }
            if (helper.isDead()) entity.kill();

            // Affinity
            if (level.getGameTime() % 5000 == 0) if (!helper.isFed() && !helper.isHydrated()) {
                helper.decrementAffinity();
            }

            // Manure
            if (helper.canDropManure() && config.manure.enabled) {
                helper.setManure();
                ManureBlock.placeManure(level, animal.getOnPos().above());
            }

            // Breeding
            if (config.breeding.enabled) {
                // Pregnant
                if (helper.isPregnant()) {
                    if (helper.isGestationCompleted()) {
                        helper.setBreeding();
                        helper.setPregnant(false);

                        if (level instanceof ServerLevel serverLevel) {
                            AgeableMob baby = animal.getBreedOffspring(serverLevel, animal);
                            if (baby != null) {
                                baby.moveTo(animal.getX(), animal.getY(), animal.getZ());
                                serverLevel.addFreshEntity(baby);

                                serverLevel.sendParticles(ParticleTypes.HEART, baby.getX(), baby.getY(), baby.getZ(), 5, 0.2, 0.2, 0.2, 0.3);
                            }
                        }
                    }
                }

                // Partner
                if (helper.canBreeding() && helper.isMale()) {
                    if (helper.getPartner() == null) {
                        int searchRange = config.breeding.searchRange;
                        AABB searchBox = animal.getBoundingBox().inflate(searchRange);
                        LivingEntity nearest = level.getNearestEntity((Class<? extends LivingEntity>) animal.getClass(), TargetingConditions.DEFAULT, animal, animal.getX(), animal.getY(), animal.getZ(), searchBox);
                        if (nearest != null) {
                            WellnessHelper partnerHelper = WellnessHelper.getInstance(nearest);
                            if (partnerHelper.canBreeding() && partnerHelper.isFemale()) {
                                helper.setPartner(nearest.getUUID());
                                partnerHelper.setPartner(animal.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDropsItems(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        WellnessConfig.Config config = WellnessConfig.config;
        WellnessHelper helper = WellnessHelper.getInstance(entity);
        if (entity instanceof Animal && helper.isConsideredAnimal() && !helper.isBaby() && config.drop.affinityDrop) {
            if (helper.getAffinity() >= config.drop.affinityThreshold) {
                event.getDrops().forEach(item -> {
                    item.getItem().setCount((int) (item.getItem().getCount() + config.drop.maxDrop * helper.getAffinity()));
                });
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onAnimalFeed(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        WellnessHelper helper = WellnessHelper.getInstance(entity);
        if (entity instanceof Animal animal && helper.isConsideredAnimal()) {
            ItemStack stack = event.getItemStack();
            if (animal.isFood(stack)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.PASS);
            }
        }
    }
}
