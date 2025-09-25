package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrackedHardenedConcrete extends Block {

    public BlockCrackedHardenedConcrete() {
        super(Material.ROCK);
        this.setUnlocalizedName(CementMod.MODID + ".cracked_hardened_concrete");
        this.setRegistryName("cracked_hardened_concrete");
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setHardness(2.0F); // 比硬化混凝土稍软，便于修复
        this.setResistance(8.0F); // 抗爆性稍低
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 0); // 可以用任何镐挖掘
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }
}