package dev.rbq.tesseract.block.entity;

import dev.rbq.tesseract.Tesseract;
import dev.rbq.tesseract.block.TesseractBlockProvider;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.content.qio.IQIOCraftingWindowHolder;
import mekanism.common.content.qio.IQIODriveHolder;
import mekanism.common.content.qio.QIOCraftingWindow;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.slot.QIODriveSlot;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.tile.qio.TileEntityQIOComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 超维立方方块实体 - 集成 Mekanism QIO 存储功能
 * 同时实现驱动器阵列（IQIODriveHolder）和仪表板（IQIOCraftingWindowHolder）两个接口
 */
public class TesseractBlockEntity extends TileEntityQIOComponent
        implements IQIODriveHolder, IQIOCraftingWindowHolder {

    public static final int DRIVE_SLOTS = 12;

    private List<IInventorySlot> driveSlots;
    private QIOCraftingWindow[] craftingWindows;

    public TesseractBlockEntity(BlockPos pos, BlockState state) {
        super(TesseractBlockProvider.INSTANCE, pos, state);
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        // 初始化合成窗口（需在 getInitialInventory 之前完成）
        craftingWindows = new QIOCraftingWindow[IQIOCraftingWindowHolder.MAX_CRAFTING_WINDOWS];
        for (byte i = 0; i < craftingWindows.length; i++) {
            craftingWindows[i] = new QIOCraftingWindow(this, i);
        }
    }

    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        driveSlots = new ArrayList<>();
        // 12个驱动器槽（2列 x 6行）
        for (int i = 0; i < DRIVE_SLOTS; i++) {
            int x = 8 + (i % 2) * 18;
            int y = 18 + (i / 2) * 18;
            QIODriveSlot slot = new QIODriveSlot(this, i, listener, x, y);
            driveSlots.add(slot);
            builder.addSlot(slot);
        }
        // 添加合成窗口槽位（用于 NBT 持久化）
        for (QIOCraftingWindow window : craftingWindows) {
            for (int j = 0; j < 9; j++) {
                builder.addSlot(window.getInputSlot(j));
            }
            builder.addSlot(window.getOutputSlot());
        }
        return builder.build();
    }

    // ===== IQIODriveHolder =====

    @Override
    public List<IInventorySlot> getDriveSlots() {
        return driveSlots;
    }

    @Override
    public void onDataUpdate() {
        sendUpdatePacket();
    }

    // ===== IQIOCraftingWindowHolder =====

    @Override
    @Nullable
    public Level getHolderWorld() {
        return getLevel();
    }

    @Override
    public QIOCraftingWindow[] getCraftingWindows() {
        return craftingWindows;
    }

    @Override
    @Nullable
    public QIOFrequency getFrequency() {
        return frequencyComponent.getFrequency(FrequencyType.QIO);
    }

    // ===== 工厂方法 =====

    public static TesseractBlockEntity create(BlockPos pos, BlockState state) {
        return new TesseractBlockEntity(pos, state);
    }
}
