package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockRefractoryBrickBlock extends Block
{
    public BlockRefractoryBrickBlock()
    {
        super(Material.ROCK);
        this.setUnlocalizedName(CementMod.MODID + ".refractorybrickblock");
        this.setRegistryName("refractory_brick_block");
        this.setHarvestLevel("pickaxe",1);
        this.setHardness(2.0F);
        this.setResistance(15.0F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
