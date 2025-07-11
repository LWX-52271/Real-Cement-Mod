package com.luowenxuan.cementmod;

import com.luowenxuan.cementmod.block.BlockRegistryHandler;
import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // 注册水泥合成器模型
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_MIXER), 0,
                new ModelResourceLocation("cementmod:cement_mixer", "inventory")
        );

        // 注册水泥打包机模型
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_PACKER), 0,
                new ModelResourceLocation("cementmod:cement_packer", "inventory")
        );

        // 注册取物器模型
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_ITEM_EXTRACTOR), 0,
                new ModelResourceLocation("cementmod:item_extractor", "inventory")
        );

        // 注册水泥袋模型
        ModelLoader.setCustomModelResourceLocation(
                ItemRegistryHandler.CEMENT_BAG, 0,
                new ModelResourceLocation("cementmod:cement_bag", "inventory")
        );

        // 注册其他模型...
    }
}