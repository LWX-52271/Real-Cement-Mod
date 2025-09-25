package lwx.cementmod.container;

import lwx.cementmod.tiles.TileCementPacker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCementPacker extends Container {
    private final TileCementPacker tileEntity;
    private int lastPackTime; // 跟踪上一次的打包时间
    private int packProgress; // 存储同步的进度值

    public ContainerCementPacker(InventoryPlayer playerInv, TileCementPacker te) {
        this.tileEntity = te;
        IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // 水泥粉输入槽 (0)
        addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 26) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == tileEntity.getInputItem();
            }
        });

        // 皮革输入槽 (1)
        addSlotToContainer(new SlotItemHandler(inventory, 1, 56, 45) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.LEATHER;
            }
        });

        // 输出槽 (2) - 不可输入
        addSlotToContainer(new SlotItemHandler(inventory, 2, 116, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
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
        return true;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        // 初始化时发送当前进度
        listener.sendWindowProperty(this, 0, tileEntity.getPackProgressScaled(100));
    }

    // 同步数据到客户端
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // 获取当前进度（0-100）
        int currentProgress = tileEntity.getPackProgressScaled(100);

        // 检测进度变化
        if (lastPackTime != currentProgress) {
            // 更新所有监听器
            for (IContainerListener listener : this.listeners) {
                listener.sendWindowProperty(this, 0, currentProgress);
            }
            lastPackTime = currentProgress;
        }
    }

    // 从客户端接收同步数据
    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            this.packProgress = data; // 保存进度值
        }
    }

    // 获取打包进度（用于GUI渲染）
    public int getPackProgressScaled(int scale) {
        return packProgress * scale / 100;
    }

    // 物品转移逻辑
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int containerSlots = 3; // 机器槽位数量（增加为3个）

            // 机器槽位 -> 玩家背包
            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, containerSlots + 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            // 玩家背包 -> 机器输入槽
            else {
                // 尝试放入水泥粉槽
                if (itemstack1.getItem() == tileEntity.getInputItem()) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 尝试放入皮革槽
                else if (itemstack1.getItem() == Items.LEATHER) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 其他物品不允许放入
                else {
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