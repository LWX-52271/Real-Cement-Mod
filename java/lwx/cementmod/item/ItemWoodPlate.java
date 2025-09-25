package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemWoodPlate extends Item
{
    public ItemWoodPlate()
    {
        this.setUnlocalizedName(CementMod.MODID + ".woodplate");
        this.setRegistryName("wood_plate");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
