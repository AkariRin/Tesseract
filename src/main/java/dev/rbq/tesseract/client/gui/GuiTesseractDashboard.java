package dev.rbq.tesseract.client.gui;

import dev.rbq.tesseract.inventory.TesseractDashboardContainer;
import mekanism.client.gui.element.tab.GuiQIOFrequencyTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.qio.GuiQIOItemViewer;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.content.qio.QIOFrequency;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 超维立方仪表板 GUI - 展示 QIO 物品查看器（搜索/排序/合成）
 * 继承 GuiQIOItemViewer，直接复用所有 QIO 视图逻辑
 */
public class GuiTesseractDashboard extends GuiQIOItemViewer<TesseractDashboardContainer> {

    public GuiTesseractDashboard(TesseractDashboardContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        // 添加频率选择标签
        addRenderableWidget(new GuiQIOFrequencyTab(this, menu.getTileEntity()));
        // 添加安全系统标签
        addRenderableWidget(new GuiSecurityTab(this, menu.getTileEntity()));
    }

    @Override
    @Nullable
    public Frequency.FrequencyIdentity getFrequency() {
        QIOFrequency freq = menu.getTileEntity().getQIOFrequency();
        return freq == null ? null : freq.getIdentity();
    }

    @Override
    @NotNull
    public GuiQIOItemViewer<TesseractDashboardContainer> recreate(TesseractDashboardContainer container) {
        return new GuiTesseractDashboard(container, inv, getTitle());
    }
}
