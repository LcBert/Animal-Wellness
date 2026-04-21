package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.UUID;

public class SearchPartnerGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.2;

    private final PathfinderMob mob;
    private LivingEntity targetAnimal;
    private int loveTimer = 0;

    public SearchPartnerGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        WellnessConfig.Config config = WellnessConfig.config;
        WellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        UUID targetUUID = wellness.getPartner();
        if (targetUUID == null) return false;

        if (this.targetAnimal == null || !this.targetAnimal.getUUID().equals(targetUUID)) {
            if (mob.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(targetUUID);
                if (entity instanceof LivingEntity living) {
                    this.targetAnimal = living;
                }
            }
        }

        if (targetAnimal != null && targetAnimal.isAlive()) {
            this.loveTimer = 0;
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        WellnessConfig.Config config = WellnessConfig.config;
        double stopDistance = config.breeding.searchRange * 0.25;

        if (mob.distanceToSqr(targetAnimal) > (stopDistance * stopDistance)) {
            this.mob.getNavigation().moveTo(this.targetAnimal, SPEED_MODIFIER);
        } else {
            this.mob.getNavigation().stop();
            this.loveTimer++;
        }
    }

    @Override
    public boolean canContinueToUse() {
        WellnessConfig.Config config = WellnessConfig.config;
        return targetAnimal != null && targetAnimal.isAlive() && this.loveTimer < config.breeding.loveTick;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();

        WellnessAttachment modWellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        WellnessAttachment targetWellness = this.targetAnimal.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());

        if (modWellness.isFemale()) {
            modWellness.setPregnant(true);
        } else {
            modWellness.setBreadingCooldown();
        }

        if (targetWellness.isFemale()) {
            targetWellness.setPregnant(true);
        } else {
            targetWellness.setBreadingCooldown();
        }

        modWellness.removePartner();
        targetWellness.removePartner();
    }
}
