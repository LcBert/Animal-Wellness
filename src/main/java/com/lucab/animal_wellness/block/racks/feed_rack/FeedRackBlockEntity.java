package com.lucab.animal_wellness.block.racks.feed_rack;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.block.racks.RackPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FeedRackBlockEntity extends BlockEntity {
    public FeedRackBlockEntity(BlockPos pos, BlockState state) {
        super(AnimalWellness.FEED_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public static final int MAX_FOOD = 10;
    private int foodAmount = 0;

    public static FeedRackBlockEntity getFeedRack(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(FeedRackBlock.PART) == RackPart.RIGHT) {
            Direction leftDir = state.getValue(FeedRackBlock.FACING).getCounterClockWise();
            BlockPos leftPos = pos.relative(leftDir);
            BlockState leftState = level.getBlockState(leftPos);
            if (level.getBlockEntity(leftPos) instanceof FeedRackBlockEntity rack && leftState.getValue(FeedRackBlock.PART) == RackPart.LEFT) {
                return rack;
            }
        } else {
            if (level.getBlockEntity(pos) instanceof FeedRackBlockEntity rack) {
                return rack;
            }
        }
        return null;
    }

    public int getFood() {
        FeedRackBlockEntity rack = getFeedRack(level, worldPosition, getBlockState());
        if (rack == null) return 0;
        return rack.foodAmount;
    }

    public boolean setFood(int amount) {
        FeedRackBlockEntity rack = getFeedRack(level, worldPosition, getBlockState());
        if (amount < 0 || amount > MAX_FOOD || rack == null) return false;
        rack.foodAmount = amount;
        rack.setChanged();
        this.setChanged();
        return true;
    }

    public boolean addFood() {
        return setFood(this.getFood() + 1);
    }

    public boolean removeFood() {
        return  setFood(this.getFood() - 1);
    }

    public boolean hasFood() {
        return getFood() > 0;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.setBlock(worldPosition, getBlockState().setValue(FeedRackBlock.FOOD, hasFood()), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("FoodCount", this.foodAmount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.foodAmount = tag.getInt("FoodCount");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }
}
