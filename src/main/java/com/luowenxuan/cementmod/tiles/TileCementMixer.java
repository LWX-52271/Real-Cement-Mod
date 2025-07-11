package com.luowenxuan.cementmod.tiles;

import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileCementMixer extends TileEntity implements ITickable {

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private int mixTime;
    private int totalMixTime = 200; // 10秒混合时间

    @Override
    public void update() {
        if (world.isRemote) return;

        if (canMix()) {
            mixTime++;
            if (mixTime >= totalMixTime) {
                mixItems();
                mixTime = 0;
            }
        } else {
            mixTime = 0;
        }
    }

    private boolean canMix() {
        // 检查输入槽：0-石灰粉, 1-沙子, 2-水桶
        ItemStack limePowder = inventory.getStackInSlot(0);
        ItemStack sand = inventory.getStackInSlot(1);
        ItemStack waterBucket = inventory.getStackInSlot(2);
        ItemStack output = inventory.getStackInSlot(3);

        // 检查材料是否充足
        if (limePowder.getItem() != ItemRegistryHandler.LIME_POWDER || limePowder.getCount() < 1) return false;
        if (sand.getItem() != Item.getItemFromBlock(Blocks.SAND) || sand.getCount() < 2) return false;
        if (waterBucket.getItem() != Items.WATER_BUCKET) return false;

        // 检查输出槽是否有空间
        if (output.isEmpty()) return true;
        return output.getCount() < output.getMaxStackSize() &&
                output.getItem() == ItemRegistryHandler.WET_CONCRETE_BUCKET;
    }

    private void mixItems() {
        // 消耗材料
        inventory.extractItem(0, 1, false); // 石灰粉
        inventory.extractItem(1, 2, false); // 沙子

        // 将水桶替换为空桶
        inventory.setStackInSlot(2, new ItemStack(Items.BUCKET));

        // 产生湿混凝土桶
        ItemStack output = inventory.getStackInSlot(3);
        if (output.isEmpty()) {
            inventory.setStackInSlot(3, new ItemStack(ItemRegistryHandler.WET_CONCRETE_BUCKET, 1));
        } else {
            output.grow(1);
        }
    }

    // 添加缺失的方法
    public boolean isMixing() {
        return mixTime > 0;
    }

    public int getMixProgressScaled(int scale) {
        if (totalMixTime == 0) return 0;
        return mixTime * scale / totalMixTime;
    }

    // 数据保存与同步
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        mixTime = compound.getInteger("MixTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("MixTime", mixTime);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
    }
}