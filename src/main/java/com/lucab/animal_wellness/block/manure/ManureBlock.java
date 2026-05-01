package com.lucab.animal_wellness.block.manure;

import com.lucab.animal_wellness.AnimalWellness;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ManureBlock extends Block {
    public static IntegerProperty AMOUNT = IntegerProperty.create("amount", 1, 5);

    public ManureBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.GRASS)
                .strength(0.2f)
                .noOcclusion()
                .noCollission());

        this.registerDefaultState(this.stateDefinition.any().setValue(AMOUNT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AMOUNT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AMOUNT, 1);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AMOUNT)) {
            case 1 -> Block.box(5, 0, 5, 11, 1, 11);
            case 2 -> Block.box(4, 0, 4, 12, 2, 12);
            case 3 -> Block.box(3, 0, 3, 14, 3, 13);
            case 4 -> Block.box(2, 0, 2, 14, 4, 14);
            case 5 -> Block.box(1, 0, 1, 15, 6, 15);
            default -> Shapes.block();
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int amount = state.getValue(AMOUNT);
        if (level.isClientSide && random.nextInt(10 * (1 - amount / 10)) == 0) {
            SimpleParticleType flyParticle = AnimalWellness.FLY_PARTICLE.get();
            level.addParticle(flyParticle, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos surfacePos = pos.below();
        return canSupportRigidBlock(level, surfacePos) || level.getBlockState(surfacePos).isFaceSturdy(level, surfacePos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() == AnimalWellness.MANURE.get()) {
            if (placeManure(level, pos)) {
                level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.shrink(player.isCreative() ? 0 : 1);
                return ItemInteractionResult.SUCCESS;
            } else {
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static boolean placeManure(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState newState = AnimalWellness.MANURE_BLOCK.get().defaultBlockState();
        if (state.canBeReplaced() && newState.canSurvive(level, pos)) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            return true;
        }
        if (state.getBlock() == AnimalWellness.MANURE_BLOCK.get()) {
            int amount = state.getValue(ManureBlock.AMOUNT);
            if (amount < 5) {
                level.setBlock(pos, AnimalWellness.MANURE_BLOCK.get().defaultBlockState().setValue(ManureBlock.AMOUNT, amount + 1), Block.UPDATE_ALL);
                return true;
            }
        }
        return false;
    }
}
