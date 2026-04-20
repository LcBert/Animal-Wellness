package com.lucab.animal_wellness.event;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.AnimalWellnessAttachment;
import com.lucab.animal_wellness.entities_goal.FeedGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class EntityTick {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (event.getEntity() instanceof Animal) {
            AnimalWellnessAttachment wellness = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());

            if (wellness.getFeedTick() > 0) {
                wellness.decreaseFeedTick();
            }
        }
    }
}
