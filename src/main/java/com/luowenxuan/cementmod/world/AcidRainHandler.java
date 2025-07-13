package com.luowenxuan.cementmod.world;

import com.luowenxuan.cementmod.CementMod;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class AcidRainHandler {
    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        World world = event.world;
        if (world.isRemote || world.getWorldTime() % 200 != 0) return;

        // 在炎热生物群系有10%几率触发酸雨
        if (world.isRaining() && world.rand.nextFloat() < 0.1F) {
            // 修复：添加world参数
            for (Biome biome : world.getBiomeProvider().getBiomesToSpawnIn()) {
                // 获取生物群系温度（需要世界坐标）
                float temperature = biome.getTemperature(new BlockPos(world.getSpawnPoint()));

                if (temperature > 0.5F) {
                    world.getWorldInfo().setRaining(true);
                    world.getWorldInfo().setThundering(true);
                    break;
                }
            }
        }

        // 添加酸雨音效
        if (world.isRaining() && CementMod.isAcidRainInDimension(world.provider.getDimension())) {
            if (world.rand.nextInt(100) == 0) {
                BlockPos randomPos = world.getSpawnPoint().add(
                        world.rand.nextInt(100) - 50,
                        0,
                        world.rand.nextInt(100) - 50
                );
                world.playSound(null, randomPos,
                        SoundEvents.WEATHER_RAIN_ABOVE,
                        SoundCategory.WEATHER, 1.0F, 0.7F);

                // 添加腐蚀音效
                if (world.rand.nextInt(5) == 0) {
                    world.playSound(null, randomPos,
                            SoundEvents.BLOCK_FIRE_EXTINGUISH,
                            SoundCategory.AMBIENT, 0.3F, 0.5F);
                }
            }
        }
    }
}
