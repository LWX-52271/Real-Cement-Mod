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

public class BlockCementPillar extends Block {
    public enum PillarType {
        EMPTY, WET, SOLID, DISMANTLED
    }

    private final PillarType type;

    // 碰撞箱定义 (完整方块)
    protected static final AxisAlignedBB PILLAR_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockCementPillar(PillarType type) {
        super(type == PillarType.DISMANTLED ? Material.ROCK : Material.WOOD);
        this.type = type;

        // 设置创造模式物品栏 - 仅empty和dismantled状态可见
        if (type == PillarType.EMPTY || type == PillarType.DISMANTLED) {
            this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        }

        // 硬度和抗性设置
        if (type == PillarType.DISMANTLED) {
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
        this.setHarvestLevel(type == PillarType.DISMANTLED ? "pickaxe" : "axe", 0);

        String name = "cement_pillar_" + type.name().toLowerCase();
        this.setUnlocalizedName(CementMod.MODID + "." + name);
        this.setRegistryName(name);

        // 设置为透明方块，不剔除面
        this.useNeighborBrightness = true;
    }

    public PillarType getType() {
        return type;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return PILLAR_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn,
                                      boolean isActualState) {
        switch (type) {
            case EMPTY:
                // 柱子边缘碰撞箱 (根据模型调整)
                addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875));
                break;
            case WET:
            case SOLID:
            case DISMANTLED:
                // 完整碰撞箱
                addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR_AABB);
                break;
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false; // 设置为非完整方块，不剔除面
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; // 设置为非不透明方块，不剔除面
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false; // 禁用面剔除
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (type == PillarType.EMPTY) {
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
                int durabilityCost = 12; // 检查耐久度
                if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                    // 检查下方方块
                    BlockPos downPos = pos.down();
                    IBlockState downState = world.getBlockState(downPos);

                    // 如果下方是空柱子，则将下方变为湿混凝土
                    if (downState.getBlock() instanceof BlockCementPillar &&
                            ((BlockCementPillar) downState.getBlock()).getType() == PillarType.EMPTY) {
                        world.setBlockState(downPos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET.getDefaultState());
                        world.playSound(player, downPos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排下方方块的更新
                        world.scheduleUpdate(downPos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET, 1);
                    } else {
                        // 如果下方不是空柱子，则正常浇筑当前方块
                        world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET.getDefaultState());
                        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排当前方块的更新
                        world.scheduleUpdate(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET, 1);
                    }

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
        else if (type == PillarType.SOLID) {
            if (!heldItem.isEmpty() && heldItem.getItem().getToolClasses(heldItem).contains("axe")) {
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_DISMANTLED.getDefaultState());
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                if (!world.isRemote) {
                    spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY)));
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
        if (type == PillarType.WET) {
            // 检查下方方块
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            Block downBlock = downState.getBlock();

            // 如果下方是相同类型的空柱子
            if (downBlock instanceof BlockCementPillar && ((BlockCementPillar) downBlock).getType() == PillarType.EMPTY) {
                // 将下方柱子变成湿状态
                world.setBlockState(downPos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET.getDefaultState());

                // 将自身变回空柱子
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY.getDefaultState());

                // 播放混凝土流动声音
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                // 安排下方湿混凝土柱的更新
                world.scheduleUpdate(downPos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET, 1);
            } else {
                // 新增：检查下方方块是否是相同类型的湿或固体柱子，如果是，则不替换，继续硬化
                if (downBlock instanceof BlockCementPillar) {
                    PillarType downType = ((BlockCementPillar) downBlock).getType();
                    if (downType == PillarType.WET || downType == PillarType.SOLID) {
                        // 下方已经是湿或固体混凝土，不替换，继续硬化
                        if (rand.nextInt(90) < 1) {
                            world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_SOLID.getDefaultState());
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

                    // 将自身变回空柱子
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY.getDefaultState());

                    // 播放混凝土硬化声音
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    // 如果下方是完整方块，则继续硬化过程
                    if (rand.nextInt(90) < 1) {  // 修改为90秒内硬化
                        world.setBlockState(pos, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_SOLID.getDefaultState());
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
        // 当湿混凝土柱下方方块发生变化时，重新安排更新
        if (type == PillarType.WET && fromPos.equals(pos.down())) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (type == PillarType.WET) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return type == PillarType.SOLID ?
                Item.getItemFromBlock(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY) :
                super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return type == PillarType.SOLID ? 1 : 0;
    }
}