package com.lucab.animal_wellness.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GeneticsTab extends AnimalScreenTab {
    public GeneticsTab() {
        this.name = "genetics";
    }

    @Override
    public void renderContent(Font font, GuiGraphics guiGraphics, int x, int y, int lineHeight) {
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "productivity"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getProductivityModifier() * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "efficiency"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getEfficiencyModifier() * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "temperament"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getTemperamentModifier() * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "resistance"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getResistanceModifier() * 100)), x + 100, y, 0xFFFFFF);
    }
}
