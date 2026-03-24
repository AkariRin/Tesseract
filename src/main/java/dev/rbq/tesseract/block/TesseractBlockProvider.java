package dev.rbq.tesseract.block;

import dev.rbq.tesseract.Tesseract;
import mekanism.api.providers.IBlockProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * IBlockProvider 适配器，将 Tesseract 的方块/物品包装为 Mekanism tile 系统所需的 provider
 */
public class TesseractBlockProvider implements IBlockProvider {

    public static final TesseractBlockProvider INSTANCE = new TesseractBlockProvider();

    private TesseractBlockProvider() {}

    @Override
    public Block getBlock() {
        return Tesseract.TESSERACT_BLOCK.get();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(Tesseract.MODID + ":tesseract");
    }

    @Override
    public Item asItem() {
        return Tesseract.TESSERACT_BLOCK_ITEM.get().asItem();
    }
}
