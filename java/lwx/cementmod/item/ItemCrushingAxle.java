package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemCrushingAxle extends Item
{
    public ItemCrushingAxle()
    {
        this.setUnlocalizedName(CementMod.MODID + ".crushingaxle");
        this.setRegistryName("crushing_axle");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
