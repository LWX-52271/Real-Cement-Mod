package lwx.cementmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // 服务端预初始化
        CementMod.logger.info("CommonProxy preInit");

    }

    public void init(FMLInitializationEvent event) {
        // 服务端初始化
        CementMod.logger.info("CommonProxy init");
    }

    public void postInit(FMLPostInitializationEvent event) {
        // 服务端后初始化
        CementMod.logger.info("CommonProxy postInit");
    }
}