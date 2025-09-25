package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.fluids.ModFluids;
import lwx.cementmod.registry.BlockRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockFluidWetConcrete extends BlockFluidClassic {
    // 凝固时间常量（最大90秒 = 1800游戏刻）
    public static final int MAX_SETTING_TIME = 1800;
    // 源方块额外凝固时间（增加50% = 2700游戏刻）
    public static final int SOURCE_BLOCK_EXTRA_TIME = 900;
    // 扩散间隔（15秒 = 300游戏刻）
    public static final int DIFFUSION_INTERVAL = 300;
    // 凝固检查间隔（5秒 = 100游戏刻）
    public static final int SETTING_CHECK_INTERVAL = 100;

    // 季节模组检测
    private static final boolean SERENE_SEASONS_LOADED = Loader.isModLoaded("sereneseasons");

    // 季节影响因子
    private static final float WINTER_FACTOR = 0.5f; // 冬季凝固速度减半
    private static final float SUMMER_FACTOR = 1.5f; // 夏季凝固速度加快50%

    // 海拔影响因子
    private static final float HIGH_ALTITUDE_FACTOR = 0.7f; // 高海拔凝固速度减慢
    private static final float LOW_ALTITUDE_FACTOR = 1.2f;  // 低海拔凝固速度加快

    // 湿度影响因子
    private static final float HIGH_HUMIDITY_FACTOR = 0.6f; // 高湿度凝固速度减慢
    private static final float LOW_HUMIDITY_FACTOR = 1.3f;  // 低湿度凝固速度加快

    // 窒息伤害源
    public static final DamageSource SUFFOCATE_DAMAGE = new DamageSource("wetConcreteSuffocate").setDamageBypassesArmor();

    public BlockFluidWetConcrete() {
        super(ModFluids.WET_CONCRETE, Material.WATER);
        setRegistryName("wet_concrete");
        setUnlocalizedName(CementMod.MODID + ".wetconcrete");
        setHardness(100.0F);

        // 设置渲染属性
        setLightOpacity(3);
        setLightLevel(0.0F);

        // 设置更新频率
        this.setTickRandomly(true);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new WetConcreteTileEntity();
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        // 获取TileEntity
        WetConcreteTileEntity tileEntity = (WetConcreteTileEntity) world.getTileEntity(pos);
        if (tileEntity == null) {
            // 如果没有TileEntity，创建一个新的
            tileEntity = new WetConcreteTileEntity();
            world.setTileEntity(pos, tileEntity);
            tileEntity.setPlacementTime(world.getTotalWorldTime());
            return;
        }

        // 如果是新创建的TileEntity，设置放置时间
        if (tileEntity.getPlacementTime() == 0) {
            tileEntity.setPlacementTime(world.getTotalWorldTime());
            return;
        }

        // 获取流体层级
        int fluidLevel = state.getValue(LEVEL);

        // 根据层级选择不同的框架方块
        Block targetBlock = getDismantledBlockForFluidLevel(fluidLevel);

        // 计算环境因子
        float environmentFactor = calculateEnvironmentFactor(world, pos);

        // 计算基础凝固时间
        int baseSettingTime = MAX_SETTING_TIME;

        // 如果是源方块（层级为0），增加额外凝固时间
        if (fluidLevel == 0) {
            baseSettingTime += SOURCE_BLOCK_EXTRA_TIME;
        }

        // 计算实际凝固时间
        int actualSettingTime = (int) (baseSettingTime / environmentFactor);

        // 计算时间因子 (0.0 - 1.0)
        long elapsedTime = world.getTotalWorldTime() - tileEntity.getPlacementTime();
        float timeFactor = Math.min(1.0f, (float) elapsedTime / actualSettingTime);

        // 当时间因子达到1.0时凝固
        if (timeFactor >= 1.0F) {
            solidifyConcrete(world, pos, targetBlock);
        } else {
            // 显示凝固进度粒子效果
            if (world.isRemote && rand.nextFloat() < 0.1F) {
                spawnSettingParticles(world, pos, rand, timeFactor);
            }

            // 继续安排更新，使用较短的间隔进行凝固检查
            world.scheduleUpdate(pos, this, SETTING_CHECK_INTERVAL);
        }
    }

    /**
     * 计算环境影响因子，考虑湿度、海拔和季节
     */
    private float calculateEnvironmentFactor(World world, BlockPos pos) {
        float factor = 1.0f; // 基础因子

        // 1. 湿度影响 - 检查周围水方块
        factor *= getHumidityFactor(world, pos);

        // 2. 海拔影响
        factor *= getAltitudeFactor(pos);

        // 3. 季节影响（如果安装了季节模组）
        if (SERENE_SEASONS_LOADED) {
            factor *= getSeasonFactor(world);
        }

        return factor;
    }

    /**
     * 根据周围水方块计算湿度影响因子
     */
    private float getHumidityFactor(World world, BlockPos pos) {
        int waterBlocks = 0;

        // 检查周围5x5x5区域内的水方块
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    IBlockState state = world.getBlockState(checkPos);

                    if (state.getMaterial() == Material.WATER && state.getBlock() != this) {
                        waterBlocks++;
                    }
                }
            }
        }

        // 根据水方块数量调整因子
        if (waterBlocks > 10) {
            return HIGH_HUMIDITY_FACTOR; // 高湿度减慢凝固
        } else if (waterBlocks < 3) {
            return LOW_HUMIDITY_FACTOR;  // 低湿度加快凝固
        }

        return 1.0f; // 正常湿度
    }

    /**
     * 根据海拔高度计算影响因子
     */
    private float getAltitudeFactor(BlockPos pos) {
        int y = pos.getY();

        if (y > 100) {
            return HIGH_ALTITUDE_FACTOR; // 高海拔减慢凝固
        } else if (y < 60) {
            return LOW_ALTITUDE_FACTOR;  // 低海拔加快凝固
        }

        return 1.0f; // 中等海拔
    }

    /**
     * 根据季节计算影响因子（需要Serene Seasons模组）
     */
    private float getSeasonFactor(World world) {
        try {
            // 使用反射获取季节信息，避免直接依赖
            Class<?> seasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
            Class<?> seasonState = Class.forName("sereneseasons.api.season.SeasonState");
            Class<?> season = Class.forName("sereneseasons.api.season.Season");

            // 获取当前季节状态
            Object seasonStateObj = seasonHelper.getMethod("getSeasonState", World.class).invoke(null, world);
            Object currentSeason = seasonStateObj.getClass().getMethod("getSeason").invoke(seasonStateObj);

            // 获取季节名称
            String seasonName = (String) currentSeason.getClass().getMethod("name").invoke(currentSeason);

            // 根据季节返回相应因子
            if (seasonName.contains("WINTER")) {
                return WINTER_FACTOR;
            } else if (seasonName.contains("SUMMER")) {
                return SUMMER_FACTOR;
            }
        } catch (Exception e) {
            // 如果季节模组API发生变化或不可用，使用默认值
            CementMod.logger.warn("Could not get season information: " + e.getMessage());
        }

        return 1.0f; // 默认因子
    }

    /**
     * 凝固湿混凝土
     */
    private void solidifyConcrete(World world, BlockPos pos, Block targetBlock) {
        // 硬化音效
        if (!world.isRemote) {
            world.playSound(null, pos,
                    SoundEvents.BLOCK_STONE_PLACE,
                    SoundCategory.BLOCKS, 0.8F, 1.0F);
            // 添加水泡音效
            world.playSound(null, pos,
                    SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.BLOCKS, 0.3F, 0.5F);

            // 根据流体层级设置不同的 dismantled 框架
            world.setBlockState(pos, targetBlock.getDefaultState());

            // 移除TileEntity
            world.removeTileEntity(pos);
        } else {
            // 客户端凝固效果
            spawnSolidificationParticles(world, pos);
        }
    }

    /**
     * 生成凝固进度粒子效果
     */
    private void spawnSettingParticles(World world, BlockPos pos, Random rand, float timeFactor) {
        // 根据凝固进度调整粒子数量
        int particleCount = (int) (5 * timeFactor) + 1;

        for (int i = 0; i < particleCount; i++) {
            double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5);
            double y = pos.getY() + 0.5 + (rand.nextDouble() - 0.5);
            double z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5);

            // 使用气泡粒子效果表示凝固过程
            world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, 0, 0.1, 0);
        }
    }

    /**
     * 生成凝固完成粒子效果
     */
    private void spawnSolidificationParticles(World world, BlockPos pos) {
        Random rand = world.rand;
        for (int i = 0; i < 10; i++) {
            double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 2;
            double y = pos.getY() + 0.5 + (rand.nextDouble() - 0.5) * 2;
            double z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 2;

            // 使用烟尘粒子效果表示凝固完成
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0.05, 0);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote) {
            // 减慢实体移动速度，模拟陷入湿混凝土
            entity.motionX *= 0.2;
            entity.motionZ *= 0.2;

            // 如果实体是生物，检查是否应该造成窒息伤害
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) entity;

                // 检查头部是否被淹没
                if (isHeadSubmerged(world, livingEntity)) {
                    // 造成窒息伤害（每20刻造成1点伤害，避免过于频繁）
                    if (world.getTotalWorldTime() % 20 == 0) {
                        livingEntity.attackEntityFrom(SUFFOCATE_DAMAGE, 1.0F);
                    }

                    // 播放窒息声音
                    if (world.rand.nextInt(20) == 0) {
                        world.playSound(null, pos,
                                SoundEvents.ENTITY_PLAYER_HURT,
                                SoundCategory.NEUTRAL, 0.5F, 0.8F);
                    }
                }
            }
        }
    }

    /**
     * 检查实体的头部是否被湿混凝土淹没
     */
    private boolean isHeadSubmerged(World world, EntityLivingBase entity) {
        // 获取头部位置（眼睛高度）
        double headY = entity.posY + entity.getEyeHeight();

        // 获取头部所在的方块位置
        BlockPos headPos = new BlockPos(entity.posX, headY, entity.posZ);

        // 检查头部位置的方块是否是湿混凝土
        IBlockState headBlockState = world.getBlockState(headPos);
        return headBlockState.getBlock() == this;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        // 返回空碰撞箱，使实体可以陷入湿混凝土
        return NULL_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        // 不添加任何碰撞箱，使实体可以陷入湿混凝土
    }

    /**
     * 根据流体层级返回对应的 dismantled 框架方块
     * @param fluidLevel 流体层级 (0-15)
     * @return 对应的 dismantled 框架方块
     */
    private Block getDismantledBlockForFluidLevel(int fluidLevel) {
        // 流体层级与框架高度的映射关系
        // 层级越低表示流体越多，应该凝固成更高的框架
        if (fluidLevel <= 1) { // 源方块或接近源方块
            return BlockRegistryHandler.BLOCK_16_16_16_DISMANTLED;
        } else if (fluidLevel <= 3) {
            return BlockRegistryHandler.BLOCK_16_16_14_DISMANTLED;
        } else if (fluidLevel <= 5) {
            return BlockRegistryHandler.BLOCK_16_16_12_DISMANTLED;
        } else if (fluidLevel <= 7) {
            return BlockRegistryHandler.BLOCK_16_16_10_DISMANTLED;
        } else if (fluidLevel <= 9) {
            return BlockRegistryHandler.BLOCK_16x16x8_DISMANTLED;
        } else if (fluidLevel <= 11) {
            return BlockRegistryHandler.BLOCK_16_16_6_DISMANTLED;
        } else if (fluidLevel <= 13) {
            return BlockRegistryHandler.BLOCK_16_16_4_DISMANTLED;
        } else { // 最高层级，流体最少
            return BlockRegistryHandler.BLOCK_CONCRETE_FRAME_DISMANTLED;
        }
    }

    @Override
    public int tickRate(World world) {
        return DIFFUSION_INTERVAL; // 返回15秒的间隔
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, neighborBlock, fromPos);

        // 所有湿混凝土方块都会安排更新
        world.scheduleUpdate(pos, this, SETTING_CHECK_INTERVAL);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);

        // 设置TileEntity的放置时间
        WetConcreteTileEntity tileEntity = (WetConcreteTileEntity) world.getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.setPlacementTime(world.getTotalWorldTime());
        }

        // 所有湿混凝土方块都会安排更新
        world.scheduleUpdate(pos, this, SETTING_CHECK_INTERVAL);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    private boolean isInWater(World world, BlockPos pos) {
        for (BlockPos checkPos : new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        }) {
            IBlockState neighborState = world.getBlockState(checkPos);
            Material material = neighborState.getMaterial();
            if (material == Material.WATER && neighborState.getBlock() != this) {
                return true;
            }
        }
        return false;
    }

    /**
     * TileEntity用于存储湿混凝土方块的放置时间
     */
    public static class WetConcreteTileEntity extends TileEntity {
        private long placementTime = 0;

        public long getPlacementTime() {
            return placementTime;
        }

        public void setPlacementTime(long placementTime) {
            this.placementTime = placementTime;
            markDirty();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            placementTime = compound.getLong("placementTime");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            compound.setLong("placementTime", placementTime);
            return compound;
        }
    }
}