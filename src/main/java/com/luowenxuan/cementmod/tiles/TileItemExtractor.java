package com.luowenxuan.cementmod.tiles;

import com.luowenxuan.cementmod.CementMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityBrewingStand;
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

    // 新增配置：允许提取的方向（默认所有方向）
    private boolean[] allowedFacings = new boolean[] {true, true, true, true, true, true};

    // 输出槽检测方法，根据不同功能方块的输出槽索引来判断
    private boolean isOutputSlot(IItemHandler handler, int slot, Class<?> tileClass) {
        if (tileClass == TileCrusher.class) {
            return slot == 1; // TileCrusher 的输出槽索引为 1
        } else if (tileClass == TileRotaryKiln.class) {
            return slot == 2; // TileRotaryKiln 的输出槽索引为 2
        } else if (tileClass == TileCementPacker.class) {
            return slot == 2; // TileCementPacker 的输出槽索引为 2
        } else if (tileClass == TileCementMixer.class) {
            return slot == 3; // TileCementMixer 的输出槽索引为 3
        } else if (tileClass == TileEntityFurnace.class) {
            return slot == 2; // 熔炉的输出槽索引为 2
        } else if (tileClass == TileEntityBrewingStand.class) {
            return slot >= 3 && slot <= 5; // 酿造台的输出槽索引为 3、4、5
        }
        // 1. 检查是否能提取物品
        ItemStack simulateExtract = handler.extractItem(slot, 1, true);
        if (simulateExtract.isEmpty()) return false;

        // 2. 关键修复：使用原物品测试插入
        ItemStack testInsert = simulateExtract.copy();
        ItemStack remainder = handler.insertItem(slot, testInsert, true);

        // 3. 仅当完全无法插入时才判定为输出槽（即使槽位未满）
        return remainder.getCount() >= testInsert.getCount();
    }

    @Override
    public void update() {
        if (world.isRemote || world.getTotalWorldTime() % 20 != 0) return;

        for (EnumFacing facing : EnumFacing.VALUES) {
            // 检查方向是否允许
            if (!allowedFacings[facing.ordinal()]) continue;

            BlockPos neighborPos = pos.offset(facing);
            TileEntity neighbor = world.getTileEntity(neighborPos);

            if (neighbor != null) {
                EnumFacing accessSide = facing.getOpposite();

                if (neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, accessSide)) {
                    IItemHandler neighborHandler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, accessSide);
                    for (int slot = 0; slot < neighborHandler.getSlots(); slot++) {
                        if (isOutputSlot(neighborHandler, slot, neighbor.getClass())) {
                            ItemStack extracted = neighborHandler.extractItem(slot, 1, false);
                            if (!extracted.isEmpty()) {
                                boolean inserted = false;
                                for (int i = 0; i < inventory.getSlots(); i++) {
                                    ItemStack remainder = inventory.insertItem(i, extracted, false);
                                    if (remainder.isEmpty()) {
                                        extractedItems++;
                                        markDirty();
                                        inserted = true;
                                        break;
                                    } else {
                                        extracted = remainder;
                                    }
                                }
                                // 如果没有空间存放提取的物品，将其放回原槽位
                                if (!inserted) {
                                    neighborHandler.insertItem(slot, extracted, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 新增方法：设置允许提取的方向
    public void setFacingAllowed(EnumFacing facing, boolean allowed) {
        allowedFacings[facing.ordinal()] = allowed;
        markDirty();
    }

    // 新增方法：获取允许提取的方向
    public boolean isFacingAllowed(EnumFacing facing) {
        return allowedFacings[facing.ordinal()];
    }

    // 数据保存与同步（新增方向配置存储）
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        extractedItems = compound.getInteger("ExtractedItems");

        // 读取方向配置
        if (compound.hasKey("AllowedFacings")) {
            byte[] facings = compound.getByteArray("AllowedFacings");
            for (int i = 0; i < 6 && i < facings.length; i++) {
                allowedFacings[i] = facings[i] != 0;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("ExtractedItems", extractedItems);

        // 存储方向配置
        byte[] facings = new byte[6];
        for (int i = 0; i < 6; i++) {
            facings[i] = (byte)(allowedFacings[i] ? 1 : 0);
        }
        compound.setByteArray("AllowedFacings", facings);

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