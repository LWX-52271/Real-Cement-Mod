package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemLargeCrushingWheel extends Item
{
    public ItemLargeCrushingWheel ()
    {
        this.setUnlocalizedName(CementMod.MODID + ".largecrushingwheel");
        this.setRegistryName("large_crushing_wheel");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
