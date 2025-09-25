package lwx.cementmod.client;

import lwx.cementmod.CommonProxy;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

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

        // 注册抹子模型
        ModelLoader.setCustomModelResourceLocation(
                ItemRegistryHandler.TROWEL, 0,
                new ModelResourceLocation("cementmod:trowel", "inventory")
        );
    }
}