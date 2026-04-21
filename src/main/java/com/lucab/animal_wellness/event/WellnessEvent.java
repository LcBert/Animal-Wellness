package com.lucab.animal_wellness.event;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.particles.DustParticleOptions;
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
import org.joml.Vector3f;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class WellnessEvent {
    @SubscribeEvent
    public static void onEntityJoinLeven(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Animal) {
            WellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (!wellness.isTracked()) {
                wellness.setTracked();
                wellness.setRandomSex();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        WellnessConfig.Config config = WellnessConfig.config;
        if (entity instanceof Animal animal) {
            WellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (!wellness.isTracked()) wellness.setTracked();

            // Feed
            if (wellness.getFeedTick() > 0) wellness.decreaseFeedTick();

            // Age
            wellness.incrementAge();
            if (wellness.isBaby()) {
                animal.setBaby(true);
                animal.setAge(-10);
            } else {
                animal.setBaby(false);
            }

            // Death from old age
            if (wellness.getAge() >= WellnessConfig.config.age.maxAge) {
                entity.kill();
            }

            // Sickness
            if (config.sickness.enabled) {
                if (entity.tickCount % 20 == 0) {
                    if (wellness.getFeedTick() > 0) {
                        wellness.removeSickness();
                    } else {
                        wellness.addSickness();
                    }
                }

                // Hurt
                if (entity.tickCount % config.sickness.hurtTickRate == 0 && wellness.getSickness() >= config.sickness.sicknessThreshold) {
                    entity.hurt(level.damageSources().generic(), 1.0f);
                }

                // Particles
                if (wellness.getSickness() >= config.sickness.sicknessThreshold) {
                    DustParticleOptions dustParticle = new DustParticleOptions(new Vector3f(0.0f, 1.0f, 0.0f), 0.5f);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(dustParticle,
                                entity.getX(), entity.getY() + 0.5, entity.getZ(),
                                5, 0.2f, 0.2f, 0.2f, 0.3f
                        );
                    }
                }
            }

            // Breeding
            if (config.breeding.enabled) {
                // Pregnant
                if (wellness.isPregnant()) {
                    if (wellness.getGestation() > 0) {
                        wellness.decreaseGestation();
                    } else {
                        wellness.setBreadingCooldown();
                        wellness.setPregnant(false);

                        if (level instanceof ServerLevel serverLevel) {
                            AgeableMob baby = animal.getBreedOffspring(serverLevel, animal);
                            if (baby != null) {
                                baby.moveTo(animal.getX(), animal.getY(), animal.getZ());
                                serverLevel.addFreshEntity(baby);

                                serverLevel.sendParticles(ParticleTypes.HEART, baby.getX(), baby.getY(), baby.getZ(),
                                        5, 0.2, 0.2, 0.2, 0.3);
                            }
                        }
                    }
                }
                // Breeding cooldown
                if (wellness.getBreedingCooldown() > 0) {
                    wellness.decreaseBreedingCooldown();
                }

                // Partner
                if (wellness.canBreeding() && wellness.isMale()) {
                    if (wellness.getPartner() == null) {
                        int searchRange = config.breeding.searchRange;
                        AABB searchBox = animal.getBoundingBox().inflate(searchRange);
                        LivingEntity nearest = level.getNearestEntity(
                                (Class<? extends LivingEntity>) animal.getClass(),
                                TargetingConditions.DEFAULT,
                                animal,
                                animal.getX(), animal.getY(), animal.getZ(),
                                searchBox
                        );
                        if (nearest != null) {
                            WellnessAttachment partnerWellness = nearest.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
                            if (partnerWellness.canBreeding() && partnerWellness.isFemale()) {
                                wellness.setPartner(nearest.getUUID());
                                partnerWellness.setPartner(animal.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Animal) {
            WellnessConfig.Config config = WellnessConfig.config;
            WellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (wellness.isOld()
                    || wellness.getAffinity() < config.drop.affinityThreshold
                    || wellness.getSickness() > config.drop.sicknessThreshold) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onAnimalFeed(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Animal animal) {
            ItemStack stack = event.getItemStack();
            if (animal.isFood(stack)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.PASS);
            }
        }
    }
}
