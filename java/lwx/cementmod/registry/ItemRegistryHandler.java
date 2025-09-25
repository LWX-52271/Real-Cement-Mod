package lwx.cementmod.registry;

import lwx.cementmod.fluids.ModFluids;
import lwx.cementmod.item.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ItemRegistryHandler {
    // 所有需要注册的物品列表
    private static final List<Item> ITEMS = new ArrayList<>();
    private static final List<Item> ORE_DICT_ITEMS = new ArrayList<>();

    public static final ItemBlock ITEM_WET_CONCRETE_SURFACE = registerBlock(BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
    // 原有物品
    public static final ItemLimePowder LIME_POWDER = register(new ItemLimePowder(), "dustLime");
    public static final ItemCementPowder CEMENT_POWDER = register(new ItemCementPowder(), "dustCement");
    public static final ItemIronPlate IRON_PLATE = register(new ItemIronPlate());
    public static final ItemLargeCrushingWheel LARGE_CRUSHING_WHEEL = register(new ItemLargeCrushingWheel());
    public static final ItemSmallCrushingWheel SMALL_CRUSHING_WHEEL = register(new ItemSmallCrushingWheel());
    public static final ItemCrushingAxle CRUSHING_AXLE = register(new ItemCrushingAxle());
    public static final ItemCompleteCrushingAxle COMPLETE_CRUSHING_AXLE = register(new ItemCompleteCrushingAxle());
    public static final ItemRawMixture RAW_MIXTURE = register(new ItemRawMixture());
    public static final ItemRefractoryBrick REFRACTORY_BRICK = register(new ItemRefractoryBrick());
    public static final ItemHammer HAMMER = register(new ItemHammer(), "toolHammer");
    public static final ItemTrowel TROWEL = register(new ItemTrowel(), "toolTrowel"); // 新增抹子
    public static final ItemWetConcreteBucket WET_CONCRETE_BUCKET = register(new ItemWetConcreteBucket());
    public static final ItemConcreteDust CONCRETE_DUST = register(new ItemConcreteDust());
    public static final ItemWoodPlate WOOD_PLATE = register(new ItemWoodPlate());
    public static final ItemCementBag CEMENT_BAG = register(new ItemCementBag(), "bagCement");

    // 方块物品
    public static final ItemBlock ITEM_CRACKED_HARDENED_CONCRETE = registerBlock(BlockRegistryHandler.BLOCK_CRACKED_HARDENED_CONCRETE);
    public static final ItemBlock ITEM_WET_CONCRETE = registerBlock(ModFluids.WET_CONCRETE_BLOCK);
    public static final ItemBlock ITEM_DEEP_CLAY = registerBlock(BlockRegistryHandler.BLOCK_DEEP_CLAY);
    public static final ItemBlock ITEM_LIMESTONE = registerBlock(BlockRegistryHandler.BLOCK_LIMESTONE);
    public static final ItemBlock ITEM_HARDENED_CONCRETE = registerBlock(BlockRegistryHandler.BLOCK_HARDENED_CONCRETE);
    public static final ItemBlock ITEM_STEEL_FRAME = registerBlock(BlockRegistryHandler.BLOCK_STEEL_FRAME);
    public static final ItemBlock ITEM_BLOCK_REFRACTORY_BRICK_BLOCK = registerBlock(BlockRegistryHandler.BLOCK_REFRACTORY_BRICK_BLOCK);
    public static final ItemBlock ITEM_REINFORCED_CONCRETE = registerBlock(BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE);
    public static final ItemBlock ITEM_STEEL_REBAR = registerBlock(BlockRegistryHandler.BLOCK_STEEL_REBAR);
    public static final ItemBlock ITEM_CONCRETE_CAULDRON = registerBlock(BlockRegistryHandler.BLOCK_CONCRETE_CAULDRON);

    // 机器方块物品
    public static final ItemBlock ITEM_CRUSHER = registerMachineBlock(BlockRegistryHandler.BLOCK_CRUSHER, "tooltip.cementmod.crusher_structure");
    public static final ItemBlock ITEM_ROTARY_KILN = registerMachineBlock(BlockRegistryHandler.BLOCK_ROTARY_KILN, "tooltip.cementmod.rotary_kiln_structure");
    public static final ItemBlock ITEM_CEMENT_MIXER = registerMachineBlock(BlockRegistryHandler.BLOCK_CEMENT_MIXER, "tooltip.cementmod.cement_mixer");
    public static final ItemBlock ITEM_CEMENT_PACKER = registerMachineBlock(BlockRegistryHandler.BLOCK_CEMENT_PACKER, "tooltip.cementmod.cement_packer");
    public static final ItemBlock ITEM_ITEM_EXTRACTOR = registerMachineBlock(BlockRegistryHandler.BLOCK_ITEM_EXTRACTOR, "tooltip.cementmod.item_extractor");

    // 框架物品
    public static final ItemBlock ITEM_CONCRETE_FRAME_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_EMPTY);
    public static final ItemBlock ITEM_CONCRETE_FRAME_WET = registerBlock(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_WET);
    public static final ItemBlock ITEM_CONCRETE_FRAME_SOLID = registerBlock(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_SOLID);
    public static final ItemBlock ITEM_CONCRETE_FRAME_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_DISMANTLED);
    public static final ItemBlock ITEM_16_16_4_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_4_EMPTY);
    public static final ItemBlock ITEM_16_16_4_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_4_WET);
    public static final ItemBlock ITEM_16_16_4_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_4_SOLID);
    public static final ItemBlock ITEM_16_16_4_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_4_DISMANTLED);
    public static final ItemBlock ITEM_16_16_6_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_6_EMPTY);
    public static final ItemBlock ITEM_16_16_6_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_6_WET);
    public static final ItemBlock ITEM_16_16_6_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_6_SOLID);
    public static final ItemBlock ITEM_16_16_6_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_6_DISMANTLED);
    public static final ItemBlock ITEM_16x16x8_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16x16x8_EMPTY);
    public static final ItemBlock ITEM_16x16x8_WET = registerBlock(BlockRegistryHandler.BLOCK_16x16x8_WET);
    public static final ItemBlock ITEM_16x16x8_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16x16x8_SOLID);
    public static final ItemBlock ITEM_16x16x8_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16x16x8_DISMANTLED);
    public static final ItemBlock ITEM_16_16_10_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_10_EMPTY);
    public static final ItemBlock ITEM_16_16_10_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_10_WET);
    public static final ItemBlock ITEM_16_16_10_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_10_SOLID);
    public static final ItemBlock ITEM_16_16_10_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_10_DISMANTLED);
    public static final ItemBlock ITEM_16_16_12_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_12_EMPTY);
    public static final ItemBlock ITEM_16_16_12_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_12_WET);
    public static final ItemBlock ITEM_16_16_12_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_12_SOLID);
    public static final ItemBlock ITEM_16_16_12_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_12_DISMANTLED);
    public static final ItemBlock ITEM_16_16_14_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_14_EMPTY);
    public static final ItemBlock ITEM_16_16_14_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_14_WET);
    public static final ItemBlock ITEM_16_16_14_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_14_SOLID);
    public static final ItemBlock ITEM_16_16_14_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_14_DISMANTLED);
    public static final ItemBlock ITEM_16_16_16_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_16_16_16_EMPTY);
    public static final ItemBlock ITEM_16_16_16_WET = registerBlock(BlockRegistryHandler.BLOCK_16_16_16_WET);
    public static final ItemBlock ITEM_16_16_16_SOLID = registerBlock(BlockRegistryHandler.BLOCK_16_16_16_SOLID);
    public static final ItemBlock ITEM_16_16_16_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_16_16_16_DISMANTLED);
    public static final ItemBlock ITEM_CEMENT_STAIR_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_EMPTY);
    public static final ItemBlock ITEM_CEMENT_STAIR_WET = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_WET);
    public static final ItemBlock ITEM_CEMENT_STAIR_SOLID = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_SOLID);
    public static final ItemBlock ITEM_CEMENT_STAIR_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_DISMANTLED);
    // 倒置混凝土楼梯物品
    public static final ItemBlock ITEM_INVERTED_CEMENT_STAIR_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_EMPTY);
    public static final ItemBlock ITEM_INVERTED_CEMENT_STAIR_WET = registerBlock(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_WET);
    public static final ItemBlock ITEM_INVERTED_CEMENT_STAIR_SOLID = registerBlock(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_SOLID);
    public static final ItemBlock ITEM_INVERTED_CEMENT_STAIR_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_DISMANTLED);
    // 倾斜水泥栏杆物品
    public static final ItemBlock ITEM_INCLINED_CEMENT_RAILING_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY);
    public static final ItemBlock ITEM_INCLINED_CEMENT_RAILING_WET = registerBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_WET);
    public static final ItemBlock ITEM_INCLINED_CEMENT_RAILING_SOLID = registerBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_SOLID);
    public static final ItemBlock ITEM_INCLINED_CEMENT_RAILING_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_DISMANTLED);
    public static final ItemBlock ITEM_END_OF_CEMENT_RAILING_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY);
    public static final ItemBlock ITEM_END_OF_CEMENT_RAILING_WET = registerBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_WET);
    public static final ItemBlock ITEM_END_OF_CEMENT_RAILING_SOLID = registerBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_SOLID);
    public static final ItemBlock ITEM_END_OF_CEMENT_RAILING_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_DISMANTLED);
    // 水泥柱物品
    public static final ItemBlock ITEM_CEMENT_PILLAR_EMPTY = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY);
    public static final ItemBlock ITEM_CEMENT_PILLAR_WET = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET);
    public static final ItemBlock ITEM_CEMENT_PILLAR_SOLID = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_SOLID);
    public static final ItemBlock ITEM_CEMENT_PILLAR_DISMANTLED = registerBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_DISMANTLED);


    // 在 ItemRegistryHandler.java 中添加以下字段

    // 注册辅助方法
    private static <T extends Item> T register(T item) {
        ITEMS.add(item);
        return item;
    }

    private static <T extends Item> T register(T item, String oreDictName) {
        ITEMS.add(item);
        ORE_DICT_ITEMS.add(item);
        return item;
    }

    private static ItemBlock registerBlock(net.minecraft.block.Block block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        ITEMS.add(itemBlock);
        return itemBlock;
    }

    private static ItemBlock registerMachineBlock(net.minecraft.block.Block block, String tooltip) {
        ItemBlock itemBlock = new ItemBlockMachine(block, tooltip);
        itemBlock.setRegistryName(block.getRegistryName());
        ITEMS.add(itemBlock);
        return itemBlock;
    }

    @SubscribeEvent
    public static void onRegistry(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // 注册所有物品
        for (Item item : ITEMS) {
            registry.register(item);
        }

        // 添加矿石词典
        for (Item item : ORE_DICT_ITEMS) {
            if (item == HAMMER) {
                OreDictionary.registerOre("toolHammer", item);
            } else if (item == LIME_POWDER) {
                OreDictionary.registerOre("dustLime", item);
            } else if (item == CEMENT_POWDER) {
                OreDictionary.registerOre("dustCement", item);
            } else if (item == CEMENT_BAG) {
                OreDictionary.registerOre("bagCement", item);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item) {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, modelResourceLocation);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        // 注册所有物品模型
        for (Item item : ITEMS) {
            registerModel(item);
        }
    }
}