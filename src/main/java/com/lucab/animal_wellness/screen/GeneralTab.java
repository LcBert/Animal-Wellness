package com.lucab.animal_wellness.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.openjdk.nashorn.api.tree.BreakTree;

import java.awt.*;

public class GeneralTab extends AnimalScreenTab {
    public GeneralTab() {
        this.name = "general";
    }

    @Override
    public void renderContent(Font font, GuiGraphics guiGraphics, int x, int y, int lineHeight) {
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "affinity"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.literal(String.format("%.2f%%", helper.getAffinity() * 100)), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "age"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "age." + getAgeString()), x + 100, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "sex"), x, y, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable(getTranslationArg() + "sex." + getSexString()), x + 100, y, 0xFFFFFF);
    }

    private String getAgeString() {
        if (helper.isBaby()) return "baby";
        if (helper.isAdult()) return "adult";
        return "old";
    }

    public String getSexString() {
        return helper.isMale() ? "male" : "female";
    }
}
