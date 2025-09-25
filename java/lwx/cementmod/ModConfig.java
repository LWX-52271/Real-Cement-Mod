package lwx.cementmod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = CementMod.MODID)
@Config.LangKey("cementmod.config.title")
public class ModConfig {
    @Config.Comment("是否启用酸雨系统")
    public static boolean acidRainEnabled = true;

    @Config.Comment("默认酸雨概率 (0.0 - 1.0)")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static float defaultAcidRainChance = 0.1f;

    @Config.Comment("特定维度的酸雨概率 (格式: 维度ID=概率)")
    public static String[] dimensionRainChances = {"0=0.1"};

    @Mod.EventBusSubscriber
    public static class ConfigHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(CementMod.MODID)) {
                // 重新加载配置
                ConfigManager.sync(CementMod.MODID, Config.Type.INSTANCE);
                CementMod.loadAcidRainConfig();
            }
        }
    }
}