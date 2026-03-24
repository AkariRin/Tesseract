package dev.rbq.tesseract.block;

import dev.rbq.tesseract.Tesseract;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import dev.rbq.tesseract.inventory.TesseractDashboardContainer;
import dev.rbq.tesseract.inventory.TesseractDriveArrayContainer;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 超维立方方块 - 带有末地传送门样式的渲染效果 + QIO 存储功能
 */
public class TesseractBlock extends BaseEntityBlock implements IHasTileEntity<TesseractBlockEntity> {

    private TileEntityTypeRegistryObject<TesseractBlockEntity> tileType;

    @Override
    public TileEntityTypeRegistryObject<? extends TesseractBlockEntity> getTileType() {
        if (tileType == null) {
            tileType = new TileEntityTypeRegistryObject<>(Tesseract.TESSERACT_BLOCK_ENTITY);
        }
        return tileType;
    }

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public TesseractBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.TRUE)
                .setValue(SOUTH, Boolean.TRUE)
                .setValue(WEST, Boolean.TRUE)
                .setValue(EAST, Boolean.TRUE)
                .setValue(UP, Boolean.TRUE)
                .setValue(DOWN, Boolean.TRUE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public TesseractBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return TesseractBlockEntity.create(pos, state);
    }

    /**
     * 右键交互：
     * - 站立右键 → 打开 QIO 仪表板（物品查看/搜索/合成）
     * - 蹲下右键 → 打开 QIO 驱动器阵列（管理驱动器）
     */
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level,
            @NotNull BlockPos pos, @NotNull Player player,
            @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof TesseractBlockEntity tile)) return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            // 蹲下右键 → 驱动器阵列 UI
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("block.tesseract.tesseract");
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) {
                    return new TesseractDriveArrayContainer(Tesseract.TESSERACT_DRIVE_ARRAY, id, inv, tile);
                }
            }, pos);
        } else {
            // 站立右键 → 仪表板 UI
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("block.tesseract.tesseract");
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) {
                    return new TesseractDashboardContainer(Tesseract.TESSERACT_DASHBOARD, id, inv, tile, false);
                }
            }, pos);
        }
        return InteractionResult.CONSUME;
    }

    /**
     * 返回 Mekanism tile entity 的 tick 处理器，驱动器状态更新和合成配方刷新依赖此机制
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
            @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, Tesseract.TESSERACT_BLOCK_ENTITY.get(),
            level.isClientSide ? TileEntityMekanism::tickClient : TileEntityMekanism::tickServer);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
