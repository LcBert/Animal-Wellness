package com.lucab.animal_wellness.screen;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.network.AnimalDataSyncRequestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnimalScreen extends Screen {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(AnimalWellness.MODID, "textures/gui/animal_screen.png");

    private int tickCounter = 0;

    private final Entity animal;
    private int currentTab = 0;

    private final List<AnimalScreenTab> tabs = List.of(
            new GeneralTab(),
            new WellnessTab(),
            new BreedingTab(),
            new GeneticsTab()
    );

    public AnimalScreen(Entity animal) {
        super(Component.translatable("screen.animal_wellness.animal_screen.title"));
        this.animal = animal;
    }

    @Override
    protected void init() {
        int tabWidth = 60;
        int tabHeight = 20;
        int startX = (width - (tabWidth * 4 + 12)) / 2;
        int startY = 20;

        for (int i = 0; i < tabs.size(); i++) {
            AnimalScreenTab tab = tabs.get(i);
            tab.setId(i);
            tab.setEntity(this.animal);
            tab.setButton(Button.builder(Component.translatable("screen.animal_wellness.animal_screen.tab." + tab.getName()),
                    btn -> setTab(tab.getId())).bounds(startX + (tabWidth + 4) * i, startY, tabWidth, tabHeight).build());
        }

        for (AnimalScreenTab tab : tabs) {
            addRenderableWidget(tab.getButton());
        }
    }

    private void setTab(int tab) {
        currentTab = tab;
    }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter % 20 == 0) {
            PacketDistributor.sendToServer(new AnimalDataSyncRequestPacket(animal.getId()));
            tickCounter = 0;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (AnimalScreenTab tab : tabs) {
            tab.getButton().active = tab.getId() != currentTab;
        }

        renderTabContent(guiGraphics);
    }

    private void renderTabContent(GuiGraphics guiGraphics) {
        int contentX = (width - 200) / 2;
        int contentY = 60;
        int lineHeight = 16;

        AnimalScreenTab tab = tabs.get(currentTab);
        tab.renderContent(font, guiGraphics, contentX, contentY, lineHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
