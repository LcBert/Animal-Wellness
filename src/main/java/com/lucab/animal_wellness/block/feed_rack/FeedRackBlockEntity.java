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

    public final int MAX_FEED = 10;
    private int feedCount = 0;

    public int getFeed() {
        return feedCount;
    }

    public boolean setFeed(int amount) {
        if (amount < 0 || amount > MAX_FEED) return false;
        this.feedCount = amount;
        setChanged();
        return true;
    }

    public boolean addFeed(int amount) {
        return setFeed(this.feedCount + amount);
    }

    public boolean addFeed() {
        return addFeed(1);
    }

    public boolean removeFeed(int amount) {
        return setFeed(this.feedCount - amount);
    }

    public boolean removeFeed() {
        return removeFeed(1);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("FeedCount", this.feedCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.feedCount = tag.getInt("FeedCount");
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
