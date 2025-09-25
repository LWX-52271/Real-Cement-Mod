package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemRawMixture extends Item
{
    public ItemRawMixture()
    {
        this.setUnlocalizedName(CementMod.MODID + ".rawmixture");
        this.setRegistryName("raw_mixture");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
