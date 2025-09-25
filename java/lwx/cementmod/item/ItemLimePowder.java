package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemLimePowder extends Item
{
    public ItemLimePowder ()
    {
        this.setUnlocalizedName(CementMod.MODID + ".limepowder");
        this.setRegistryName("lime_powder");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
