package com.lucab.animal_wellness.screen;

import com.lucab.animal_wellness.attachments.GeneticTraits;
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
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getTrait(GeneticTraits.TraitType.PRODUCTIVITY) * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "efficiency"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getTrait(GeneticTraits.TraitType.EFFICIENCY) * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "temperament"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getTrait(GeneticTraits.TraitType.TEMPERAMENT) * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "resistance"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getTrait(GeneticTraits.TraitType.RESISTANCE) * 100)), x + 100, y, 0xFFFFFF);
    }
}
