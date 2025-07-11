package com.luowenxuan.cementmod;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        CementMod.logger.info("ServerProxy preInit");
    }
}