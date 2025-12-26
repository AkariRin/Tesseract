package dev.rbq.tesseract;

import com.mojang.logging.LogUtils;
import dev.rbq.tesseract.block.TesseractBlock;
import dev.rbq.tesseract.block.entity.TesseractBlockEntity;
import dev.rbq.tesseract.client.renderer.TesseractRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
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

    // 创造模式标签页
    public static final RegistryObject<CreativeModeTab> TESSERACT_TAB = CREATIVE_MODE_TABS.register("tesseract_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .icon(() -> TESSERACT_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(TESSERACT_BLOCK_ITEM.get())).build());

    public Tesseract() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

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
            });
        }
    }
}
