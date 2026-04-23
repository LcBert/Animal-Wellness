package com.lucab.animal_wellness.block.manures_farmland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class ManuredFarmland extends FarmBlock {
    public ManuredFarmland() {
        super(Properties.of()
                .mapColor(MapColor.DIRT)
                .sound(SoundType.GRAVEL)
                .strength(2.0f, 1.0f)
                .randomTicks());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);

        boolean isNearWater = isNearWater(level, pos) || level.isRainingAt(pos.above());

        if (!isNearWater && moisture > 0) {
            level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), FarmBlock.UPDATE_ALL);
        } else if (isNearWater && moisture < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, moisture + 1), FarmBlock.UPDATE_ALL);
        }

        BlockPos cropPos = pos.above();
        BlockState cropState = level.getBlockState(cropPos);

        if (moisture == 7) {
            if (cropState.getBlock() instanceof BonemealableBlock) {
                if (random.nextInt(1) == 0) {
                    cropState.randomTick(level, cropPos, random);
                }
            }
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, level.damageSources().fall());
    }

    private boolean isNearWater(Level level, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}
