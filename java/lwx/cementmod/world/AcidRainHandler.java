package lwx.cementmod.world;

import lwx.cementmod.CementMod;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.HashMap;
import java.util.Map;

public class AcidRainHandler {
    private static final Map<Integer, Boolean> commandTriggeredRain = new HashMap<>();

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        World world = event.world;
        if (world.isRemote || world.getWorldTime() % 200 != 0) return;

        int dimension = world.provider.getDimension();
        float rainChance = CementMod.getAcidRainChance(dimension);

        // 检查酸雨是否启用且当前维度有酸雨概率
        if (CementMod.isAcidRainEnabled() && rainChance > 0) {
            boolean isCommandTriggered = commandTriggeredRain.getOrDefault(dimension, false);

            float actualChance = 0;
            if ((world.isRaining() && world.rand.nextFloat() < actualChance) || isCommandTriggered) {
                world.getWorldInfo().setRaining(true);
                world.getWorldInfo().setThundering(true);

                // 如果是命令触发的，重置标志
                if (isCommandTriggered) {
                    commandTriggeredRain.put(dimension, false);
                }
            }

            // 获取玩家出生点温度
            BlockPos spawnPos = world.getSpawnPoint();
            float temperature = world.getBiome(spawnPos).getTemperature(spawnPos);

            // 炎热生物群系增加酸雨概率 (温度 > 0.5)
            float temperatureFactor = temperature > 0.5F ? 1.5f : 1.0f;

            // 计算实际酸雨概率
            actualChance = rainChance * temperatureFactor;

            // 如果正在下雨，有概率转为酸雨
            if (world.isRaining() && world.rand.nextFloat() < actualChance) {
                world.getWorldInfo().setRaining(true);
                world.getWorldInfo().setThundering(true);
            }

            // 添加酸雨音效
            if (world.isRaining() && world.rand.nextInt(100) == 0) {
                BlockPos randomPos = spawnPos.add(
                        world.rand.nextInt(100) - 50,
                        0,
                        world.rand.nextInt(100) - 50
                );
                world.playSound(null, randomPos,
                        net.minecraft.init.SoundEvents.WEATHER_RAIN_ABOVE,
                        SoundCategory.WEATHER, 1.0F, 0.7F);

                // 添加腐蚀音效
                if (world.rand.nextInt(5) == 0) {
                    world.playSound(null, randomPos,
                            net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH,
                            SoundCategory.AMBIENT, 0.3F, 0.5F);
                }
            }
        }
    }

    // 添加一个静态方法来设置命令触发的酸雨
    public static void setCommandTriggeredRain(int dimensionId) {
        commandTriggeredRain.put(dimensionId, true);
    }
}