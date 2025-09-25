package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.entities.EntityConcreteDebris;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.util.StructureIntegrity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockReinforcedConcrete extends Block
{
    // 添加自定义音效引用
    public static final SoundEvent COLLAPSE_SOUND = new SoundEvent(new ResourceLocation(CementMod.MODID, "block.reinforced_concrete.collapse"));
    // 添加坍塌状态标记
    private boolean isCollapsing = false;

    public BlockReinforcedConcrete()
    {
        super(Material.ROCK);
        this.setUnlocalizedName(CementMod.MODID + ".reinforcedconcrete");
        this.setRegistryName("reinforced_concrete");
        this.setHarvestLevel("pickaxe", 3);
        this.setHardness(3.0F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setResistance(30.0F);
        this.setSoundType(SoundType.STONE);
    }

    // 钢筋加固检查（六方向）
    public boolean isReinforced(IBlockAccess world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos checkPos = pos.offset(facing);
            IBlockState neighbor = world.getBlockState(checkPos);
            if (neighbor.getBlock() == BlockRegistryHandler.BLOCK_STEEL_REBAR) {
                return true;
            }
        }
        return false;
    }

    // 被破坏时触发结构检查
    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            StructureIntegrity.checkStructuralIntegrity(world, pos);
        }
        super.onBlockDestroyedByPlayer(world, pos, state);
    }

    // 爆炸破坏时触发
    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            StructureIntegrity.checkStructuralIntegrity(world, pos);
        }
        super.onBlockDestroyedByExplosion(world, pos, explosion);
    }

    // 随机刻更新（用于延迟坍塌）
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote && !isCollapsing) {
            // 添加支撑检查 - 如果下方有支撑，不坍塌
            if (!hasSupportBelow(world, pos)) {
                startCollapse(world, pos, state);
            }
        }
    }

    // 检查下方是否有支撑
    private boolean hasSupportBelow(World world, BlockPos pos) {
        BlockPos downPos = pos.down();
        IBlockState downState = world.getBlockState(downPos);

        // 下方是固体方块、钢筋或粘液块
        return downState.isSideSolid(world, downPos, EnumFacing.UP) ||
                downState.getBlock() == BlockRegistryHandler.BLOCK_STEEL_REBAR ||
                downState.getBlock() == Blocks.SLIME_BLOCK; // 添加粘液块作为支撑
    }

    // 开始坍塌过程
    private void startCollapse(World world, BlockPos pos, IBlockState state) {
        isCollapsing = true;
        // 1. 播放破坏音效和粒子
        world.playEvent(2001, pos, Block.getStateId(state));
        world.playSound(null, pos,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS, 1.0F, 0.8F); // 添加破坏音效

        // 2. 生成掉落实体
        if (!world.isRemote) {
            EntityConcreteDebris debris = new EntityConcreteDebris(
                    world,
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    state
            );
            world.spawnEntity(debris);
        }

        // 添加坍塌回响效果
        world.playSound(null, pos,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.BLOCKS, 0.5F, 0.9F);

        // 添加自定义坍塌音效
        world.playSound(null, pos,
                new SoundEvent(new ResourceLocation("cementmod:reinforced_collapse")),
                SoundCategory.BLOCKS, 1.2F, 0.7F);

        // 3. 移除原方块
        world.setBlockToAir(pos);

        // 新增: 触发上方所有方块崩塌
        if (!world.isRemote) {
            StructureIntegrity.collapseAboveBlocks(world, pos);
        }

        isCollapsing = false;
    }

    // 禁止植物生长
    @Override
    public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
                                   @Nonnull BlockPos pos, @Nonnull EnumFacing direction,
                                   @Nonnull IPlantable plantable) {
        return false;
    }

    // 提高爆炸抗性（当被加固时）
    @Override
    public float getExplosionResistance(Entity entity) {
        // 返回固定值（实际加固效果在事件处理器中实现）
        return 50.0F;
    }

    // 防止新放置方块立即更新
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        // 空实现，避免立即触发更新
    }
}