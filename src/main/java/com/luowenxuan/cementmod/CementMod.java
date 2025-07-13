package com.luowenxuan.cementmod;

import com.luowenxuan.cementmod.container.*;
import com.luowenxuan.cementmod.events.BlockEventHandler;
import com.luowenxuan.cementmod.fluids.ModFluids;
import com.luowenxuan.cementmod.gui.*;
import com.luowenxuan.cementmod.integration.ModIntegration;
import com.luowenxuan.cementmod.item.ItemCementBag;
import com.luowenxuan.cementmod.tiles.*;
import com.luowenxuan.cementmod.world.AcidRainHandler;
import com.luowenxuan.cementmod.world.ModWorldGen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

@Mod(modid = CementMod.MODID, name = CementMod.NAME, version = CementMod.VERSION)
public class CementMod {
    public static final String MODID = "cementmod";
    public static final String NAME = "Cement Mod";
    public static final String VERSION = "1.0";
    public static CementMod instance;

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

    private static boolean acidRainEnabled = true;
    private static List<Integer> acidRainDimensions = Arrays.asList(0); // 默认主世界

    @SidedProxy(
            clientSide = "com.luowenxuan.cementmod.ClientProxy",
            serverSide = "com.luowenxuan.cementmod.ServerProxy"
    )
    public static CommonProxy proxy;

    public CementMod() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("Starting Cement Mod pre-initialization");

        GameRegistry.registerTileEntity(TileCementMixer.class, new ResourceLocation(MODID, "cement_mixer"));
        GameRegistry.registerTileEntity(TileCementPacker.class, new ResourceLocation(MODID, "cement_packer"));
        GameRegistry.registerTileEntity(TileItemExtractor.class, new ResourceLocation(MODID, "item_extractor"));

        proxy.preInit(event);

        ModFluids.register();

        GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new BlockEventHandler());

        GameRegistry.registerTileEntity(TileCrusher.class, new ResourceLocation(MODID, "crusher"));
        GameRegistry.registerTileEntity(TileRotaryKiln.class, new ResourceLocation(MODID, "rotary_kiln"));
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

    // ========== 酸雨配置方法 ==========
    public static boolean isAcidRainEnabled() {
        return acidRainEnabled;
    }

    public static void setAcidRainEnabled(boolean enabled) {
        acidRainEnabled = enabled;
    }

    public static boolean isAcidRainInDimension(int dimensionId) {
        // 检查维度是否有效存在
        if (DimensionManager.getWorld(dimensionId) == null) {
            logger.warn("Invalid dimension ID detected: " + dimensionId);
            return false;
        }

        return acidRainDimensions.contains(dimensionId);
    }

    public static void addAcidRainDimension(int dimensionId) {
        if (!acidRainDimensions.contains(dimensionId)) {
            acidRainDimensions.add(dimensionId);
            logger.info("Added dimension " + dimensionId + " to acid rain list");
        }
    }

    public static void removeAcidRainDimension(int dimensionId) {
        if (acidRainDimensions.contains(dimensionId)) {
            acidRainDimensions.remove(Integer.valueOf(dimensionId));
            logger.info("Removed dimension " + dimensionId + " from acid rain list");
        }
    }
} // 结束 CementMod 类