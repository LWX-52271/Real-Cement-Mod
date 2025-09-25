package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemConcreteDust extends Item {
    public ItemConcreteDust() {
        this.setUnlocalizedName(CementMod.MODID + ".concretedust");
        this.setRegistryName("concrete_dust");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
