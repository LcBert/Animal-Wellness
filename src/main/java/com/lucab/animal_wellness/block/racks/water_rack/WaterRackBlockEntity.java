package com.lucab.animal_wellness.block.racks.water_rack;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.block.racks.RackPart;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlockEntity;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class WaterRackBlockEntity extends BlockEntity {
    public WaterRackBlockEntity(BlockPos pos, BlockState state) {
        super(AnimalWellness.WATER_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public static final int MAX_WATER = 10;
    public final FluidTank tank = new FluidTank(MAX_WATER * 1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER);
        }
    };

    public static WaterRackBlockEntity getWaterRack(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(WaterRackBlock.PART) == RackPart.RIGHT) {
            Direction leftDir = state.getValue(WaterRackBlock.FACING).getCounterClockWise();
            BlockPos leftPos = pos.relative(leftDir);
            BlockState leftState = level.getBlockState(leftPos);
            if (level.getBlockEntity(leftPos) instanceof WaterRackBlockEntity rack && leftState.getValue(WaterRackBlock.PART) == RackPart.LEFT) {
                return rack;
            }
        } else {
            if (level.getBlockEntity(pos) instanceof WaterRackBlockEntity rack) {
                return rack;
            }
        }
        return null;
    }

    public int getWater() {
        WaterRackBlockEntity rack = getWaterRack(level, worldPosition, getBlockState());
        if (rack == null) return 0;
        return rack.tank.getFluidAmount() / 1000;
    }

    public boolean setWater(int amount) {
        WaterRackBlockEntity rack = getWaterRack(level, worldPosition, getBlockState());
        if (amount < 0 || amount > MAX_WATER || rack == null) return false;
        rack.tank.setFluid(new FluidStack(Fluids.WATER, amount * 1000));
        rack.setChanged();
        this.setChanged();
        return true;
    }

    public boolean addWater() {
        return setWater(this.getWater() + 1);
    }

    public boolean removeWater() {
        return setWater(this.getWater() - 1);
    }

    public boolean hasWater() {
        return getWater() > 0;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.setBlock(worldPosition, getBlockState().setValue(WaterRackBlock.WATER, hasWater()), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Tank", tank.writeToNBT(provider, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Tank")) {
            tank.readFromNBT(provider, tag.getCompound("Tank"));
        }
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
