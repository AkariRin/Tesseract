# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Tesseract 是一个 Minecraft Forge 模组（MC 1.20.1，Forge 47.4.10），依赖 Mekanism（10.4.16.80）。模组核心内容是"超维立方"方块，带有末地传送门样式的视觉渲染效果。

## 构建与运行

```bash
# 编译构建
./gradlew build

# 在 Minecraft 客户端中运行（开发环境）
./gradlew runClient

# 在 Minecraft 服务端中运行
./gradlew runServer

# 运行游戏测试
./gradlew runGameTestServer

# 生成数据包（datagen）
./gradlew runData
```

构建产物位于 `build/libs/`，运行时工作目录为 `run/`。

## 代码架构

### 注册系统

所有注册均在 `Tesseract.java`（主类）中通过 `DeferredRegister` 集中管理：

- `BLOCKS` - 方块注册
- `ITEMS` - 物品注册
- `BLOCK_ENTITIES` - 方块实体注册
- `CREATIVE_MODE_TABS` - 创造模式标签页注册

新增内容须在此处注册，并在构造函数中调用 `.register(modEventBus)`。

### 目录结构

```
src/main/java/dev/rbq/tesseract/
├── Tesseract.java              # 模组主类，统一注册入口
├── block/
│   ├── TesseractBlock.java     # 方块定义（继承 BaseEntityBlock）
│   ├── TesseractBlockProvider.java  # 方块属性提供者（供 TileEntityQIOComponent 使用）
│   └── entity/
│       └── TesseractBlockEntity.java  # 方块实体（继承 TileEntityQIOComponent，实现 IQIODriveHolder + IQIOCraftingWindowHolder）
├── inventory/
│   ├── TesseractDashboardContainer.java    # 仪表板容器（继承 MekanismTileContainer）
│   └── TesseractDriveArrayContainer.java   # 驱动器阵列容器（12槽 QIO Drive）
└── client/
    ├── gui/
    │   ├── GuiTesseractDashboard.java      # 仪表板 GUI（继承 GuiMekanismTile）
    │   └── GuiTesseractDriveArray.java     # 驱动器阵列 GUI（dynamicSlots=true）
    └── renderer/
        └── TesseractRenderer.java     # 客户端渲染器（末地传送门效果）

src/main/resources/assets/tesseract/
├── blockstates/   # 方块状态 JSON
├── lang/          # 语言文件（en_us, zh_cn）
├── models/        # 模型 JSON
└── textures/      # 贴图
```

### 渲染架构

`TesseractRenderer` 通过 `BlockEntityRenderer` 接口实现客户端专属渲染，使用 `RenderType.endGateway()` 渲染末地传送门效果。渲染器在 `ClientSetup.onClientSetup` 中注册，该方法仅在 `Dist.CLIENT` 端执行。

### 方块状态属性

`TesseractBlock` 使用六向 `BooleanProperty`（NORTH/SOUTH/WEST/EAST/UP/DOWN）控制各面是否显示，默认全部为 `true`。

### GUI 与容器注册（Mekanism 模式）

使用 `ContainerTypeDeferredRegister`（Mekanism 专有）而非标准 Forge `DeferredRegister<MenuType<?>>`:
- 注册后须在 `Tesseract()` 构造函数中调用 `CONTAINER_TYPES.register(modEventBus)`
- `MekanismContainerType.tile(TileClass.class, factory)` 创建 tile 关联容器
- 两种工厂签名：`IMekanismSidedContainerFactory`（带 `remote` 参数）和 `IMekanismContainerFactory`（无 `remote`）
- GUI 屏幕通过 `MenuScreens.register(containerType.get(), GuiClass::new)` 注册，在 `enqueueWork` 内执行

**已知 Gotcha：**
- `MekanismTileContainer.stillValid()` 默认调用 `tile.hasGui()`，`TesseractBlockEntity` 未实现该接口会返回 `false`，导致容器在下一 tick 自动关闭。**必须覆写 `stillValid()` 用距离判断代替。**
- `TesseractBlockEntity.presetVariables()` 中必须先初始化 `craftingWindows`，再由 `getInitialInventory()` 使用，顺序不可颠倒。
- GUI 扩展高度：`imageHeight += N; inventoryLabelY = imageHeight - 94;`（N 视槽位行数而定）

## 关键配置

| 参数 | 值 |
|------|-----|
| Minecraft 版本 | 1.20.1 |
| Forge 版本 | 47.4.10 |
| Mekanism 版本 | 1.20.1-10.4.16.80 |
| Java 版本 | 17 |
| Mod ID | `tesseract` |
| 包名 | `dev.rbq.tesseract` |

## 依赖说明

Mekanism 作为强制依赖（`mandatory = true`），编译时仅引入 API（`compileOnly ... :api`），运行时使用完整包（`runtimeOnly fg.deobf(...)`）。
