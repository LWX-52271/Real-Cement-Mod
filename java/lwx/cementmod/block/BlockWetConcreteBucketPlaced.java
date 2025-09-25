package lwx.cementmod.block;

import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.item.ItemTrowel;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWetConcreteBucketPlaced extends Block {
    public static final PropertyInteger DURABILITY = PropertyInteger.create("durability", 0, 8);

    public BlockWetConcreteBucketPlaced() {
        super(Material.IRON);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.METAL);
        this.setRegistryName("wet_concrete_bucket_placed");
        this.setUnlocalizedName("wet_concrete_bucket_placed");
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DURABILITY, 8)); // 初始状态为满 (8)
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{DURABILITY});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DURABILITY, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DURABILITY);
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        // 原有的潜行拾取功能
        if (player.isSneaking() && hand == EnumHand.MAIN_HAND) {
            if (!world.isRemote) {
                int durabilityState = state.getValue(DURABILITY);
                // 计算物品对应的耐久度 (将0-8的状态映射到0-16的损伤值)
                int itemDamage = (8 - durabilityState) * 2; // 0满 -> 0损伤, 8空 -> 16损伤

                ItemStack bucketStack = new ItemStack(ItemRegistryHandler.WET_CONCRETE_BUCKET);
                bucketStack.setItemDamage(itemDamage);

                if (!player.inventory.addItemStackToInventory(bucketStack)) {
                    player.dropItem(bucketStack, false);
                }
                world.setBlockToAir(pos);
            }
            return true;
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        // 从堆栈的元数据获取耐久度状态
        int durabilityState = stack.getMetadata();
        if (durabilityState > 0) {
            tooltip.add(I18n.format("tooltip.cementmod.has_wet_cement"));
        } else {
            tooltip.add(I18n.format("tooltip.cementmod.cement_used"));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
}