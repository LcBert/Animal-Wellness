package com.lucab.animal_wellness.screen;

import com.lucab.animal_wellness.attachments.WellnessHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.Entity;

public abstract class AnimalScreenTab {
    protected String name;
    protected Entity entity;
    protected WellnessHelper helper;
    protected int id;
    protected Button button;

    public AnimalScreenTab() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        this.helper = WellnessHelper.getInstance(entity);
    }

    public Entity getEntity() {
        return this.entity;
    }

    public WellnessHelper getHelper() {
        return this.helper;
    }

    public String getTranslationArg() {
        return String.format("screen.animal_wellness.animal_screen.%s.", name);
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Button getButton() {
        return button;
    }

    public void renderContent(Font font, GuiGraphics guiGraphics, int x, int y, int lineHeight) {
    }
}
