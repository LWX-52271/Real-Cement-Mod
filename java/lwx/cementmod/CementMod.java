package lwx.cementmod;

import lwx.cementmod.block.BlockFluidWetConcrete;
import lwx.cementmod.block.BlockWetConcreteSurface;
import lwx.cementmod.command.CommandAcidRain;
import lwx.cementmod.container.*;
import lwx.cementmod.events.BlockEventHandler;
import lwx.cementmod.fluids.ModFluids;
import lwx.cementmod.gui.*;
import lwx.cementmod.handlers.ModEventHandler;
import lwx.cementmod.integration.ModIntegration;
import lwx.cementmod.item.ItemCementBag;
import lwx.cementmod.tiles.*;
import lwx.cementmod.world.AcidRainHandler;
import lwx.cementmod.world.ModWorldGen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = CementMod.MODID, name = CementMod.NAME, version = CementMod.VERSION)
public class CementMod {
    public static final String MODID = "cementmod";
    public static final String NAME = "Cement Mod";
    public static final String VERSION = "1.1.5";
    public static CementMod instance;

    public static final int CONCRETE_SETTING_TIME = 24000; // 20分钟（Minecraft一天）

    public static SoundEvent getCrusherSound() {
        return SoundRegistry.CRUSHER_OPERATING;
    }

    public static SoundEvent getConcretePourSound() {
        return SoundRegistry.CONCRETE_POUR;
    }

    public static SoundEvent getCollapseSound() {
        return SoundRegistry.REINFORCED_COLLAPSE;
    }

    public static final Logger logger = LogManager.getLogger(MODID);

    // 酸雨配置
    private static boolean acidRainEnabled = true;
    private static Map<Integer, Float> dimensionRainChances = new HashMap<>(); // 维度ID -> 酸雨概率
    private static float defaultRainChance = 0.1f; // 默认酸雨概率

    @SidedProxy(
            clientSide = "lwx.cementmod.client.ClientProxy",
            serverSide = "lwx.cementmod.ServerProxy"
    )
    public static CommonProxy proxy;

    public CementMod() {
        instance = this;
    }

    // 加载酸雨配置
    public static void loadAcidRainConfig() {
        acidRainEnabled = ModConfig.acidRainEnabled;
        defaultRainChance = ModConfig.defaultAcidRainChance;
        dimensionRainChances.clear();

        for (String entry : ModConfig.dimensionRainChances) {
            try {
                String[] parts = entry.split("=");
                if (parts.length == 2) {
                    int dimensionId = Integer.parseInt(parts[0].trim());
                    float chance = Float.parseFloat(parts[1].trim());
                    dimensionRainChances.put(dimensionId, chance);
                }
            } catch (NumberFormatException e) {
                logger.error("无法解析酸雨配置: " + entry, e);
            }
        }

        logger.info("酸雨配置已加载 - 启用: {}, 默认概率: {}",
                acidRainEnabled, defaultRainChance);
    }

    // ========== 酸雨配置方法 ==========
    public static boolean isAcidRainEnabled() {
        return acidRainEnabled;
    }

    public static void setAcidRainEnabled(boolean enabled) {
        acidRainEnabled = enabled;
    }

    public static float getAcidRainChance(int dimensionId) {
        // 如果有该维度的特定概率，返回该概率
        if (dimensionRainChances.containsKey(dimensionId)) {
            return dimensionRainChances.get(dimensionId);
        }
        // 否则返回默认概率
        return defaultRainChance;
    }

    public static void setDefaultAcidRainChance(float chance) {
        defaultRainChance = Math.max(0, Math.min(1, chance)); // 确保在0-1范围内
    }

    public static void setDimensionAcidRainChance(int dimensionId, float chance) {
        chance = Math.max(0, Math.min(1, chance)); // 确保在0-1范围内
        dimensionRainChances.put(dimensionId, chance);
        logger.info("Set acid rain chance for dimension {} to {}", dimensionId, chance);
    }

    public static void removeDimensionAcidRainChance(int dimensionId) {
        dimensionRainChances.remove(dimensionId);
        logger.info("Removed custom acid rain chance for dimension {}", dimensionId);
    }

