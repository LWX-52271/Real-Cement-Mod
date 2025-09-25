package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockSteelFrame extends Block
{
    public BlockSteelFrame()
    {
        super(Material.IRON);
        this.setUnlocalizedName(CementMod.MODID + ".steelframe");
        this.setRegistryName("steel_frame");
        this.setHarvestLevel("pickaxe",2);
        this.setHardness(2.5F);
        this.setResistance(12.0F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setSoundType(SoundType.METAL);
    }
}
