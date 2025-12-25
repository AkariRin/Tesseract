package dev.rbq.tesseract.block.entity;

import dev.rbq.tesseract.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 超维立方方块实体 - 仅用于附加渲染器
 */
public class TesseractBlockEntity extends BlockEntity {

    public TesseractBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 工厂方法用于创建实例
     */
    public static TesseractBlockEntity create(BlockPos pos, BlockState state) {
        return new TesseractBlockEntity(ModBlockEntities.TESSERACT_BLOCK_ENTITY.get(), pos, state);
    }
}

