package com.lucab.animal_wellness.block.feed_rack;

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

public class FeedRackBlockEntity extends BlockEntity {
    public FeedRackBlockEntity(BlockPos pos, BlockState state) {
        super(AnimalWellness.FEED_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public final int MAX_FOOD = 10;
    private int foodAmount = 0;

    public int getFood() {
        return foodAmount;
    }

    public boolean setFood(int amount) {
        if (amount < 0 || amount > MAX_FOOD) return false;
        this.foodAmount = amount;
        setChanged();
        return true;
    }

    public boolean addFood(int amount) {
        return setFood(this.foodAmount + amount);
    }

    public boolean addFood() {
        return addFood(1);
    }

    public boolean removeFood(int amount) {
        return setFood(this.foodAmount - amount);
    }

    public boolean removeFood() {
        return removeFood(1);
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
