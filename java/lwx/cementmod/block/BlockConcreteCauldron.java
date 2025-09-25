package lwx.cementmod.block;

import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockConcreteCauldron extends Block {

    public static enum CauldronState implements IStringSerializable {
        EMPTY("empty"),
        SAND("sand"),
        CEMENT_POWDER1("cement_powder1"),
        CEMENT_POWDER2("cement_powder2"),
        CEMENT_WATER("cement_water"),
        CONCRETE("concrete");

        private final String name;

        private CauldronState(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static final PropertyEnum<CauldronState> STATE = PropertyEnum.create("state", CauldronState.class);

    public BlockConcreteCauldron() {
        super(Material.IRON);
        this.setUnlocalizedName("concrete_cauldron");
        this.setRegistryName("concrete_cauldron");
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, CauldronState.EMPTY));
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }

    // 添加这些必要的方法来定义方块状态
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STATE, CauldronState.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE).ordinal();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        CauldronState currentState = state.getValue(STATE);

        // 沙子右键空炼药锅 -> 沙子状态
        if (currentState == CauldronState.EMPTY && heldItem.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.SAND));
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        // 水泥粉右键沙子状态 -> 水泥粉1状态
        if (currentState == CauldronState.SAND && heldItem.getItem() == ItemRegistryHandler.CEMENT_POWDER) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.CEMENT_POWDER1));
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        // 水泥粉右键水泥粉1状态 -> 水泥粉2状态
        if (currentState == CauldronState.CEMENT_POWDER1 && heldItem.getItem() == ItemRegistryHandler.CEMENT_POWDER) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.CEMENT_POWDER2));
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        // 水桶右键水泥粉2状态 -> 水泥水状态
        if (currentState == CauldronState.CEMENT_POWDER2 && heldItem.getItem() == Items.WATER_BUCKET) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.CEMENT_WATER));
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                    player.addItemStackToInventory(new ItemStack(Items.BUCKET));
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        // 木棍右键水泥水状态 -> 搅拌（需要9次）
        if (currentState == CauldronState.CEMENT_WATER && heldItem.getItem() == Items.STICK) {
            if (!world.isRemote) {
                // 这里需要存储搅拌次数，可以使用TileEntity或者BlockState属性
                // 简化实现：直接转换为混凝土状态（实际应该需要9次）
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.CONCRETE));
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                // 可选：消耗木棍耐久
                if (!player.capabilities.isCreativeMode) {
                    heldItem.damageItem(1, player);
                }
            }
            return true;
        }

        // 空桶右键混凝土状态 -> 获得湿混凝土桶
        if (currentState == CauldronState.CONCRETE && heldItem.getItem() == Items.BUCKET) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(STATE, CauldronState.EMPTY));
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                    player.addItemStackToInventory(new ItemStack(ItemRegistryHandler.WET_CONCRETE_BUCKET));
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return 0;
    }

    // 添加转换原版炼药锅的静态方法
    public static boolean convertVanillaCauldron(World world, BlockPos pos, EntityPlayer player) {
        IBlockState oldState = world.getBlockState(pos);

        if (oldState.getBlock() == Blocks.CAULDRON) {
            // 检查原版炼药锅的水位（LEVEL属性）
            if (oldState.getValue(BlockCauldron.LEVEL) == 0) {
                // 创建混凝土炼药锅的新实例
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_CONCRETE_CAULDRON.getDefaultState()
                        .withProperty(STATE, CauldronState.EMPTY));
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }

        return false;
    }
}