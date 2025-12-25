package dev.rbq.tesseract.client;

import dev.rbq.tesseract.client.renderer.TesseractRenderer;
import dev.rbq.tesseract.init.ModBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端设置
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册超维立方渲染器
            BlockEntityRenderers.register(ModBlockEntities.TESSERACT_BLOCK_ENTITY.get(), TesseractRenderer::new);
        });
    }
}

