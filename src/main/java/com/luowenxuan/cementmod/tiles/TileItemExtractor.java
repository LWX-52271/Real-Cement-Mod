package com.luowenxuan.cementmod.tiles;

import com.luowenxuan.cementmod.CementMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileItemExtractor extends TileEntity implements ITickable {

    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private int extractedItems = 0; // 添加这个字段记录提取的物品数量

    // 更可靠的输出槽检测方法
    private boolean isOutputSlot(IItemHandler handler, int slot, EnumFacing side) {
        // 1. 检查是否可以提取物品
        ItemStack extracted = handler.extractItem(slot, 1, true);
        if (extracted.isEmpty()) {
            return false; // 无法提取物品，不是输出槽
        }

        // 2. 检查是否可以插入物品（使用更宽松的条件）
        ItemStack testInsert = extracted.copy();
        testInsert.setCount(1);
        ItemStack remaining = handler.insertItem(slot, testInsert, true);

        // 3. 如果能插入少于50%的物品，则认为是输出槽（允许部分机器有少量插入能力）
        return remaining.getCount() > testInsert.getCount() / 2;
    }

    @Override
    public void update() {
        if (world.isRemote || world.getTotalWorldTime() % 20 != 0) return;

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighborPos = pos.offset(facing);
            TileEntity neighbor = world.getTileEntity(neighborPos);

            if (neighbor != null && neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                IItemHandler neighborInventory = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

                // 调试信息：记录找到的机器
                CementMod.logger.info("发现相邻机器: " + neighbor.getClass().getSimpleName() + " 方向: " + facing);

                for (int i = 0; i < neighborInventory.getSlots(); i++) {
                    // 检查是否为输出槽
                    if (!isOutputSlot(neighborInventory, i, facing.getOpposite())) {
                        continue;
                    }

                    // 调试信息：记录找到的输出槽
                    CementMod.logger.info("  槽位 " + i + " 被识别为输出槽");

                    ItemStack stack = neighborInventory.extractItem(i, 1, true);
                    if (!stack.isEmpty()) {
                        for (int j = 0; j < inventory.getSlots(); j++) {
                            ItemStack remainder = inventory.insertItem(j, stack.copy(), false);
                            if (remainder.isEmpty()) {
                                neighborInventory.extractItem(i, 1, false);
                                extractedItems++;
                                markDirty();

                                // 调试信息：记录成功提取
                                CementMod.logger.info("成功提取物品: " + stack.getDisplayName());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    // 数据保存与同步
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        extractedItems = compound.getInteger("ExtractedItems");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("ExtractedItems", extractedItems);
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

    public String getStatus() {
        // 检查是否有相邻机器
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.getTileEntity(pos.offset(facing)) != null) {
                return "§a工作正常";
            }
        }
        return "§c未连接机器";
    }

    // 获取已提取物品数量
    public int getExtractedCount() {
        return extractedItems;
    }
}