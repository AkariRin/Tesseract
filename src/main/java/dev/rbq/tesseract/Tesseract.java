package dev.rbq.tesseract;

import com.mojang.logging.LogUtils;
import dev.rbq.tesseract.block.TesseractBlock;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import dev.rbq.tesseract.client.gui.GuiTesseractDashboard;
import dev.rbq.tesseract.client.gui.GuiTesseractDriveArray;
import dev.rbq.tesseract.client.renderer.TesseractRenderer;
import dev.rbq.tesseract.inventory.TesseractDashboardContainer;
import dev.rbq.tesseract.inventory.TesseractDriveArrayContainer;
import mekanism.common.inventory.container.type.MekanismContainerType;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
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
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MODID);

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

    // 超维立方：仪表板容器类型（使用 MekanismContainerType，支持频率选择返回按钮等）
    // 注意：工厂辅助方法中引用此字段是合法的（方法体延迟执行，不受前向引用限制）
    public static final ContainerTypeRegistryObject<TesseractDashboardContainer> TESSERACT_DASHBOARD =
            CONTAINER_TYPES.register("tesseract_dashboard",
                    () -> MekanismContainerType.tile(TesseractBlockEntity.class,
                            (MekanismContainerType.IMekanismSidedContainerFactory<TesseractBlockEntity, TesseractDashboardContainer>)
                            Tesseract::createDashboard
                    )
            );

    // 超维立方：驱动器阵列容器类型
    public static final ContainerTypeRegistryObject<TesseractDriveArrayContainer> TESSERACT_DRIVE_ARRAY =
            CONTAINER_TYPES.register("tesseract_drive_array",
                    TesseractBlockEntity.class,
                    (MekanismContainerType.IMekanismContainerFactory<TesseractBlockEntity, TesseractDriveArrayContainer>)
                    Tesseract::createDriveArray
            );

    // 创造模式标签页
    public static final RegistryObject<CreativeModeTab> TESSERACT_TAB = CREATIVE_MODE_TABS.register("tesseract_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .title(Component.translatable("itemGroup.tesseract.tesseract_tab"))
            .icon(() -> TESSERACT_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(TESSERACT_BLOCK_ITEM.get())).build());

    // 容器工厂辅助方法（方法体延迟执行，可合法引用静态字段，不受前向引用限制）
    private static TesseractDashboardContainer createDashboard(int id, Inventory inv, TesseractBlockEntity tile, boolean remote) {
        return new TesseractDashboardContainer(TESSERACT_DASHBOARD, id, inv, tile, remote);
    }

    private static TesseractDriveArrayContainer createDriveArray(int id, Inventory inv, TesseractBlockEntity tile) {
        return new TesseractDriveArrayContainer(TESSERACT_DRIVE_ARRAY, id, inv, tile);
    }

    public Tesseract() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);

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
