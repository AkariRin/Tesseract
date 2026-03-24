package dev.rbq.tesseract.inventory;

import dev.rbq.tesseract.Tesseract;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 超维立方仪表板容器 - 提供 QIO 物品查看/搜索/合成功能
 * 直接复用 Mekanism 的 QIOItemViewerContainer 逻辑
 */
public class TesseractDashboardContainer extends QIOItemViewerContainer {

    private final TesseractBlockEntity tile;

    public TesseractDashboardContainer(ContainerTypeRegistryObject<?> containerType, int id,
            Inventory inv, TesseractBlockEntity tile, boolean isRemote) {
        super(containerType, id, inv, isRemote, tile);
        this.tile = tile;
        // 追踪 tile entity 的频率/安全等数据到容器
        tile.addContainerTrackers(this);
        addSlotsAndOpen();
    }

    @Override
    @NotNull
    public TesseractDashboardContainer recreate() {
        TesseractDashboardContainer container = new TesseractDashboardContainer(
            Tesseract.TESSERACT_DASHBOARD, containerId, inv, tile, true
        );
        sync(container);
        return container;
    }

    @Override
    protected void openInventory(@NotNull Inventory inv) {
        super.openInventory(inv);
        tile.open(inv.player);
    }

    @Override
    protected void closeInventory(@NotNull Player player) {
        super.closeInventory(player);
        tile.close(player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !tile.isRemoved() &&
               player.distanceToSqr(tile.getBlockPos().getCenter()) <= 64;
    }

    @Override
    @Nullable
    public ICapabilityProvider getSecurityObject() {
        return tile;
    }

    public TesseractBlockEntity getTileEntity() {
        return tile;
    }
}
