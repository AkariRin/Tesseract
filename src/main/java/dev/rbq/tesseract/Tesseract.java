package dev.rbq.tesseract;

import com.mojang.logging.LogUtils;
import dev.rbq.tesseract.block.TesseractBlock;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import dev.rbq.tesseract.client.gui.GuiTesseractDashboard;
import dev.rbq.tesseract.client.gui.GuiTesseractDriveArray;
import dev.rbq.tesseract.client.renderer.TesseractRenderer;
import dev.rbq.tesseract.inventory.TesseractDashboardContainer;
import dev.rbq.tesseract.inventory.TesseractDriveArrayContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(Tesseract.MODID)
public class Tesseract {

    public static final String MODID = "tesseract";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    // 超维立方：方块
    public static final RegistryObject<Block> TESSERACT_BLOCK = BLOCKS.register("tesseract", () -> new TesseractBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(100.0f, 3600000.0f) // 硬度100，爆炸抗性3600000
                    .requiresCorrectToolForDrops() // 需要合适的工具
                    .lightLevel(state -> 15) // 亮度15
                    .pushReaction(PushReaction.BLOCK) // 不可被活塞推动拉动
                    .isRedstoneConductor((state, getter, pos) -> true) // 红石导体
    ));
    // 超维立方：物品
    public static final RegistryObject<Item> TESSERACT_BLOCK_ITEM = ITEMS.register("tesseract", () -> new BlockItem(TESSERACT_BLOCK.get(),
            new Item.Properties()
                    .stacksTo(1) // 最大堆叠为1
                    .rarity(Rarity.EPIC) // 史诗级稀有度
    ));

    // 超维立方：方块实体
    public static final RegistryObject<BlockEntityType<TesseractBlockEntity>> TESSERACT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tesseract", () ->
                    BlockEntityType.Builder.of(
                            TesseractBlockEntity::create,
                            TESSERACT_BLOCK.get()
                    ).build(null)
            );

    // 超维立方：仪表板容器类型
    // 注意：客户端工厂使用静态辅助方法（方法体不受"非法前向引用"限制）
    private static final RegistryObject<MenuType<TesseractDashboardContainer>> TESSERACT_DASHBOARD_RAW =
            MENU_TYPES.register("tesseract_dashboard", () ->
                    net.minecraftforge.common.extensions.IForgeMenuType.create(
                            (IContainerFactory<TesseractDashboardContainer>) Tesseract::createDashboardClient
                    )
            );
    // 包装为 ContainerTypeRegistryObject，供 MekanismContainer 体系使用
    public static final ContainerTypeRegistryObject<TesseractDashboardContainer> TESSERACT_DASHBOARD =
            new ContainerTypeRegistryObject<>(TESSERACT_DASHBOARD_RAW);

    // 超维立方：驱动器阵列容器类型
    private static final RegistryObject<MenuType<TesseractDriveArrayContainer>> TESSERACT_DRIVE_ARRAY_RAW =
            MENU_TYPES.register("tesseract_drive_array", () ->
                    net.minecraftforge.common.extensions.IForgeMenuType.create(
                            (IContainerFactory<TesseractDriveArrayContainer>) Tesseract::createDriveArrayClient
                    )
            );
    // 包装为 ContainerTypeRegistryObject
    public static final ContainerTypeRegistryObject<TesseractDriveArrayContainer> TESSERACT_DRIVE_ARRAY =
            new ContainerTypeRegistryObject<>(TESSERACT_DRIVE_ARRAY_RAW);

    // 创造模式标签页
    public static final RegistryObject<CreativeModeTab> TESSERACT_TAB = CREATIVE_MODE_TABS.register("tesseract_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .icon(() -> TESSERACT_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(TESSERACT_BLOCK_ITEM.get())).build());

    // 客户端容器工厂辅助方法（方法体中可合法引用任意静态字段，不受前向引用限制）
    private static TesseractDashboardContainer createDashboardClient(int windowId, net.minecraft.world.entity.player.Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        TesseractBlockEntity tile = (TesseractBlockEntity) inv.player.level().getBlockEntity(pos);
        return new TesseractDashboardContainer(TESSERACT_DASHBOARD, windowId, inv, tile, true);
    }

    private static TesseractDriveArrayContainer createDriveArrayClient(int windowId, net.minecraft.world.entity.player.Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        TesseractBlockEntity tile = (TesseractBlockEntity) inv.player.level().getBlockEntity(pos);
        return new TesseractDriveArrayContainer(TESSERACT_DRIVE_ARRAY, windowId, inv, tile);
    }

    public Tesseract() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENU_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    // 客户端设置
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetup {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // 注册超维立方渲染器
                BlockEntityRenderers.register(TESSERACT_BLOCK_ENTITY.get(), TesseractRenderer::new);
                // 注册仪表板屏幕
                MenuScreens.register(TESSERACT_DASHBOARD.get(), GuiTesseractDashboard::new);
                // 注册驱动器阵列屏幕
                MenuScreens.register(TESSERACT_DRIVE_ARRAY.get(), GuiTesseractDriveArray::new);
            });
        }
    }
}
