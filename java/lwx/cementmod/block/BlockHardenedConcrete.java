package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockHardenedConcrete extends Block
{
    public BlockHardenedConcrete()
    {
        super(Material.ROCK);
        this.setUnlocalizedName(CementMod.MODID + ".hardenedconcrete");
        this.setRegistryName("hardened_concrete");
        this.setHarvestLevel("pickaxe",3);
        this.setHardness(2.0F);
        this.setResistance(12.0F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
