package dev.rbq.tesseract.client.gui;

import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import dev.rbq.tesseract.inventory.TesseractDriveArrayContainer;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.custom.GuiQIOFrequencyDataScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * 超维立方驱动器阵列 GUI - 展示12个 QIO 驱动器槽位
 * 布局与 Mekanism GuiQIODriveArray 保持一致
 */
public class GuiTesseractDriveArray extends GuiMekanismTile<TesseractBlockEntity, TesseractDriveArrayContainer> {

    public GuiTesseractDriveArray(TesseractDriveArrayContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        // 与 GuiQIODriveArray 相同：启用动态槽位渲染，扩展 GUI 高度（多出驱动器槽区域）
        dynamicSlots = true;
        imageHeight += 40;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        // 仅添加频率数据显示屏（只读），频率配置通过仪表板完成
        // 移除 GuiQIOFrequencyTab 避免返回按钮导航到错误的 GUI
        addRenderableWidget(new GuiQIOFrequencyDataScreen(
            this, 15, 19, imageWidth - 32, 46,
            tile::getQIOFrequency
        ));
    }

}
