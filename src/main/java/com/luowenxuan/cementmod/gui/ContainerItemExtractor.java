package com.luowenxuan.cementmod.gui;

import com.luowenxuan.cementmod.tiles.TileItemExtractor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerItemExtractor extends Container {
    private final TileItemExtractor tileEntity;

    public ContainerItemExtractor(InventoryPlayer playerInv, TileItemExtractor te) {
        this.tileEntity = te;
        IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // 取物器的9个物品槽 (3x3网格)
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new SlotItemHandler(inventory, x + y * 3, 62 + x * 18, 17 + y * 18));
            }
        }

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
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int containerSlots = 9; // 取物器的9个槽位

            // 取物器槽位 -> 玩家背包
            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, containerSlots + 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 玩家背包 -> 取物器槽位
            else {
                if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
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