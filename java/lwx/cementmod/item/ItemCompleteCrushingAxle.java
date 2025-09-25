package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemCompleteCrushingAxle extends Item
{
    public ItemCompleteCrushingAxle()
    {
        this.setUnlocalizedName(CementMod.MODID + ".completecrushingaxle");
        this.setRegistryName("complete_crushing_axle");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
