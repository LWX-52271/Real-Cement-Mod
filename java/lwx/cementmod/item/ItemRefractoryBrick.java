package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemRefractoryBrick extends Item
{
    public ItemRefractoryBrick()
    {
        this.setUnlocalizedName(CementMod.MODID + ".refractorybrick");
        this.setRegistryName("refractory_brick");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}

