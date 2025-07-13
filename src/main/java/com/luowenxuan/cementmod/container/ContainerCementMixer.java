package com.luowenxuan.cementmod.container;

import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import com.luowenxuan.cementmod.tiles.TileCementMixer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCementMixer extends Container {
    private final TileCementMixer tileEntity;
    private int lastMixTime;
    private int mixProgress; // 添加进度存储

    public ContainerCementMixer(InventoryPlayer playerInv, TileCementMixer te) {
        this.tileEntity = te;
        IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // 输入槽
        addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 17) { // 石灰粉
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ItemRegistryHandler.LIME_POWDER;
            }
        });

        addSlotToContainer(new SlotItemHandler(inventory, 1, 56, 53) { // 沙子
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Item.getItemFromBlock(Blocks.SAND);
            }
        });

        addSlotToContainer(new SlotItemHandler(inventory, 2, 38, 35) { // 水桶
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.WATER_BUCKET;
            }
        });

        // 输出槽
        addSlotToContainer(new SlotItemHandler(inventory, 3, 116, 35) { // 湿混凝土桶
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false; // 输出槽不能放入物品
            }
        });

        // 玩家物品栏 (9x3)
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // 玩家快捷栏 (9x1)
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.getDistanceSq(player.posX, player.posY, player.posZ) <= 64.0D;
    }

    // 同步数据到客户端
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // 检查进度值是否变化
        int currentMixTime = tileEntity.getMixProgressScaled(100);

        if (lastMixTime != currentMixTime) {
            // 更新所有监听器
            for (IContainerListener listener : this.listeners) {
                listener.sendWindowProperty(this, 0, currentMixTime);
            }
            lastMixTime = currentMixTime;
        }
    }

    // 从客户端接收同步数据
    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            this.mixProgress = data; // 保存进度值
        }
    }

    // 获取混合进度（用于GUI渲染）
    public int getMixProgressScaled(int scale) {
        if (scale == 0) return 0; // 防止除以零
        return mixProgress * scale / 100;
    }

    // 物品转移逻辑 - 修复版
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = 4; // 机器槽位数量
            int playerSlots = containerSlots + 36; // 玩家槽位总数

            // 从机器槽位移动到玩家背包
            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, playerSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            // 玩家背包 -> 机器输入槽
            else if (index >= containerSlots) {
                // 尝试放入石灰粉槽
                if (itemstack1.getItem() == ItemRegistryHandler.LIME_POWDER) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 尝试放入沙子槽
                else if (itemstack1.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 尝试放入水桶槽
                else if (itemstack1.getItem() == Items.WATER_BUCKET) {
                    if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 其他物品：在玩家背包内部转移
                else {
                    // 从快捷栏移动到主物品栏
                    if (index < containerSlots + 27) {
                        if (!this.mergeItemStack(itemstack1, containerSlots + 27, playerSlots, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    // 从主物品栏移动到快捷栏
                    else if (index >= containerSlots + 27 && index < playerSlots) {
                        if (!this.mergeItemStack(itemstack1, containerSlots, containerSlots + 27, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}