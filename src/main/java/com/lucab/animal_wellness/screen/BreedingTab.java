package com.lucab.animal_wellness.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class BreedingTab extends AnimalScreenTab {
    public BreedingTab() {
        this.name = "breeding";
    }

    @Override
    public void renderContent(Font font, GuiGraphics guiGraphics, int x, int y, int lineHeight) {
        if (helper.isFemale()) {
            guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "pregnant"), x, y, 0xFFFFFF);
            guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "pregnant." + helper.isPregnant()), x + 150, y, 0xFFFFFF);
            y += lineHeight;

            guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "gestation_cooldown"), x, y, 0xFFFFFF);
            guiGraphics.drawString(font, Component.literal(formatTicks(helper.getRemainingGestation())), x + 150, y, 0xFFFFFF);
            y += lineHeight;
        }

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "breeding_cooldown"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(formatTicks(helper.getRemainingBreeding())), x + 150, y, 0xFFFFFF);
    }

    private String formatTicks(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        return minutes + "m " + (seconds % 60) + "s";
    }
}
