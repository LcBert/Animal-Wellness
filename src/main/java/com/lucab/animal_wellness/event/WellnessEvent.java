package com.lucab.animal_wellness.event;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.AnimalWellnessAttachment;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        WellnessConfig.Config config = WellnessConfig.config;
        if (entity instanceof Animal animal) {
            AnimalWellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (!wellness.isTracked()) wellness.setTracked();

            // Feed
            if (wellness.getFeedTick() > 0) wellness.decreaseFeedTick();

            // Age
            wellness.incrementAge();
            if ((float) wellness.getAge() / config.age.maxAge <= config.age.babyAgeThreshold) {
                animal.setBaby(true);
                animal.setAge(-10);
            } else {
                animal.setBaby(false);
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

            // Death from old age
            if (wellness.getAge() >= WellnessConfig.config.age.maxAge) {
                entity.kill();
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Animal) {
            AnimalWellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (wellness.getAge() >= WellnessConfig.config.age.maxAge) {
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
