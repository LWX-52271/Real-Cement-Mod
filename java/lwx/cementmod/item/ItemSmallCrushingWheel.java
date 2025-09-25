package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemSmallCrushingWheel extends Item
{
    public ItemSmallCrushingWheel ()
    {
        this.setUnlocalizedName(CementMod.MODID + ".smallcrushingwheel");
        this.setRegistryName("small_crushing_wheel");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
