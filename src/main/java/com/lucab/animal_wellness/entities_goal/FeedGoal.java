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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.2;
    private static final int EAT_DISTANCE = 2;

    private final PathfinderMob mob;
    private BlockPos targetRackPos;
    private int eatTimer;
    private boolean isEating;

    public FeedGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        WellnessConfig.Config config = WellnessConfig.config;
        WellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
        if (wellness.getFeedTick() > 0) return false;

        for (int x = -config.feed.searchRange; x <= config.feed.searchRange; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -config.feed.searchRange; z <= config.feed.searchRange; z++) {
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
    public void tick() {
        WellnessConfig.Config config = WellnessConfig.config;
        if (this.targetRackPos == null) return;

        Level level = this.mob.level();
        BlockState blockState = level.getBlockState(this.targetRackPos);
        Direction facing = blockState.getValue(FeedRackBlock.FACING);

        BlockPos frontPos = this.targetRackPos.relative(facing.getOpposite());
        Vec3 targetVec = new Vec3(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5);

        this.mob.getNavigation().moveTo(targetVec.x, targetVec.y, targetVec.z, SPEED_MODIFIER);

        double distance = this.mob.position().distanceTo(new Vec3(this.targetRackPos.getX(), this.targetRackPos.getY(), this.targetRackPos.getZ()));
        if (distance <= EAT_DISTANCE) {
            this.isEating = true;
            this.eatTimer++;


            WellnessAttachment wellness = this.mob.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
            if (!wellness.isFeeded()) {
                if (eatTimer % 20 == 0)
                    level.playSound(null, targetRackPos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (this.eatTimer >= config.feed.eatTime) {
                    BlockEntity be = level.getBlockEntity(this.targetRackPos);
                    if (be instanceof FeedRackBlockEntity rack) {
                        rack.removeFeed();
                        wellness.setFeed();
                        wellness.incrementAffinity();
                    }
                    this.isEating = false;
                }
            }
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

        return true;
    }

    @Override
    public void start() {
        this.eatTimer = 0;
        this.isEating = true;
    }

    @Override
    public void stop() {
        this.targetRackPos = null;
        this.eatTimer = 0;
        this.isEating = false;
        this.mob.getNavigation().stop();
    }
}
