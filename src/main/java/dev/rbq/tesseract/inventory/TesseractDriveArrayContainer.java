package dev.rbq.tesseract.inventory;

import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;

/**
 * 超维立方驱动器阵列容器 - 提供12个 QIO 驱动器槽位管理
 * 继承 MekanismTileContainer，自动处理槽位、追踪和容器管理
 */
public class TesseractDriveArrayContainer extends MekanismTileContainer<TesseractBlockEntity> {

    public TesseractDriveArrayContainer(ContainerTypeRegistryObject<TesseractDriveArrayContainer> containerType,
            int id, Inventory inv, TesseractBlockEntity tile) {
        super(containerType, id, inv, tile);
        // MekanismTileContainer 构造函数已自动调用 addContainerTrackers() 和 addSlotsAndOpen()
    }
}
