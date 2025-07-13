package com.luowenxuan.cementmod.item;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.item.Item;

public class ItemConcreteDust extends Item {
    public ItemConcreteDust() {
        this.setUnlocalizedName(CementMod.MODID + ".concretedust");
        this.setRegistryName("concrete_dust");
        this.setMaxStackSize(64);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }
}
