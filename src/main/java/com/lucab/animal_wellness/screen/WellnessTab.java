package com.lucab.animal_wellness.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class WellnessTab extends AnimalScreenTab {
    public WellnessTab() {
        this.name = "wellness";
    }

    @Override
    public void renderContent(Font font, GuiGraphics guiGraphics, int x, int y, int lineHeight) {
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "food"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "food." + helper.isFed()), x + 100, y, 0XFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "water"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "water." + helper.isHydrated()), x + 100, y, 0XFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "brush"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "brush." + helper.isBrushed()), x + 100, y, 0XFFFFFF);
    }
}
