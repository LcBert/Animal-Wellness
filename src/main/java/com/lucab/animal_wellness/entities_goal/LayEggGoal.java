package com.lucab.animal_wellness.entities_goal;

import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.lucab.animal_wellness.block.nest.NestBlock;
import com.lucab.animal_wellness.block.nest.NestBlockEntity;
import com.lucab.animal_wellness.config.WellnessConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.stream.StreamSupport;

public class LayEggGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.0;
    private static final double LAY_DISTANCE_SQR = 4.0;

    private final PathfinderMob mob;
    private final WellnessHelper helper;
    private BlockPos targetNestPos;
    private int layTimer;
    private int navigationDelay;

    public LayEggGoal(PathfinderMob mob) {
        this.mob = mob;
        this.helper = WellnessHelper.getInstance(mob);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(10) != 0 && targetNestPos == null) return false;
        if (!helper.isEggReady()) return false;

        this.targetNestPos = findNearestNest();
        return this.targetNestPos != null;
    }

    private BlockPos findNearestNest() {
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        int range = WellnessConfig.config.egg.searchRange;

        Iterable<BlockPos> positions = BlockPos.betweenClosed(
                mobPos.offset(-range, -2, -range),
                mobPos.offset(range, 2, range)
        );

        return StreamSupport.stream(positions.spliterator(), false)
                .map(BlockPos::immutable)
                .filter(checkPos -> {
                    BlockState state = level.getBlockState(checkPos);
                    if (state.getBlock() instanceof NestBlock && level.getBlockEntity(checkPos) instanceof NestBlockEntity nest) {
                        return !nest.isBusy() && !nest.isFull();
                    }
                    return false;
                })
                .min(Comparator.comparingDouble(pos -> pos.distSqr(mobPos)))
                .orElse(null);
    }

    @Override
    public void tick() {
        if (this.targetNestPos == null) return;

        Level level = this.mob.level();

        if (--this.navigationDelay <= 0) {
            this.navigationDelay = 10;
            this.mob.getNavigation().moveTo(
                    this.targetNestPos.getX() + 0.5,
                    this.targetNestPos.getY(),
                    this.targetNestPos.getZ() + 0.5,
                    0,
                    SPEED_MODIFIER
            );
        }

        this.mob.getLookControl().setLookAt(
                this.targetNestPos.getX() + 0.5,
                this.targetNestPos.getY() + 0.5,
                this.targetNestPos.getZ() + 0.5
        );

        double distSqr = this.mob.distanceToSqr(Vec3.atCenterOf(this.targetNestPos));
        if (distSqr <= LAY_DISTANCE_SQR) {
            this.layTimer++;

            if (this.layTimer >= WellnessConfig.config.egg.layTime) {
                if (level.getBlockEntity(this.targetNestPos) instanceof NestBlockEntity nest && !nest.isFull()) {
                    nest.addEgg();
                    helper.setEggTime();
                }
                this.stop();
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetNestPos == null || this.layTimer >= WellnessConfig.config.egg.layTime) return false;
        if (!helper.isEggReady()) return false;

        if (this.mob.level().getBlockEntity(this.targetNestPos) instanceof NestBlockEntity nest) {
            return !nest.isFull();
        }
        return false;
    }

    @Override
    public void start() {
        this.layTimer = 0;
        this.navigationDelay = 0;

        Level level = this.mob.level();
        if (level.getBlockEntity(this.targetNestPos) instanceof NestBlockEntity nest) {
            nest.setBusy(true);
        }
    }

    @Override
    public void stop() {
        if (this.targetNestPos != null) {
            Level level = this.mob.level();
            if (level.getBlockEntity(this.targetNestPos) instanceof NestBlockEntity nest) {
                nest.setBusy(false);
            }
        }
        this.targetNestPos = null;
        this.layTimer = 0;
        this.mob.getNavigation().stop();
    }
}
