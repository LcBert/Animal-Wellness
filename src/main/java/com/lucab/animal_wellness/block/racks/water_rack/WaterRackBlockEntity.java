package com.lucab.animal_wellness.block.racks.water_rack;

import com.lucab.animal_wellness.AnimalWellness;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WaterRackBlockEntity extends BlockEntity {
    public WaterRackBlockEntity(BlockPos pos, BlockState state) {
        super(AnimalWellness.WATER_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public static final int MAX_WATER = 10;
    private int waterAmount = 0;

    public int getWater() {
        return waterAmount;
    }

    public boolean setWater(int amount) {
        if (amount < 0 || amount > MAX_WATER) return false;
        this.waterAmount = amount;
        setChanged();
        return true;
    }

    public boolean addWater(int amount) {
        return setWater(this.waterAmount + amount);
    }

    public boolean addWater() {
        return addWater(1);
    }

    public boolean removeWater(int amount) {
        return setWater(this.waterAmount - amount);
    }

    public boolean removeWater() {
        return removeWater(1);
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
        tag.putInt("WaterCount", this.waterAmount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.waterAmount = tag.getInt("WaterCount");
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
