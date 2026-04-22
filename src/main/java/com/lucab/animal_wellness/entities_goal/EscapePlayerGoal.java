package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EscapePlayerGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.6;
    private static final float DISTANCE_THRESHOLD = 8.0f;

    private final PathfinderMob mob;
    private Vec3 escapePos;

    public EscapePlayerGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player targetPlayer = this.mob.level().getNearestPlayer(this.mob, DISTANCE_THRESHOLD);
        if (targetPlayer == null || targetPlayer.isCreative()) return false;

        float affinity = mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get()).getAffinity();
        if (affinity >= WellnessConfig.config.affinity.affinityThreshold) return false;

        if (!this.mob.hasLineOfSight(targetPlayer)) return false;

        this.escapePos = DefaultRandomPos.getPosAway(this.mob, 16, 7, targetPlayer.position());

        return this.escapePos != null;
    }

    @Override
    public void start() {
        if (this.escapePos != null) {
            this.mob.getNavigation().moveTo(this.escapePos.x, this.escapePos.y, this.escapePos.z, SPEED_MODIFIER);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
}