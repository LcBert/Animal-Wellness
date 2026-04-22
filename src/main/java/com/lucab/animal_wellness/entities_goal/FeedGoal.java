package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlockEntity;
import com.lucab.animal_wellness.block.racks.water_rack.WaterRackBlock;
import com.lucab.animal_wellness.block.racks.water_rack.WaterRackBlockEntity;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedGoal extends Goal {
    private enum RackType {FEED, WATER}

    private static final double SPEED_MODIFIER = 1.2;
    // Using squared distance to avoid expensive Math.sqrt() calls during tick
    private static final double EAT_DISTANCE_SQR = 4.0;

    private final PathfinderMob mob;
    private final WellnessHelper helper;
    private BlockPos targetRackPos;
    private RackType rackType;
    private int eatTimer;
    private int navigationDelay;

    public FeedGoal(PathfinderMob mob) {
        this.mob = mob;
        this.helper = WellnessHelper.getInstance(mob);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Optimization: Throttle the search so it doesn't run every single tick for every animal
        if (this.mob.getRandom().nextInt(10) != 0 && targetRackPos == null) return false;

        // Don't search if the animal is already full
        if (this.helper.isFed() && this.helper.isHydrated()) return false;

        if (!this.helper.isFed()) {
            this.targetRackPos = findNearestFeedRack();
            this.rackType = RackType.FEED;
            if (this.targetRackPos != null) return true;
        }
        if (!this.helper.isHydrated()) {
            this.targetRackPos = findNearestWaterRack();
            this.rackType = RackType.WATER;
            if (this.targetRackPos != null) return true;
        }
        return false;
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

    private BlockPos findNearestWaterRack() {
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        int range = WellnessConfig.config.feed.searchRange;

        // Efficiently iterate through blocks within the search volume
        for (BlockPos checkPos : BlockPos.betweenClosed(
                mobPos.offset(-range, -2, -range),
                mobPos.offset(range, 2, range))) {

            if (level.getBlockState(checkPos).getBlock() instanceof WaterRackBlock) {
                if (level.getBlockEntity(checkPos) instanceof WaterRackBlockEntity rack && rack.getWater() > 0) {
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
            this.mob.getNavigation().moveTo(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5, 0, SPEED_MODIFIER);
        }

        // Keep the animal looking at the center of the rack
        this.mob.getLookControl().setLookAt(this.targetRackPos.getX() + 0.5, this.targetRackPos.getY() + 0.5, this.targetRackPos.getZ() + 0.5);

        // Calculate squared distance for better performance
        double distSqr = this.mob.distanceToSqr(Vec3.atCenterOf(this.targetRackPos));
        if (distSqr <= EAT_DISTANCE_SQR) {
            this.eatTimer++;

            // Play eating sound once per second (20 ticks)
            if (eatTimer % 20 == 0) {
                SoundEvent sound = this.rackType == RackType.FEED ? SoundEvents.GRASS_BREAK : SoundEvents.GENERIC_DRINK;
                level.playSound(null, targetRackPos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            // Finish eating process
            if (this.eatTimer >= WellnessConfig.config.feed.eatTime) {
                if (level.getBlockEntity(this.targetRackPos) instanceof FeedRackBlockEntity feedRack) {
                    feedRack.removeFood();
                    this.helper.setFood();
                    this.helper.incrementAffinity();
                    this.stop(); // Task complete
                } else if (level.getBlockEntity(this.targetRackPos) instanceof WaterRackBlockEntity waterRack) {
                    waterRack.removeWater();
                    this.helper.setWater();
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if the animal is already fed/hydrated
        if (this.rackType == RackType.FEED && this.helper.isFed()) return false;
        if (this.rackType == RackType.WATER && this.helper.isHydrated()) return false;

        // Stop if the rack is destroyed, emptied, or the timer finishes
        if (this.targetRackPos == null || eatTimer >= WellnessConfig.config.feed.eatTime) return false;

        if (this.mob.level().getBlockEntity(this.targetRackPos) instanceof FeedRackBlockEntity rack) {
            return rack.getFood() > 0;
        }

        if (this.mob.level().getBlockEntity(this.targetRackPos) instanceof WaterRackBlockEntity waterRack) {
            return waterRack.getWater() > 0;
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
        this.rackType = null;
        this.eatTimer = 0;
        this.mob.getNavigation().stop();
    }
}