    // 获取所有配置了酸雨概率的维度
    public static Set<Integer> getAcidRainDimensions() {
        return dimensionRainChances.keySet();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("Starting Cement Mod pre-initialization");

        GameRegistry.registerTileEntity(BlockFluidWetConcrete.WetConcreteTileEntity.class, new ResourceLocation(MODID, "wet_concrete_tile"));
        GameRegistry.registerTileEntity(TileCementMixer.class, new ResourceLocation(MODID, "cement_mixer"));
        GameRegistry.registerTileEntity(TileCementPacker.class, new ResourceLocation(MODID, "cement_packer"));
        GameRegistry.registerTileEntity(TileItemExtractor.class, new ResourceLocation(MODID, "item_extractor"));
        GameRegistry.registerTileEntity(TileCrusher.class, new ResourceLocation(MODID, "crusher"));
        GameRegistry.registerTileEntity(TileRotaryKiln.class, new ResourceLocation(MODID, "rotary_kiln"));
        GameRegistry.registerTileEntity(BlockWetConcreteSurface.TileEntityWetConcreteSurface.class, new ResourceLocation(MODID, "wet_concrete_surface"));

        proxy.preInit(event);

        ModFluids.register();

        GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new BlockEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());

        setDimensionAcidRainChance(0, 0.1f);
        loadAcidRainConfig();

    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAcidRain());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Starting Cement Mod initialization");
        proxy.init(event);

        MinecraftForge.EVENT_BUS.register(new AcidRainHandler());

        ModIntegration.checkLoadedMods();
        ModIntegration.integrate();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Cement Mod initialization complete");
        proxy.postInit(event);
    }

    // GUI处理器类
    public static class GuiHandler implements IGuiHandler {
        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            // 处理物品GUI（水泥袋）
            if (ID == 5) {
                EnumHand hand = x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                ItemStack heldItem = player.getHeldItem(hand);

                if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemCementBag) {
                    return new ContainerCementBag(player.inventory, heldItem);
                }
                return null;
            }

            BlockPos pos = new BlockPos(x, y, z);
            TileEntity te = world.getTileEntity(pos);

            if (te == null) return null;

            switch (ID) {
                case 0: // 破碎机
                    if (te instanceof TileCrusher) {
                        return new ContainerCrusher(player.inventory, (TileCrusher) te);
                    }
                    break;
                case 1: // 回转窑
                    if (te instanceof TileRotaryKiln) {
                        return new ContainerRotaryKiln(player.inventory, (TileRotaryKiln) te);
                    }
                    break;
                case 2: // 水泥合成器
                    if (te instanceof TileCementMixer) {
                        return new ContainerCementMixer(player.inventory, (TileCementMixer) te);
                    }
                    break;
                case 3: // 水泥打包机
                    if (te instanceof TileCementPacker) {
                        return new ContainerCementPacker(player.inventory, (TileCementPacker) te);
                    }
                    break;
                case 4: // 取物器
                    if (te instanceof TileItemExtractor) {
                        return new ContainerItemExtractor(player.inventory, (TileItemExtractor) te);
                    }
                    break;
            }
            return null;
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            // 处理物品GUI（水泥袋）
            if (ID == 5) {
                EnumHand hand = x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                ItemStack heldItem = player.getHeldItem(hand);

                if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemCementBag) {
                    return new GuiCementBag(player, heldItem);
                }
                return null;
            }

            BlockPos pos = new BlockPos(x, y, z);
            TileEntity te = world.getTileEntity(pos);

            if (te == null) return null;

            switch (ID) {
                case 0: // 破碎机GUI
                    if (te instanceof TileCrusher) {
                        return new GuiCrusher((TileCrusher) te, new ContainerCrusher(player.inventory, (TileCrusher) te));
                    }
                    break;
                case 1: // 回转窑GUI
                    if (te instanceof TileRotaryKiln) {
                        return new GuiRotaryKiln((TileRotaryKiln) te, new ContainerRotaryKiln(player.inventory, (TileRotaryKiln) te));
                    }
                    break;
                case 2: // 水泥合成器
                    if (te instanceof TileCementMixer) {
                        return new GuiCementMixer((TileCementMixer) te, new ContainerCementMixer(player.inventory, (TileCementMixer) te));
                    }
                    break;
                case 3: // 水泥打包机
                    if (te instanceof TileCementPacker) {
                        return new GuiCementPacker((TileCementPacker) te, new ContainerCementPacker(player.inventory, (TileCementPacker) te));
                    }
                    break;
                case 4: // 取物器
                    if (te instanceof TileItemExtractor) {
                        return new GuiItemExtractor((TileItemExtractor) te, new ContainerItemExtractor(player.inventory, (TileItemExtractor) te));
                    }
                    break;
            }
            return null;
        }
    } // 结束 GuiHandler 类
} // 结束 CementMod 类