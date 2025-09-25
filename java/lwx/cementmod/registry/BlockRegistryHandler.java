package lwx.cementmod.registry;

import lwx.cementmod.block.*;
import lwx.cementmod.fluids.ModFluids;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class BlockRegistryHandler {
    // 基础方块
    public static final BlockCrackedHardenedConcrete BLOCK_CRACKED_HARDENED_CONCRETE = new BlockCrackedHardenedConcrete();
    public static final BlockDeepClay BLOCK_DEEP_CLAY = new BlockDeepClay();
    public static final BlockLimestone BLOCK_LIMESTONE = new BlockLimestone();
    public static final BlockHardenedConcrete BLOCK_HARDENED_CONCRETE = new BlockHardenedConcrete();
    public static final BlockSteelFrame BLOCK_STEEL_FRAME = new BlockSteelFrame();
    public static final BlockCrusher BLOCK_CRUSHER = new BlockCrusher();
    public static final BlockRefractoryBrickBlock BLOCK_REFRACTORY_BRICK_BLOCK = new BlockRefractoryBrickBlock();
    public static final BlockRotaryKiln BLOCK_ROTARY_KILN = new BlockRotaryKiln();
    public static final BlockReinforcedConcrete BLOCK_REINFORCED_CONCRETE = new BlockReinforcedConcrete();
    public static final BlockSteelRebar BLOCK_STEEL_REBAR = new BlockSteelRebar();
    public static final BlockCementMixer BLOCK_CEMENT_MIXER = new BlockCementMixer();
    public static final BlockCementPacker BLOCK_CEMENT_PACKER = new BlockCementPacker();
    public static final BlockItemExtractor BLOCK_ITEM_EXTRACTOR = new BlockItemExtractor();
    public static final BlockConcreteCauldron BLOCK_CONCRETE_CAULDRON = new BlockConcreteCauldron();

    // 混凝土框架
    public static final BlockConcreteFrame BLOCK_CONCRETE_FRAME_EMPTY = new BlockConcreteFrame(BlockConcreteFrame.FrameType.EMPTY);
    public static final BlockConcreteFrame BLOCK_CONCRETE_FRAME_WET = new BlockConcreteFrame(BlockConcreteFrame.FrameType.WET);
    public static final BlockConcreteFrame BLOCK_CONCRETE_FRAME_SOLID = new BlockConcreteFrame(BlockConcreteFrame.FrameType.SOLID);
    public static final BlockConcreteFrame BLOCK_CONCRETE_FRAME_DISMANTLED = new BlockConcreteFrame(BlockConcreteFrame.FrameType.DISMANTLED);

    // 各种尺寸的混凝土板
    public static final Block16_16_4 BLOCK_16_16_4_EMPTY = new Block16_16_4(Block16_16_4.PlateType.EMPTY);
    public static final Block16_16_4 BLOCK_16_16_4_WET = new Block16_16_4(Block16_16_4.PlateType.WET);
    public static final Block16_16_4 BLOCK_16_16_4_SOLID = new Block16_16_4(Block16_16_4.PlateType.SOLID);
    public static final Block16_16_4 BLOCK_16_16_4_DISMANTLED = new Block16_16_4(Block16_16_4.PlateType.DISMANTLED);

    public static final Block16_16_6 BLOCK_16_16_6_EMPTY = new Block16_16_6(Block16_16_6.PlateType.EMPTY);
    public static final Block16_16_6 BLOCK_16_16_6_WET = new Block16_16_6(Block16_16_6.PlateType.WET);
    public static final Block16_16_6 BLOCK_16_16_6_SOLID = new Block16_16_6(Block16_16_6.PlateType.SOLID);
    public static final Block16_16_6 BLOCK_16_16_6_DISMANTLED = new Block16_16_6(Block16_16_6.PlateType.DISMANTLED);

    public static final Block16_16_8 BLOCK_16x16x8_EMPTY = new Block16_16_8(Block16_16_8.PlateType.EMPTY);
    public static final Block16_16_8 BLOCK_16x16x8_WET = new Block16_16_8(Block16_16_8.PlateType.WET);
    public static final Block16_16_8 BLOCK_16x16x8_SOLID = new Block16_16_8(Block16_16_8.PlateType.SOLID);
    public static final Block16_16_8 BLOCK_16x16x8_DISMANTLED = new Block16_16_8(Block16_16_8.PlateType.DISMANTLED);

    public static final Block16_16_10 BLOCK_16_16_10_EMPTY = new Block16_16_10(Block16_16_10.PlateType.EMPTY);
    public static final Block16_16_10 BLOCK_16_16_10_WET = new Block16_16_10(Block16_16_10.PlateType.WET);
    public static final Block16_16_10 BLOCK_16_16_10_SOLID = new Block16_16_10(Block16_16_10.PlateType.SOLID);
    public static final Block16_16_10 BLOCK_16_16_10_DISMANTLED = new Block16_16_10(Block16_16_10.PlateType.DISMANTLED);

    public static final Block16_16_12 BLOCK_16_16_12_EMPTY = new Block16_16_12(Block16_16_12.PlateType.EMPTY);
    public static final Block16_16_12 BLOCK_16_16_12_WET = new Block16_16_12(Block16_16_12.PlateType.WET);
    public static final Block16_16_12 BLOCK_16_16_12_SOLID = new Block16_16_12(Block16_16_12.PlateType.SOLID);
    public static final Block16_16_12 BLOCK_16_16_12_DISMANTLED = new Block16_16_12(Block16_16_12.PlateType.DISMANTLED);

    public static final Block16_16_14 BLOCK_16_16_14_EMPTY = new Block16_16_14(Block16_16_14.PlateType.EMPTY);
    public static final Block16_16_14 BLOCK_16_16_14_WET = new Block16_16_14(Block16_16_14.PlateType.WET);
    public static final Block16_16_14 BLOCK_16_16_14_SOLID = new Block16_16_14(Block16_16_14.PlateType.SOLID);
    public static final Block16_16_14 BLOCK_16_16_14_DISMANTLED = new Block16_16_14(Block16_16_14.PlateType.DISMANTLED);

    public static final Block16_16_16 BLOCK_16_16_16_EMPTY = new Block16_16_16(Block16_16_16.PlateType.EMPTY);
    public static final Block16_16_16 BLOCK_16_16_16_WET = new Block16_16_16(Block16_16_16.PlateType.WET);
    public static final Block16_16_16 BLOCK_16_16_16_SOLID = new Block16_16_16(Block16_16_16.PlateType.SOLID);
    public static final Block16_16_16 BLOCK_16_16_16_DISMANTLED = new Block16_16_16(Block16_16_16.PlateType.DISMANTLED);

    // 混凝土楼梯
    public static final BlockCementStair BLOCK_CEMENT_STAIR_EMPTY = new BlockCementStair(BlockCementStair.PlateType.EMPTY);
    public static final BlockCementStair BLOCK_CEMENT_STAIR_WET = new BlockCementStair(BlockCementStair.PlateType.WET);
    public static final BlockCementStair BLOCK_CEMENT_STAIR_SOLID = new BlockCementStair(BlockCementStair.PlateType.SOLID);
    public static final BlockCementStair BLOCK_CEMENT_STAIR_DISMANTLED = new BlockCementStair(BlockCementStair.PlateType.DISMANTLED);

    // 倒置混凝土楼梯
    public static final BlockInvertedCementStair BLOCK_INVERTED_CEMENT_STAIR_EMPTY = new BlockInvertedCementStair(BlockInvertedCementStair.PlateType.EMPTY);
    public static final BlockInvertedCementStair BLOCK_INVERTED_CEMENT_STAIR_WET = new BlockInvertedCementStair(BlockInvertedCementStair.PlateType.WET);
    public static final BlockInvertedCementStair BLOCK_INVERTED_CEMENT_STAIR_SOLID = new BlockInvertedCementStair(BlockInvertedCementStair.PlateType.SOLID);
    public static final BlockInvertedCementStair BLOCK_INVERTED_CEMENT_STAIR_DISMANTLED = new BlockInvertedCementStair(BlockInvertedCementStair.PlateType.DISMANTLED);

    // 倾斜水泥栏杆
    public static final BlockInclinedCementRailing BLOCK_INCLINED_CEMENT_RAILING_EMPTY = new BlockInclinedCementRailing(BlockInclinedCementRailing.RailingType.EMPTY);
    public static final BlockInclinedCementRailing BLOCK_INCLINED_CEMENT_RAILING_WET = new BlockInclinedCementRailing(BlockInclinedCementRailing.RailingType.WET);
    public static final BlockInclinedCementRailing BLOCK_INCLINED_CEMENT_RAILING_SOLID = new BlockInclinedCementRailing(BlockInclinedCementRailing.RailingType.SOLID);
    public static final BlockInclinedCementRailing BLOCK_INCLINED_CEMENT_RAILING_DISMANTLED = new BlockInclinedCementRailing(BlockInclinedCementRailing.RailingType.DISMANTLED);

    public static final BlockEndOfCementRailing BLOCK_END_OF_CEMENT_RAILING_EMPTY = new BlockEndOfCementRailing(BlockEndOfCementRailing.EndRailingType.EMPTY);
    public static final BlockEndOfCementRailing BLOCK_END_OF_CEMENT_RAILING_WET = new BlockEndOfCementRailing(BlockEndOfCementRailing.EndRailingType.WET);
    public static final BlockEndOfCementRailing BLOCK_END_OF_CEMENT_RAILING_SOLID = new BlockEndOfCementRailing(BlockEndOfCementRailing.EndRailingType.SOLID);
    public static final BlockEndOfCementRailing BLOCK_END_OF_CEMENT_RAILING_DISMANTLED = new BlockEndOfCementRailing(BlockEndOfCementRailing.EndRailingType.DISMANTLED);

    // 在 BlockRegistryHandler.java 中添加以下字段
    public static final BlockCementPillar BLOCK_CEMENT_PILLAR_EMPTY = new BlockCementPillar(BlockCementPillar.PillarType.EMPTY);
    public static final BlockCementPillar BLOCK_CEMENT_PILLAR_WET = new BlockCementPillar(BlockCementPillar.PillarType.WET);
    public static final BlockCementPillar BLOCK_CEMENT_PILLAR_SOLID = new BlockCementPillar(BlockCementPillar.PillarType.SOLID);
    public static final BlockCementPillar BLOCK_CEMENT_PILLAR_DISMANTLED = new BlockCementPillar(BlockCementPillar.PillarType.DISMANTLED);
    public static final BlockWetConcreteBucketPlaced BLOCK_WET_CONCRETE_BUCKET_PLACED = new BlockWetConcreteBucketPlaced();
    public static final BlockWetConcreteSurface BLOCK_WET_CONCRETE_SURFACE = new BlockWetConcreteSurface();


    // 所有需要注册的方块列表
    private static final List<Block> ALL_BLOCKS = new ArrayList<>();

    static {
        // 添加基础方块
        addBlock(BLOCK_CRACKED_HARDENED_CONCRETE);
        addBlock(BLOCK_WET_CONCRETE_SURFACE);
        addBlock(BLOCK_DEEP_CLAY);
        addBlock(BLOCK_LIMESTONE);
        addBlock(BLOCK_HARDENED_CONCRETE);
        addBlock(BLOCK_STEEL_FRAME);
        addBlock(BLOCK_CRUSHER);
        addBlock(BLOCK_REFRACTORY_BRICK_BLOCK);
        addBlock(BLOCK_ROTARY_KILN);
        addBlock(ModFluids.WET_CONCRETE_BLOCK);
        addBlock(BLOCK_REINFORCED_CONCRETE);
        addBlock(BLOCK_STEEL_REBAR);
        addBlock(BLOCK_CEMENT_MIXER);
        addBlock(BLOCK_CEMENT_PACKER);
        addBlock(BLOCK_ITEM_EXTRACTOR);
        addBlock(BLOCK_CONCRETE_CAULDRON);

        // 添加混凝土框架
        addBlock(BLOCK_CONCRETE_FRAME_EMPTY);
        addBlock(BLOCK_CONCRETE_FRAME_WET);
        addBlock(BLOCK_CONCRETE_FRAME_SOLID);
        addBlock(BLOCK_CONCRETE_FRAME_DISMANTLED);

        // 添加各种尺寸的混凝土板
        addBlocks(
                BLOCK_16_16_4_EMPTY, BLOCK_16_16_4_WET, BLOCK_16_16_4_SOLID, BLOCK_16_16_4_DISMANTLED,
                BLOCK_16_16_6_EMPTY, BLOCK_16_16_6_WET, BLOCK_16_16_6_SOLID, BLOCK_16_16_6_DISMANTLED,
                BLOCK_16x16x8_EMPTY, BLOCK_16x16x8_WET, BLOCK_16x16x8_SOLID, BLOCK_16x16x8_DISMANTLED,
                BLOCK_16_16_10_EMPTY, BLOCK_16_16_10_WET, BLOCK_16_16_10_SOLID, BLOCK_16_16_10_DISMANTLED,
                BLOCK_16_16_12_EMPTY, BLOCK_16_16_12_WET, BLOCK_16_16_12_SOLID, BLOCK_16_16_12_DISMANTLED,
                BLOCK_16_16_14_EMPTY, BLOCK_16_16_14_WET, BLOCK_16_16_14_SOLID, BLOCK_16_16_14_DISMANTLED,
                BLOCK_16_16_16_EMPTY, BLOCK_16_16_16_WET, BLOCK_16_16_16_SOLID, BLOCK_16_16_16_DISMANTLED
        );

        // 添加混凝土楼梯
        addBlocks(
                BLOCK_CEMENT_STAIR_EMPTY, BLOCK_CEMENT_STAIR_WET,
                BLOCK_CEMENT_STAIR_SOLID, BLOCK_CEMENT_STAIR_DISMANTLED
        );

        // 添加倒置混凝土楼梯
        addBlocks(
                BLOCK_INVERTED_CEMENT_STAIR_EMPTY, BLOCK_INVERTED_CEMENT_STAIR_WET,
                BLOCK_INVERTED_CEMENT_STAIR_SOLID, BLOCK_INVERTED_CEMENT_STAIR_DISMANTLED
        );
        // 添加倾斜水泥栏杆
        addBlock(BLOCK_INCLINED_CEMENT_RAILING_EMPTY);
        addBlock(BLOCK_INCLINED_CEMENT_RAILING_WET);
        addBlock(BLOCK_INCLINED_CEMENT_RAILING_SOLID);
        addBlock(BLOCK_INCLINED_CEMENT_RAILING_DISMANTLED);
        addBlock(BLOCK_END_OF_CEMENT_RAILING_EMPTY);
        addBlock(BLOCK_END_OF_CEMENT_RAILING_WET);
        addBlock(BLOCK_END_OF_CEMENT_RAILING_SOLID);
        addBlock(BLOCK_END_OF_CEMENT_RAILING_DISMANTLED);
        addBlock(BLOCK_WET_CONCRETE_BUCKET_PLACED);
        addBlocks(
                BLOCK_CEMENT_PILLAR_EMPTY, BLOCK_CEMENT_PILLAR_WET,
                BLOCK_CEMENT_PILLAR_SOLID, BLOCK_CEMENT_PILLAR_DISMANTLED
        );

    }

    // 辅助方法：添加单个方块到列表
    private static void addBlock(Block block) {
        if (block != null) {
            ALL_BLOCKS.add(block);
        }
    }

    // 辅助方法：添加多个方块到列表
    private static void addBlocks(Block... blocks) {
        for (Block block : blocks) {
            addBlock(block);
        }
    }

    @SubscribeEvent
    public static void onRegistry(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        // 一次性注册所有方块
        for (Block block : ALL_BLOCKS) {
            registry.register(block);
        }
    }
}