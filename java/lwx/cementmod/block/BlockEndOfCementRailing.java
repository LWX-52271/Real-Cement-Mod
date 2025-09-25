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

public class BlockEndOfCementRailing extends Block {
    public enum EndRailingType {
        EMPTY, WET, SOLID, DISMANTLED
    }

    private final EndRailingType type;

    // 朝向属性
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    // 根据模型数据重新计算的碰撞箱
    // 模型坐标: [6.5, 0.01777, 13.46777] 到 [9.5, 16.01777, 15.96777]
    // 转换为0-1比例: x: 6.5/16=0.40625, 9.5/16=0.59375
    // z: 13.46777/16≈0.84174, 15.96777/16≈0.99799
    // y: 0.01777/16≈0.00111, 16.01777/16≈1.00111

    // 修复碰撞箱计算 - 使用正确的坐标转换
    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.40625, 0.00111, 0.84174, 0.59375, 1.00111, 0.99799);

    // 各朝向的碰撞箱 - 修复计算错误
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.40625, 0.00111, 0.00201, 0.59375, 1.00111, 0.15826);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.40625, 0.00111, 0.84174, 0.59375, 1.00111, 0.99799);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.00201, 0.00111, 0.40625, 0.15826, 1.00111, 0.59375);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.84174, 0.00111, 0.40625, 0.99799, 1.00111, 0.59375);

    public BlockEndOfCementRailing(EndRailingType type) {
        super(type == EndRailingType.DISMANTLED ? Material.ROCK : Material.WOOD);
        this.type = type;

        // 设置默认状态
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        // 设置创造模式物品栏 - 仅empty和dismantled状态可见
        if (type == EndRailingType.EMPTY || type == EndRailingType.DISMANTLED) {
            this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        }

        // 硬度和抗性设置
        if (type == EndRailingType.DISMANTLED) {
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
        this.setHarvestLevel(type == EndRailingType.DISMANTLED ? "pickaxe" : "axe", 0);

        String name = "end_of_cement_railing_" + type.name().toLowerCase();
        this.setUnlocalizedName(CementMod.MODID + "." + name);
        this.setRegistryName(name);
    }

    // 方块状态相关方法
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
        // 根据朝向返回对应的碰撞箱
        EnumFacing enumfacing = state.getValue(FACING);

        switch (enumfacing) {
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
            default:
                return EAST_AABB;
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn,
                                      boolean isActualState) {
        // 使用贴合模型的碰撞箱
        EnumFacing enumfacing = state.getValue(FACING);

        switch (enumfacing) {
            case NORTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
                break;
            case SOUTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
                break;
            case WEST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
                break;
            case EAST:
            default:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
                break;
        }
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

        if (type == EndRailingType.EMPTY) {
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
                // 末端栏杆消耗耐久度
                int durabilityCost = 4;

                // 检查耐久度是否足够
                if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                    // 保持原有的朝向
                    EnumFacing currentFacing = state.getValue(FACING);
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_WET.getDefaultState().withProperty(FACING, currentFacing));
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
        else if (type == EndRailingType.SOLID) {
            if (!heldItem.isEmpty() && heldItem.getItem().getToolClasses(heldItem).contains("axe")) {
                // 保持原有的朝向
                EnumFacing currentFacing = state.getValue(FACING);
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_DISMANTLED.getDefaultState().withProperty(FACING, currentFacing));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                if (!world.isRemote) {
                    spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY)));
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
        if (type == EndRailingType.WET) {
            // 获取当前湿栏杆的朝向
            EnumFacing currentFacing = state.getValue(FACING);

            // 检查下方方块
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            Block downBlock = downState.getBlock();

            // 如果下方是相同类型的空栏杆
            if (downBlock instanceof BlockEndOfCementRailing && ((BlockEndOfCementRailing) downBlock).type == EndRailingType.EMPTY) {
                // 将下方栏杆变成湿状态，保持朝向
                world.setBlockState(downPos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_WET.getDefaultState().withProperty(FACING, currentFacing));

                // 将自身变回空栏杆，保持朝向
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY.getDefaultState().withProperty(FACING, currentFacing));

                // 播放混凝土流动声音
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                // 安排下方湿混凝土栏杆的更新
                world.scheduleUpdate(downPos, this, 1);
            }
            // 检查下方是否是完整方块
            else if (downState.isFullCube()) {
                // 如果下方是完整方块，则硬化自身，保持朝向
                if (rand.nextInt(90) < 1) {
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_SOLID.getDefaultState().withProperty(FACING, currentFacing));
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    world.scheduleUpdate(pos, this, 40);
                }
            }
            else {
                // 如果下方不是完整方块，则替换下方方块为硬化混凝土
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
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY.getDefaultState().withProperty(FACING, currentFacing));

                    // 播放混凝土硬化声音
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                }
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        // 当湿混凝土栏杆下方方块发生变化时，重新安排更新
        if (type == EndRailingType.WET && fromPos.equals(pos.down())) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (type == EndRailingType.WET) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return type == EndRailingType.SOLID ?
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY) :
                super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == EndRailingType.SOLID ? 1 : 0;
    }
}