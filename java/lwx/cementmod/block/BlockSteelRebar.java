package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSteelRebar extends Block {
    public static final PropertyEnum<BarType> BAR_TYPE = PropertyEnum.create("bar_type", BarType.class);
    public static final PropertyEnum<PourType> POUR_TYPE = PropertyEnum.create("pour_type", PourType.class);

    public enum BarType implements IStringSerializable {
        ONE("one"), TWO("two"), THREE("three"), FOUR("four"), FOUR_BEGIRT("four_begirt");

        private final String name;

        BarType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum PourType implements IStringSerializable {
        EMPTY("empty"), WET("wet"), SOLID("solid");

        private final String name;

        PourType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public BlockSteelRebar() {
        super(Material.IRON);
        setRegistryName("steel_rebar");
        setUnlocalizedName(CementMod.MODID + ".steel_rebar");
        setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        setSoundType(SoundType.METAL);
        setHardness(3.0F);
        setResistance(10.0F);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(BAR_TYPE, BarType.ONE)
                .withProperty(POUR_TYPE, PourType.EMPTY));

        // 修复透明问题
        this.useNeighborBrightness = true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BAR_TYPE, POUR_TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BarType barType = state.getValue(BAR_TYPE);
        PourType pourType = state.getValue(POUR_TYPE);

        int barMeta = barType.ordinal() * 3;
        int pourMeta = pourType.ordinal();

        return barMeta + pourMeta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        BarType barType = BarType.values()[meta / 3];
        PourType pourType = PourType.values()[meta % 3];

        return this.getDefaultState()
                .withProperty(BAR_TYPE, barType)
                .withProperty(POUR_TYPE, pourType);
    }

    // 修复透明问题的方法
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getValue(POUR_TYPE) == PourType.SOLID;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(POUR_TYPE) == PourType.SOLID;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        // 只有固体状态时才阻挡渲染
        return state.getValue(POUR_TYPE) == PourType.SOLID;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (state.getValue(POUR_TYPE) == PourType.WET) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        if (state.getValue(POUR_TYPE) == PourType.WET && fromPos.equals(pos.down())) {
            world.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(POUR_TYPE) == PourType.WET) {
            BarType barType = state.getValue(BAR_TYPE);

            // 检查下方方块
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            Block downBlock = downState.getBlock();

            // 如果下方是相同类型的空钢筋
            if (downBlock instanceof BlockSteelRebar) {
                IBlockState downRebarState = (IBlockState) downState;
                BarType downBarType = downRebarState.getValue(BAR_TYPE);
                PourType downPourType = downRebarState.getValue(POUR_TYPE);

                if (downBarType == barType && downPourType == PourType.EMPTY) {
                    // 将下方钢筋变成湿状态
                    world.setBlockState(downPos, downRebarState.withProperty(POUR_TYPE, PourType.WET));

                    // 将自身变回空状态
                    world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.EMPTY));

                    // 播放混凝土流动声音
                    world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

                    // 安排下方湿钢筋的更新
                    world.scheduleUpdate(downPos, this, 1);
                    return;
                }
            }

            // 新增：检查下方方块是否是相同类型的湿或固体钢筋，如果是，则不替换，继续硬化
            if (downBlock instanceof BlockSteelRebar) {
                IBlockState downRebarState = (IBlockState) downState;
                BarType downBarType = downRebarState.getValue(BAR_TYPE);
                PourType downPourType = downRebarState.getValue(POUR_TYPE);

                if (downBarType == barType && (downPourType == PourType.WET || downPourType == PourType.SOLID)) {
                    // 下方已经是湿或固体钢筋，不替换，继续硬化
                    if (rand.nextInt(3600) < 5) {
                        world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.SOLID));
                        world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                    } else {
                        world.scheduleUpdate(pos, this, 20);
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

                // 将自身变回空状态
                world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.EMPTY));

                // 播放混凝土硬化声音
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
            } else {
                // 如果下方是完整方块，则继续硬化过程
                if (rand.nextInt(3600) < 5) {
                    world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.SOLID));
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                } else {
                    world.scheduleUpdate(pos, this, 20);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        BarType barType = state.getValue(BAR_TYPE);
        PourType pourType = state.getValue(POUR_TYPE);

        // 升级钢筋数量
        if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.ITEM_STEEL_REBAR) {
            BarType nextType = null;

            switch (barType) {
                case ONE:
                    nextType = BarType.TWO;
                    break;
                case TWO:
                    nextType = BarType.THREE;
                    break;
                case THREE:
                    nextType = BarType.FOUR;
                    break;
                case FOUR:
                case FOUR_BEGIRT:
                    // 已经是最高等级
                    break;
            }

            if (nextType != null) {
                world.setBlockState(pos, state.withProperty(BAR_TYPE, nextType));
                world.playSound(player, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                return true;
            }
        }

        // 使用空框架升级为带边框的钢筋
        if (barType == BarType.FOUR && pourType == PourType.EMPTY &&
                !heldItem.isEmpty() && heldItem.getItem() == Item.getItemFromBlock(BlockRegistryHandler.BLOCK_16_16_16_EMPTY)) {
            world.setBlockState(pos, state.withProperty(BAR_TYPE, BarType.FOUR_BEGIRT));
            world.playSound(player, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.capabilities.isCreativeMode) {
                heldItem.shrink(1);
            }
            return true;
        }

        // 浇筑湿混凝土 - 修改为使用耐久度系统
        if ((barType == BarType.FOUR_BEGIRT) && pourType == PourType.EMPTY &&
                !heldItem.isEmpty() && heldItem.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET) {
            int durabilityCost = 16; // 耐久度消耗值，设置为2的倍数

            // 检查耐久度是否足够
            if (heldItem.getItemDamage() + durabilityCost <= heldItem.getMaxDamage()) {
                // 检查下方方块
                BlockPos downPos = pos.down();
                IBlockState downState = world.getBlockState(downPos);

                // 如果下方是相同类型的空钢筋，则优先浇筑下方
                if (downState.getBlock() instanceof BlockSteelRebar) {
                    IBlockState downRebarState = (IBlockState) downState;
                    BarType downBarType = downRebarState.getValue(BAR_TYPE);
                    PourType downPourType = downRebarState.getValue(POUR_TYPE);

                    if (downBarType == barType && downPourType == PourType.EMPTY) {
                        world.setBlockState(downPos, downRebarState.withProperty(POUR_TYPE, PourType.WET));
                        world.playSound(player, downPos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排下方湿钢筋的更新
                        world.scheduleUpdate(downPos, this, 1);
                    } else {
                        // 如果下方不是空钢筋，则正常浇筑当前方块
                        world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.WET));
                        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // 安排当前方块的更新
                        world.scheduleUpdate(pos, this, 1);
                    }
                } else {
                    // 如果下方不是钢筋方块，则正常浇筑当前方块
                    world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.WET));
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // 安排当前方块的更新
                    world.scheduleUpdate(pos, this, 1);
                }

                // 消耗耐久度
                if (!player.capabilities.isCreativeMode) {
                    heldItem.damageItem(durabilityCost, player);
                }
                return true;
            } else {
                // 耐久度不足时播放提示音效
                world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.5F, 0.8F);
                return true;
            }
        }

        // 拆除凝固的混凝土 - 修改为变成强化混凝土并掉落空框架
        if (pourType == PourType.SOLID &&
                !heldItem.isEmpty() && heldItem.getItem().getToolClasses(heldItem).contains("axe")) {
            // 替换为强化混凝土方块
            world.setBlockState(pos, BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

            // 掉落空框架
            if (!world.isRemote) {
                spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(BlockRegistryHandler.BLOCK_16_16_16_EMPTY)));
            }

            if (!player.capabilities.isCreativeMode) {
                heldItem.damageItem(1, player);
            }
            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemRegistryHandler.ITEM_STEEL_REBAR;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // 确保放置时是空状态
        world.setBlockState(pos, state.withProperty(POUR_TYPE, PourType.EMPTY));
    }
}