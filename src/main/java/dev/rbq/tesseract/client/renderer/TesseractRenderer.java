package dev.rbq.tesseract.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;

/**
 * 超维立方渲染器 - 渲染末地传送门样式的效果
 */
public class TesseractRenderer implements BlockEntityRenderer<TesseractBlockEntity> {

    public TesseractRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TesseractBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.endGateway());

        // 渲染六个面，创造末地传送门效果
        // 上面 (Y = 0.75)
        renderFace(matrix4f, consumer, 0.25F, 0.75F, 0.25F, 0.75F, 0.75F, 0.75F, 0.75F, 0.75F);
        // 下面 (Y = 0.25)
        renderFace(matrix4f, consumer, 0.25F, 0.75F, 0.75F, 0.25F, 0.25F, 0.25F, 0.25F, 0.25F);
        // 东面 (X = 0.75)
        renderFace(matrix4f, consumer, 0.75F, 0.75F, 0.75F, 0.25F, 0.25F, 0.75F, 0.75F, 0.25F);
        // 西面 (X = 0.25)
        renderFace(matrix4f, consumer, 0.25F, 0.25F, 0.25F, 0.75F, 0.25F, 0.75F, 0.75F, 0.25F);
        // 北面 (Z = 0.25)
        renderFace(matrix4f, consumer, 0.25F, 0.75F, 0.25F, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F);
        // 南面 (Z = 0.75)
        renderFace(matrix4f, consumer, 0.25F, 0.75F, 0.75F, 0.75F, 0.75F, 0.75F, 0.25F, 0.25F);
    }

    /**
     * 渲染单个面
     */
    private void renderFace(Matrix4f matrix4f, VertexConsumer consumer,
                           float x1, float x2, float y1, float y2,
                           float z1, float z2, float z3, float z4) {
        consumer.vertex(matrix4f, x1, y1, z1).endVertex();
        consumer.vertex(matrix4f, x2, y1, z2).endVertex();
        consumer.vertex(matrix4f, x2, y2, z3).endVertex();
        consumer.vertex(matrix4f, x1, y2, z4).endVertex();
    }

    @Override
    public int getViewDistance() {
        return 32;
    }
}

