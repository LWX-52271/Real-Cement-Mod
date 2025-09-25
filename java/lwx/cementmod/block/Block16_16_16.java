package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

public class Block16_16_16 extends Block {
    public enum PlateType {
        EMPTY, WET, SOLID, DISMANTLED
    }

    private final PlateType type;

    // 碰撞箱定义 (高度16/16=1.0)
    protected static final AxisAlignedBB PLATE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public Block16_16_16(PlateType type) {
        super(type == PlateType.DISMANTLED ? Material.ROCK : Material.WOOD);
        this.type = type;

        // 设置创造模式物品栏 - 仅empty和dismantled状态可见
        if (type == PlateType.EMPTY || type == PlateType.DISMANTLED) {
            this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        }

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

        String name = "16_16_16_" + type.name().toLowerCase();
        this.setUnlocalizedName(CementMod.MODID + "." + name);
        this.setRegistryName(name);
    }

    public PlateType getType() {
        return type;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return PLATE_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn,
                                      boolean isActualState) {
        switch (type) {
            case EMPTY:
                // 边缘碰撞箱
                addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.03125));
                addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, 0.0, 0.96875, 1.0, 1.0, 1.0));
                addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, 0.0, 0.0, 0.03125, 1.0, 1.0));
                addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.96875, 0.0, 0.0, 1.0, 1.0, 1.0));
                break;
            case WET:
            case SOLID:
            case DISMANTLED:
                // 完整碰撞箱
                addCollisionBoxToList(pos, entityBox, collidingBoxes, PLATE_AABB);
                break;
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return type != PlateType.EMPTY;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return type != PlateType.EMPTY;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (type == PlateType.EMPTY) {
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
                int durabilityCost = 16;// 检查耐久度
                if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                    // 检查下方方块
                    BlockPos downPos = pos.down();
                    IBlockState downState = world.getBlockState(downPos);

                    // 如果下方是空框架，则将下方变为湿混凝土
                    if (downState.getBlock() instanceof Block16_16_16 &&
                            ((Block16_16_16) downState.getBlock()).getType() == PlateType.EMPTY) {
                        world.setBlockState(downPos, BlockRegistryHandler.BLOCK_16_16_16_WET.getDefaultState());
                        world.playSound(player, downPos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排下方方块的更新
                        world.scheduleUpdate(downPos, BlockRegistryHandler.BLOCK_16_16_16_WET, 1);
                    } else {
                        // 如果下方不是空框架，则正常浇筑当前方块
                        world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_WET.getDefaultState());
                        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排当前方块的更新
                        world.scheduleUpdate(pos, BlockRegistryHandler.BLOCK_16_16_16_WET, 1);
                    }

                    if (!player.capabilities.isCreativeMode) {
                        // 全高框架消耗16点耐久

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
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_DISMANTLED.getDefaultState());
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                if (!world.isRemote) {
                    spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_16_16_16_EMPTY)));
                }

                if (!player.capabilities.isCreativeMode) {
                    heldItem.damageItem(1, player);
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    // 其余代码保持不变...
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (type == PlateType.WET) {
            // 检查下方方块
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            Block downBlock = downState.getBlock();

            // 如果下方是相同类型的空框架
            if (downBlock instanceof Block16_16_16 && ((Block16_16_16) downBlock).getType() == PlateType.EMPTY) {
                // 将下方框架变成湿状态
                world.setBlockState(downPos, BlockRegistryHandler.BLOCK_16_16_16_WET.getDefaultState());

                // 将自身变回空框架
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_EMPTY.getDefaultState());

                // 播放混凝土流动声音
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                // 安排下方湿混凝土板的更新
                world.scheduleUpdate(downPos, BlockRegistryHandler.BLOCK_16_16_16_WET, 1);
            } else {
                // 新增：检查下方方块是否是相同类型的湿或固体框架，如果是，则不替换，继续硬化
                if (downBlock instanceof Block16_16_16) {
                    PlateType downType = ((Block16_16_16) downBlock).getType();
                    if (downType == PlateType.WET || downType == PlateType.SOLID) {
                        // 下方已经是湿或固体混凝土，不替换，继续硬化
                        if (rand.nextInt(90) < 1) {
                            world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_SOLID.getDefaultState());
                            world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                        } else {
                            world.scheduleUpdate(pos, this, 40);
                        }
                        return;
                    }
                }

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

                    // 将自身变回空框架
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_EMPTY.getDefaultState());

                    // 播放混凝土硬化声音
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    // 如果下方是完整方块，则继续硬化过程
                    if (rand.nextInt(90) < 1) {  // 修改为90秒内硬化
                        world.setBlockState(pos, BlockRegistryHandler.BLOCK_16_16_16_SOLID.getDefaultState());
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
        // 当湿混凝土板下方方块发生变化时，重新安排更新
        if (type == PlateType.WET && fromPos.equals(pos.down())) {
            world.scheduleUpdate(pos, this, 1);
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
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_16_16_16_EMPTY) :
                super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == PlateType.SOLID ? 1 : 0;
    }
}