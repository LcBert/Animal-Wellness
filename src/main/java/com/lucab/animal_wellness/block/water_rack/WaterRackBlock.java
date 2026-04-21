package com.lucab.animal_wellness.block.water_rack;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.block.feed_rack.RackPart;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterRackBlock extends BaseEntityBlock {
    public static final MapCodec<WaterRackBlock> CODEC = simpleCodec(WaterRackBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RackPart> PART = EnumProperty.create("part", RackPart.class);
    public static final BooleanProperty WATER = BooleanProperty.create("water");

    public WaterRackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, RackPart.LEFT)
                .setValue(WATER, false));
    }

    public WaterRackBlock() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .strength(2.0f, 1.0f)
                .requiresCorrectToolForDrops()
                .noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, RackPart.LEFT)
                .setValue(WATER, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATER);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();

        Direction rightDir = facing.getClockWise();
        BlockPos rightPos = pos.relative(rightDir);

        if (context.getLevel().getBlockState(rightPos).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(PART, RackPart.LEFT);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction rightDir = state.getValue(FACING).getClockWise();
        level.setBlock(pos.relative(rightDir), state.setValue(PART, RackPart.RIGHT), Block.UPDATE_ALL);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            RackPart part = state.getValue(PART);
            Direction facing = state.getValue(FACING);

            Direction neighborDir = (part == RackPart.LEFT) ? facing.getClockWise() : facing.getCounterClockWise();
            BlockPos neighborPos = pos.relative(neighborDir);

            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(this) && neighborState.getValue(PART) != part) {
                level.destroyBlock(neighborPos, false);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(PART) == RackPart.RIGHT) {
            Direction leftDir = state.getValue(FACING).getCounterClockWise();
            BlockPos leftPos = pos.relative(leftDir);
            BlockState leftState = level.getBlockState(leftPos);
            if (leftState.is(this) && leftState.getValue(PART) == RackPart.LEFT) {
                BlockHitResult newHitResult = new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), leftPos, hitResult.isInside());
                return this.useItemOn(stack, leftState, level, leftPos, player, hand, newHitResult);
            }
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof WaterRackBlockEntity rack) {
            if (stack.getItem() == Items.WATER_BUCKET) {
                if (rack.addWater()) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        player.getInventory().add(new ItemStack(Items.BUCKET));
                    }
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new WaterRackBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return state.getValue(PART) == RackPart.RIGHT ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
