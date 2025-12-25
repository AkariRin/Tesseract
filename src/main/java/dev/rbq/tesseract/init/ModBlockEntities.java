package dev.rbq.tesseract.init;

import dev.rbq.tesseract.Tesseract;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块实体注册
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Tesseract.MODID);

    public static final RegistryObject<BlockEntityType<TesseractBlockEntity>> TESSERACT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tesseract", () ->
                    BlockEntityType.Builder.of(
                            TesseractBlockEntity::create,
                            Tesseract.TESSERACT_BLOCK.get()
                    ).build(null)
            );
}

