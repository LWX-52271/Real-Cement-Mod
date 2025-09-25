package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockCementStair extends Block {
    public enum PlateType {
        EMPTY, WET, SOLID, DISMANTLED
    }

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    final PlateType type;

    // 楼梯碰撞箱定义
    protected static final AxisAlignedBB STAIR_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB STAIR_BOTTOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB STAIR_TOP_NORTH_AABB = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    protected static final AxisAlignedBB STAIR_TOP_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB STAIR_TOP_WEST_AABB = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D);
    protected static final AxisAlignedBB STAIR_TOP_EAST_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockCementStair(PlateType type) {
        super(type == PlateType.DISMANTLED ? Material.ROCK : Material.WOOD);
        this.type = type;

        // 设置创造模式物品栏 - 仅empty和dismantled状态可见
        if (type == PlateType.EMPTY || type == PlateType.DISMANTLED) {
            this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        }

        // 设置默认状态
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        // 硬度和抗性设置
        if (type == PlateType.DISMANTLED) {
            this.setHardness(2.0F);
            this.setResistance(10.0F);
        } else {
            this.setHardness(1.5F);
            this.setResistance(5.0F);
        }

        // 音效设置
        switch (type) {
            case EMPTY:
                this.setSoundType(SoundType.WOOD);
                break;
            case WET:
                this.setSoundType(new SoundType(1.0F, 1.0F,
                        SoundEvents.BLOCK_SLIME_PLACE,
                        SoundEvents.BLOCK_SLIME_STEP,
                        SoundEvents.BLOCK_SLIME_PLACE,
                        SoundEvents.BLOCK_SLIME_HIT,
                        SoundEvents.BLOCK_SLIME_FALL
                ));
                break;
            case SOLID:
            case DISMANTLED:
                this.setSoundType(SoundType.STONE);
                break;
        }

        // 挖掘工具设置
        this.setHarvestLevel(type == PlateType.DISMANTLED ? "pickaxe" : "axe", 0);

        String name = "cement_stair_" + type.name().toLowerCase();
        this.setUnlocalizedName(CementMod.MODID + "." + name);
        this.setRegistryName(name);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta);
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        // 所有状态都使用楼梯型边界框
        return STAIR_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn,
                                      boolean isActualState) {
        // 所有状态都使用楼梯型碰撞箱
        EnumFacing facing = state.getValue(FACING);

        // 添加底部碰撞箱
        addCollisionBoxToList(pos, entityBox, collidingBoxes, STAIR_BOTTOM_AABB);

        // 根据朝向添加上部碰撞箱 - 修复方向问题
        switch (facing) {
            case NORTH:
                // 朝北的楼梯，完整部分在南侧
                addCollisionBoxToList(pos, entityBox, collidingBoxes, STAIR_TOP_SOUTH_AABB);
                break;
            case SOUTH:
                // 朝南的楼梯，完整部分在北侧
                addCollisionBoxToList(pos, entityBox, collidingBoxes, STAIR_TOP_NORTH_AABB);
                break;
            case WEST:
                // 朝西的楼梯，完整部分在东侧
                addCollisionBoxToList(pos, entityBox, collidingBoxes, STAIR_TOP_EAST_AABB);
                break;
            case EAST:
                // 朝东的楼梯，完整部分在西侧
                addCollisionBoxToList(pos, entityBox, collidingBoxes, STAIR_TOP_WEST_AABB);
                break;
            default:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false; // 所有状态都不是完整方块
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; // 所有状态都不是不透明方块
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (type == PlateType.EMPTY) {
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
                int durabilityCost = 12;
                if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                    // 修复：保留原有朝向
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_STAIR_WET.getDefaultState().withProperty(FACING, state.getValue(FACING)));
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!player.capabilities.isCreativeMode) {
                        // 根据框架高度消耗不同耐久度
                        heldItem.damageItem(durabilityCost, player);
                    }
                    return true;
                } else {
                    // 桶已耗尽，播放空桶声音
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.5F, 0.8F);
                    return true;
                }
            }
        }
        else if (type == PlateType.SOLID) {
            if (!heldItem.isEmpty() && heldItem.getItem().getToolClasses(heldItem).contains("axe")) {
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_STAIR_DISMANTLED.getDefaultState().withProperty(FACING, state.getValue(FACING)));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                if (!world.isRemote) {
                    spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_EMPTY)));
                }

                if (!player.capabilities.isCreativeMode) {
                    heldItem.damageItem(1, player);
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (type == PlateType.WET) {
            // 直接硬化自身，不处理下方方块
            if (rand.nextInt(90) < 1) {  // 修改为90秒内硬化
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_STAIR_SOLID.getDefaultState().withProperty(FACING, state.getValue(FACING)));
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
            } else {
                // 每次更新间隔设置为40刻(2秒)，累计达到约90秒
                world.scheduleUpdate(pos, this, 40);
            }
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (type == PlateType.WET) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return type == PlateType.SOLID ?
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_STAIR_EMPTY) :
                super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == PlateType.SOLID ? 1 : 0;
    }
}