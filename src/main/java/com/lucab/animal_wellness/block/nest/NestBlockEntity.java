package com.lucab.animal_wellness.block.nest;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.block.racks.water_rack.WaterRackBlock;
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

public class NestBlockEntity extends BlockEntity {
    public static final int MAX_EGGS = 5;

    private boolean busy = false;
    private int eggs = 0;

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(AnimalWellness.NEST_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        setChanged();
    }

    public void setEggs(int eggs) {
        this.eggs = Math.clamp(eggs, 0, MAX_EGGS);
        setChanged();
    }

    public int getEggs() {
        return eggs;
    }

    public void removeEggs() {
        setEggs(0);
    }

    public void addEgg() {
        setEggs(eggs + 1);
    }

    public boolean hasEggs() {
        return eggs > 0;
    }

    public boolean isFull() {
        return eggs >= MAX_EGGS;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("Busy", busy);
        tag.putInt("Eggs", eggs);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        busy = tag.getBoolean("Busy");
        eggs = tag.getInt("Eggs");
    }

    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.setBlock(worldPosition, getBlockState().setValue(NestBlock.EGG, getEggs()), Block.UPDATE_ALL);
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
