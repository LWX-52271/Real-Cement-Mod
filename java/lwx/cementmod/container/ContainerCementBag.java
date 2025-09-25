package lwx.cementmod.container;

import lwx.cementmod.item.ItemCementBag;
import lwx.cementmod.item.ItemCementPowder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCementBag extends Container {
    private final ItemStack bagStack;
    private final ItemStackHandler bagInventory;
    private final EntityPlayer player;

    public ContainerCementBag(InventoryPlayer playerInventory, ItemStack bagStack) {
        this.bagStack = bagStack;
        this.player = playerInventory.player;
        this.bagInventory = ItemCementBag.getInventory(bagStack);

        // 添加水泥袋格子 (3x3)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                addSlotToContainer(new SlotItemHandler(bagInventory, col + row * 3,
                        62 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        // 只允许放入水泥粉
                        return stack.getItem() instanceof ItemCementPowder;
                    }
                });
            }
        }

        // 添加玩家物品栏 (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }

        // 添加玩家快捷栏
        for (int col = 0; col < 9; ++col) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    // 更新transferStackInSlot方法
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            // 从水泥袋转移到玩家物品栏
            if (index < 9) {
                if (!mergeItemStack(slotStack, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 从玩家物品栏转移到水泥袋
            else if (slotStack.getItem() instanceof ItemCementPowder) {
                // 尝试将水泥粉放入已有的堆栈中
                for (int i = 0; i < 9; i++) {
                    ItemStack bagStack = bagInventory.getStackInSlot(i);
                    if (!bagStack.isEmpty() && bagStack.getItem() == slotStack.getItem()) {
                        if (mergeItemStack(slotStack, i, i + 1, false)) {
                            return stack;
                        }
                    }
                }

                // 如果没有找到匹配的堆栈，放入空槽位
                for (int i = 0; i < 9; i++) {
                    if (bagInventory.getStackInSlot(i).isEmpty()) {
                        if (mergeItemStack(slotStack, i, i + 1, false)) {
                            return stack;
                        }
                    }
                }
                return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        // 保存物品栏数据到NBT
        if (!player.world.isRemote) {
            ItemCementBag.saveInventory(bagStack, bagInventory);
        }
    }
}
