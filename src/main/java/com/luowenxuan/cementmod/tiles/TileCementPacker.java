package com.luowenxuan.cementmod.tiles;

import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileCementPacker extends TileEntity implements ITickable {

    // 增加皮革槽位：0-水泥粉, 1-皮革, 2-输出
    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private int packTime;
    private int totalPackTime = 100; // 5秒打包时间

    @Override
    public void update() {
        if (world.isRemote) return;

        if (canPack()) {
            packTime++;
            if (packTime >= totalPackTime) {
                packCement();
                packTime = 0;
            }
        } else {
            packTime = 0;
        }

        // 标记 TileEntity 数据有变化，需要同步到客户端
        if (this.hasWorld() && !this.world.isRemote) {
            this.markDirty();
            this.world.notifyBlockUpdate(this.getPos(), this.world.getBlockState(this.getPos()), this.world.getBlockState(this.getPos()), 3);
        }
    }

    private boolean canPack() {
        // 输入槽0：水泥粉（需要9个）
        ItemStack cementPowder = inventory.getStackInSlot(0);
        // 输入槽1：皮革（需要1个）
        ItemStack leather = inventory.getStackInSlot(1);
        // 输出槽2：水泥袋
        ItemStack output = inventory.getStackInSlot(2);

        // 检查水泥粉
        boolean hasCement = !cementPowder.isEmpty() &&
                cementPowder.getItem() == getInputItem() &&
                cementPowder.getCount() >= 9;

        // 检查皮革
        boolean hasLeather = !leather.isEmpty() &&
                leather.getItem() == Items.LEATHER &&
                leather.getCount() >= 1;

        // 检查输出槽
        boolean canOutput = output.isEmpty() ||
                (output.getItem() == ItemRegistryHandler.CEMENT_BAG &&
                        output.getCount() < output.getMaxStackSize());

        return hasCement && hasLeather && canOutput;
    }

    private void packCement() {
        // 消耗9个水泥粉
        inventory.extractItem(0, 9, false);
        // 消耗1个皮革
        inventory.extractItem(1, 1, false);

        // 产生水泥袋
        ItemStack output = inventory.getStackInSlot(2);
        if (output.isEmpty()) {
            inventory.setStackInSlot(2, new ItemStack(ItemRegistryHandler.CEMENT_BAG, 1));
        } else {
            output.grow(1);
        }
    }

    // 数据保存与同步
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        packTime = compound.getInteger("PackTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("PackTime", packTime);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
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

    public int getPackProgressScaled(int scale) {
        if (totalPackTime == 0) return 0;
        return packTime * scale / totalPackTime;
    }

    public Item getInputItem() {
        return ItemRegistryHandler.CEMENT_POWDER;
    }

    // 获取皮革物品
    public Item getLeatherItem() {
        return Items.LEATHER;
    }
}