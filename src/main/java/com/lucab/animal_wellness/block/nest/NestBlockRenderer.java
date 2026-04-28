package com.lucab.animal_wellness.block.nest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NestBlockRenderer implements BlockEntityRenderer<NestBlockEntity> {
    private final ItemRenderer itemRenderer;

    private final float[][] eggPositions = {
            {0.5f, 0.45f},
            {0.3f, 0.25f},
            {0.3f, 0.65f},
            {0.7f, 0.65f},
            {0.7f, 0.25f},
    };

    public NestBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(NestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int eggCount = Math.min(blockEntity.getEggs(), 5);
        if (eggCount == 0) return;

        ItemStack eggStack = new ItemStack(Items.EGG);

        for (int i = 0; i < eggCount; i++) {
            poseStack.pushPose();

            float[] position = eggPositions[i];
            float x = position[0];
            float z = position[1];
            float y = 0.1f;

            poseStack.translate(x, y, z);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            itemRenderer.renderStatic(eggStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);

            poseStack.popPose();
        }
    }
}
