package com.lucab.animal_wellness.block.feed_rack;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RackPart implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    private RackPart(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
