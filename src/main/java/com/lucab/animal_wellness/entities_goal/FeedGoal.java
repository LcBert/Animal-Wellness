package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlockEntity;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.2;
    // Using squared distance to avoid expensive Math.sqrt() calls during tick
    private static final double EAT_DISTANCE_SQR = 4.0;

    private final PathfinderMob mob;
    private BlockPos targetRackPos;
    private int eatTimer;
    private int navigationDelay;

    public FeedGoal(PathfinderMob mob) {
        this.mob = mob;
        // Flag.MOVE: controls movement, Flag.LOOK: controls head rotation
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Optimization: Throttle the search so it doesn't run every single tick for every animal
        if (this.mob.getRandom().nextInt(10) != 0 && targetRackPos == null) return false;

        WellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());

        // Don't search if the animal is already full or on cooldown
        if (wellness.getFoodTick() > 0) return false;

        this.targetRackPos = findNearestFeedRack();
        return this.targetRackPos != null;
    }

    private BlockPos findNearestFeedRack() {
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        int range = WellnessConfig.config.feed.searchRange;

        // Efficiently iterate through blocks within the search volume
        for (BlockPos checkPos : BlockPos.betweenClosed(
                mobPos.offset(-range, -2, -range),
                mobPos.offset(range, 2, range))) {

            if (level.getBlockState(checkPos).getBlock() instanceof FeedRackBlock) {
                if (level.getBlockEntity(checkPos) instanceof FeedRackBlockEntity rack && rack.getFood() > 0) {
                    // Return an immutable copy to prevent position shifting during iteration
                    return checkPos.immutable();
                }
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (this.targetRackPos == null) return;

        Level level = this.mob.level();

        // Optimization: Update pathfinding every 10 ticks instead of every 1 tick
        if (--this.navigationDelay <= 0) {
            this.navigationDelay = 10;
            Direction facing = level.getBlockState(this.targetRackPos).getValue(FeedRackBlock.FACING);
            // Target the block in front of the rack based on its facing direction
            BlockPos frontPos = this.targetRackPos.relative(facing.getOpposite());
            this.mob.getNavigation().moveTo(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5, SPEED_MODIFIER);
        }

        // Keep the animal looking at the center of the rack
        this.mob.getLookControl().setLookAt(this.targetRackPos.getX() + 0.5, this.targetRackPos.getY() + 0.5, this.targetRackPos.getZ() + 0.5);

        // Calculate squared distance for better performance
        double distSqr = this.mob.distanceToSqr(Vec3.atCenterOf(this.targetRackPos));
        if (distSqr <= EAT_DISTANCE_SQR) {
            this.eatTimer++;

            WellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());

            // Play eating sound once per second (20 ticks)
            if (eatTimer % 20 == 0) {
                level.playSound(null, targetRackPos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            // Finish eating process
            if (this.eatTimer >= WellnessConfig.config.feed.eatTime) {
                if (level.getBlockEntity(this.targetRackPos) instanceof FeedRackBlockEntity rack) {
                    rack.removeFood();
                    wellness.setFood();
                    wellness.incrementAffinity();
                    this.stop(); // Task complete
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if the rack is destroyed, emptied, or the timer finishes
        if (this.targetRackPos == null || eatTimer >= WellnessConfig.config.feed.eatTime) return false;

        if (this.mob.level().getBlockEntity(this.targetRackPos) instanceof FeedRackBlockEntity rack) {
            return rack.getFood() > 0;
        }
        return false;
    }

    @Override
    public void start() {
        this.eatTimer = 0;
        this.navigationDelay = 0; // Trigger navigation immediately on start
    }

    @Override
    public void stop() {
        this.targetRackPos = null;
        this.eatTimer = 0;
        this.mob.getNavigation().stop();
    }
}