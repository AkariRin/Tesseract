package dev.rbq.tesseract.inventory;

import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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

    /**
     * 覆写 getInventoryYOffset()，将玩家物品栏下移 40px，避免遮挡第二排驱动器槽（y=88）。
     * 与 Mekanism QIO Drive Array 的 ContainerBuilder.offset(0, 40) 保持一致。
     */
    @Override
    protected int getInventoryYOffset() {
        return BASE_Y_OFFSET + 40; // 84 + 40 = 124
    }

    /**
     * 覆写 stillValid() 绕过父类对 hasGui() 的检查（加固措施）。
     * 父类: tile.hasGui() → false → 下一 tick 立即关闭容器
     * 此覆写直接用 tile 存活状态 + 距离判断代替。
     */
    @Override
    public boolean stillValid(@NotNull Player player) {
        return !getTileEntity().isRemoved() &&
               player.distanceToSqr(getTileEntity().getBlockPos().getCenter()) <= 64;
    }
}
