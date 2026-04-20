package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.AnimalWellnessAttachment;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedGoal extends Goal {
    private static final int SEARCH_RANGE = 10;
    private static final int EAT_TIME_SECONDS = 5;
    private static final double SPEED_MODIFIER = 1.2;
    private static final int EAT_DISTANCE = 1;

    private final PathfinderMob mob;
    private BlockPos targetRackPos;
    private int eatTimer;
    private boolean isEating;

    public FeedGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        AnimalWellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        if (wellness.getFeedTick() > 0) return false;

        for (int x = -SEARCH_RANGE; x <= SEARCH_RANGE; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -SEARCH_RANGE; z <= SEARCH_RANGE; z++) {
                    BlockPos checkPos = mobPos.offset(x, y, z);

                    if (level.getBlockState(checkPos).getBlock() instanceof FeedRackBlock) {
                        BlockEntity be = level.getBlockEntity(checkPos);
                        if (be instanceof FeedRackBlockEntity rack && rack.getFeed() > 0) {
                            this.targetRackPos = checkPos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.eatTimer = 0;
        this.isEating = false;
    }

    @Override
    public void tick() {
        if (this.targetRackPos == null) return;

        Level level = this.mob.level();
        BlockState blockState = level.getBlockState(this.targetRackPos);
        Direction facing = blockState.getValue(FeedRackBlock.FACING);

        // Calculate position in front of the rack (1 block opposite to facing direction)
        BlockPos frontPos = this.targetRackPos.relative(facing.getOpposite());
        Vec3 targetPos = new Vec3(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5);

        double distance = this.mob.position().distanceTo(targetPos);

        if (distance <= EAT_DISTANCE) {
            this.isEating = true;
            this.eatTimer++;

            if (this.eatTimer >= EAT_TIME_SECONDS * 20) {
                AnimalWellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
                BlockEntity be = level.getBlockEntity(this.targetRackPos);
                if (be instanceof FeedRackBlockEntity rack) {
                    rack.removeFeed();
                    wellness.setFeed();
                }
                this.isEating = false;
            }
        } else {
            this.mob.getNavigation().moveTo(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    SPEED_MODIFIER
            );
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetRackPos == null) return false;

        Level level = this.mob.level();

        if (!(level.getBlockState(this.targetRackPos).getBlock() instanceof FeedRackBlock)) {
            return false;
        }

        BlockEntity be = level.getBlockEntity(this.targetRackPos);
        if (!(be instanceof FeedRackBlockEntity rack) || rack.getFeed() <= 0) {
            return false;
        }

        if (this.isEating && this.eatTimer < EAT_TIME_SECONDS * 20) {
            return true;
        }

        BlockState blockState = level.getBlockState(this.targetRackPos);
        Direction facing = blockState.getValue(FeedRackBlock.FACING);
        BlockPos frontPos = this.targetRackPos.relative(facing.getOpposite());
        Vec3 targetPos = new Vec3(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5);

        double distance = this.mob.position().distanceTo(targetPos);
        return distance > EAT_DISTANCE;
    }

    @Override
    public void stop() {
        this.targetRackPos = null;
        this.eatTimer = 0;
        this.isEating = false;
        this.mob.getNavigation().stop();
    }
}
