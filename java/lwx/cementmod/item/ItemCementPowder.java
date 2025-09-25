package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemCementPowder extends Item
{
    public ItemCementPowder ()
    {
        this.setUnlocalizedName(CementMod.MODID + ".cementpowder");
        this.setRegistryName("cement_powder");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
