package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.AnimalWellness;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class AddGoals {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PathfinderMob mob && event.getEntity() instanceof Animal && !event.getLevel().isClientSide()) {
            mob.goalSelector.addGoal(1, new EscapePlayerGoal(mob));
            mob.goalSelector.addGoal(2, new FeedGoal(mob));

            mob.goalSelector.getAvailableGoals().removeIf(goal ->
                    goal.getGoal() instanceof TemptGoal
            );
        }
    }
}
