package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockInclinedCementRailing extends Block {
    public enum RailingType {
        EMPTY, WET, SOLID, DISMANTLED
    }

    private final RailingType type;

    // 添加朝向属性
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    // 根据JSON模型定义的碰撞箱尺寸
    // 模型尺寸: x: 3/16=0.1875, z: 16/16=1.0, y: 24/16=1.5
    // 模型原点偏移: x: 8/16=0.5, z: 14.96777/16≈0.9355
    protected static final AxisAlignedBB BASE_COLLISION_AABB = new AxisAlignedBB(0.40625D, 0.0D, 0.0D, 0.59375D, 1.5D, 1.0D);

    // 选择框定义 (高度1.0格)
    protected static final AxisAlignedBB SELECTION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    // 为不同朝向定义的碰撞箱
    protected static final AxisAlignedBB NORTH_COLLISION_AABB = new AxisAlignedBB(0.40625D, 0.0D, 0.0D, 0.59375D, 1.5D, 1.0D);
    protected static final AxisAlignedBB SOUTH_COLLISION_AABB = new AxisAlignedBB(0.40625D, 0.0D, 0.0D, 0.59375D, 1.5D, 1.0D);
    protected static final AxisAlignedBB WEST_COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.40625D, 1.0D, 1.5D, 0.59375D);
    protected static final AxisAlignedBB EAST_COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.40625D, 1.0D, 1.5D, 0.59375D);

    public BlockInclinedCementRailing(RailingType type) {
        super(type == RailingType.DISMANTLED ? Material.ROCK : Material.WOOD);
        this.type = type;

        // 设置默认状态
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        // 设置创造模式物品栏 - 仅empty和dismantled状态可见
        if (type == RailingType.EMPTY || type == RailingType.DISMANTLED) {
            this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        }

        // 硬度和抗性设置
        if (type == RailingType.DISMANTLED) {
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
        this.setHarvestLevel(type == RailingType.DISMANTLED ? "pickaxe" : "axe", 0);

        String name = "inclined_cement_railing_" + type.name().toLowerCase();
        this.setUnlocalizedName(CementMod.MODID + "." + name);
        this.setRegistryName(name);
    }

    // 添加方块状态相关方法
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    // 设置方块放置时的朝向
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        // 使用与碰撞箱相同的边界框
        return getCollisionBoundingBox(state, source, pos);
    }

    // 重写选择框方法，使其高度为1格
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return SELECTION_AABB.offset(pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        // 根据朝向返回不同的碰撞箱
        EnumFacing facing = state.getValue(FACING);

        switch (facing) {
            case NORTH:
                return NORTH_COLLISION_AABB;
            case SOUTH:
                return SOUTH_COLLISION_AABB;
            case WEST:
                return WEST_COLLISION_AABB;
            case EAST:
                return EAST_COLLISION_AABB;
            default:
                return BASE_COLLISION_AABB;
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn,
                                      boolean isActualState) {
        // 根据朝向选择碰撞箱
        EnumFacing facing = state.getValue(FACING);
        AxisAlignedBB collisionAABB;

        switch (facing) {
            case NORTH:
                collisionAABB = NORTH_COLLISION_AABB;
                break;
            case SOUTH:
                collisionAABB = SOUTH_COLLISION_AABB;
                break;
            case WEST:
                collisionAABB = WEST_COLLISION_AABB;
                break;
            case EAST:
                collisionAABB = EAST_COLLISION_AABB;
                break;
            default:
                collisionAABB = BASE_COLLISION_AABB;
                break;
        }

        addCollisionBoxToList(pos, entityBox, collidingBoxes, collisionAABB);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (type == RailingType.EMPTY) {
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
                // 倾斜栏杆消耗更多耐久度
                int durabilityCost = 8; // 1.5格高的栏杆消耗8点耐久

                // 检查耐久度是否足够
                if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                    // 保持原有的朝向
                    EnumFacing currentFacing = state.getValue(FACING);
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_WET.getDefaultState().withProperty(FACING, currentFacing));
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!player.capabilities.isCreativeMode) {
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
        else if (type == RailingType.SOLID) {
            if (!heldItem.isEmpty() && heldItem.getItem().getToolClasses(heldItem).contains("axe")) {
                // 保持原有的朝向
                EnumFacing currentFacing = state.getValue(FACING);
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_DISMANTLED.getDefaultState().withProperty(FACING, currentFacing));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                if (!world.isRemote) {
                    spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY)));
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
        if (type == RailingType.WET) {
            // 检查下方方块
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            Block downBlock = downState.getBlock();

            // 获取当前湿栏杆的朝向
            EnumFacing currentFacing = state.getValue(FACING);

            // 如果下方是相同类型的空栏杆
            if (downBlock instanceof BlockInclinedCementRailing && ((BlockInclinedCementRailing) downBlock).type == RailingType.EMPTY) {
                // 将下方栏杆变成湿状态，保持朝向
                world.setBlockState(downPos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_WET.getDefaultState().withProperty(FACING, currentFacing));

                // 将自身变回空栏杆，保持朝向
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY.getDefaultState().withProperty(FACING, currentFacing));

                // 播放混凝土流动声音
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                // 安排下方湿混凝土栏杆的更新
                world.scheduleUpdate(downPos, this, 1);
            }
            // 检查下方是否是原版楼梯或水泥楼梯（dismantled状态）且方向相同
            else if ((downBlock instanceof net.minecraft.block.BlockStairs ||
                    (downBlock instanceof BlockCementStair && ((BlockCementStair) downBlock).type == BlockCementStair.PlateType.DISMANTLED)) &&
                    downState.getValue(BlockHorizontal.FACING) == currentFacing) {
                // 不替换下方方块，直接硬化自身
                if (rand.nextInt(90) < 1) {
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_SOLID.getDefaultState().withProperty(FACING, currentFacing));
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    world.scheduleUpdate(pos, this, 40);
                }
            }
            else {
                // 原有逻辑：检查下方方块是否可以替换为混凝土
                if (!downState.isFullCube() || downBlock.isAir(downState, world, downPos)) {
                    // 检查是否是容器（如箱子），如果是则清除内容
                    if (downBlock.hasTileEntity(downState)) {
                        TileEntity tileEntity = world.getTileEntity(downPos);
                        if (tileEntity instanceof IInventory) {
                            IInventory inventory = (IInventory) tileEntity;
                            // 清除容器内容
                            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                            }
                        }
                    }

                    // 替换下方方块为硬化混凝土
                    world.setBlockState(downPos, BlockRegistryHandler.BLOCK_HARDENED_CONCRETE.getDefaultState());

                    // 将自身变回空栏杆，保持朝向
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY.getDefaultState().withProperty(FACING, currentFacing));

                    // 播放混凝土硬化声音
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    // 如果下方是完整方块，则继续硬化过程，保持朝向
                    if (rand.nextInt(90) < 1) {
                        world.setBlockState(pos, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_SOLID.getDefaultState().withProperty(FACING, currentFacing));
                        world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                    } else {
                        // 每次更新间隔设置为40刻(2秒)，累计达到约90秒
                        world.scheduleUpdate(pos, this, 40);
                    }
                }
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        // 当湿混凝土栏杆下方方块发生变化时，重新安排更新
        if (type == RailingType.WET && fromPos.equals(pos.down())) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (type == RailingType.WET) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return type == RailingType.SOLID ?
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY) :
                super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == RailingType.SOLID ? 1 : 0;
    }
}