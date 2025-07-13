// TileCrusher.java - 1.12.2 修复版
package com.luowenxuan.cementmod.tiles;

import com.luowenxuan.cementmod.block.BlockCrusher;
import com.luowenxuan.cementmod.block.BlockRegistryHandler;
import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileCrusher extends TileEntity implements ITickable {
    // ==================== 配方系统 ====================
    public static class CrusherRecipe {
        public final Object input; // 可以是ItemStack, Block, 或String(矿辞)
        private final ItemStack output;
        private final int count;
        private final int time; // 处理时间（刻）
        private final float extraOutputChance; // 额外产出几率
        private final ItemStack extraOutput; // 额外产出物品

        public CrusherRecipe(Object input, ItemStack output, int count, int time) {
            this(input, output, count, time, 0.0f, ItemStack.EMPTY);
        }

        public CrusherRecipe(Object input, ItemStack output, int count, int time, float extraOutputChance, ItemStack extraOutput) {
            this.input = input;
            this.output = output;
            this.count = count;
            this.time = time;
            this.extraOutputChance = extraOutputChance;
            this.extraOutput = extraOutput;
        }

        public boolean matches(ItemStack stack) {
            if (input instanceof ItemStack) {
                return OreDictionary.itemMatches((ItemStack) input, stack, false);
            } else if (input instanceof Block) {
                return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == input;
            } else if (input instanceof String) {
                int[] ids = OreDictionary.getOreIDs(stack);
                if (ids != null) { // 1.12.2修复：添加空检查
                    for (int id : ids) {
                        if (OreDictionary.getOreName(id).equals(input)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public ItemStack getOutput() {
            return output.copy();
        }

        public int getOutputCount() {
            return count;
        }

        public int getProcessingTime() {
            return time;
        }

        public boolean hasExtraOutput() {
            return extraOutputChance > 0 && !extraOutput.isEmpty();
        }

        public float getExtraOutputChance() {
            return extraOutputChance;
        }

        public ItemStack getExtraOutput() {
            return extraOutput.copy();
        }
    }

    // 配方列表
    public static final List<CrusherRecipe> CRUSHER_RECIPES = new ArrayList<>();

    // 注册配方（静态初始化）
    static {
        // 基础配方
        addRecipe(BlockRegistryHandler.BLOCK_LIMESTONE, new ItemStack(ItemRegistryHandler.LIME_POWDER, 2), 200);
        addRecipe(Blocks.COBBLESTONE, new ItemStack(Blocks.GRAVEL), 1, 100); // 圆石 -> 沙砾
        addRecipe(Blocks.GRAVEL, new ItemStack(Blocks.SAND), 1, 80);         // 沙砾 -> 沙子

        // 矿石配方
        addRecipe("oreIron", new ItemStack(Items.IRON_INGOT), 2, 250);  // 铁矿石 -> 铁锭
        addRecipe("oreGold", new ItemStack(Items.GOLD_INGOT), 2, 250);  // 金矿石 -> 金锭
        addRecipe("oreCoal", new ItemStack(Items.COAL), 3, 180);        // 煤矿石 -> 煤炭
        addRecipe("oreRedstone", new ItemStack(Items.REDSTONE, 6), 200); // 红石矿石 -> 红石
        addRecipe("oreLapis", new ItemStack(Items.DYE, 8, 4), 200);     // 青金石矿石 -> 青金石
        addRecipe("oreDiamond", new ItemStack(Items.DIAMOND), 2, 300);  // 钻石矿石 -> 钻石
        addRecipe("oreEmerald", new ItemStack(Items.EMERALD), 2, 300);  // 绿宝石矿石 -> 绿宝石
        addRecipe("oreQuartz", new ItemStack(Items.QUARTZ, 3), 180);    // 下界石英矿石 -> 石英

        // 特殊配方
        addRecipe(Blocks.SAND, new ItemStack(Items.FLINT), 1, 150, 0.1f, new ItemStack(Items.FLINT)); // 沙子 -> 燧石（10%额外几率）
        addRecipe(Blocks.NETHERRACK, new ItemStack(Items.NETHERBRICK), 2, 150); // 下界岩 -> 下界砖
        addRecipe(Blocks.BONE_BLOCK, new ItemStack(Items.DYE, 6, 15), 180); // 骨块 -> 骨粉
        addRecipe(Blocks.OBSIDIAN, new ItemStack(Blocks.GRAVEL, 2), 300); // 黑曜石 -> 沙砾
        addRecipe(Blocks.END_STONE, new ItemStack(Blocks.SAND, 2), 200); // 末地石 -> 沙子

        // 自定义配方
        addRecipe(BlockRegistryHandler.BLOCK_HARDENED_CONCRETE,
                new ItemStack(ItemRegistryHandler.CONCRETE_DUST, 4), 200);
        addRecipe(Blocks.CONCRETE, new ItemStack(Blocks.SAND, 1), 100); // 混凝土 -> 沙子
        addRecipe(Blocks.CONCRETE_POWDER, new ItemStack(Blocks.SAND, 1), 80); // 混凝土粉末 -> 沙子
    }

    // 添加配方辅助方法
    public static void addRecipe(Object input, ItemStack output, int count, int time) {
        CRUSHER_RECIPES.add(new CrusherRecipe(input, output, count, time));
    }

    public static void addRecipe(Object input, ItemStack output, int count, int time, float extraChance, ItemStack extraOutput) {
        CRUSHER_RECIPES.add(new CrusherRecipe(input, output, count, time, extraChance, extraOutput));
    }

    public static void addRecipe(Object input, ItemStack output, int time) {
        addRecipe(input, output, output.getCount(), time);
    }

    public static void addRecipe(Object input, ItemStack output, int time, float extraChance, ItemStack extraOutput) {
        addRecipe(input, output, output.getCount(), time, extraChance, extraOutput);
    }

    // ==================== TileEntity 核心 ====================
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            syncInventory();
        }
    };

    private int progress;
    private int maxProgress = 200;
    private boolean structureValid = false;
    private boolean structureCheckDirty = true;
    private long lastStructureCheckTime = 0;
    private static final int CHECK_INTERVAL = 20;
    private int soundCooldown = 0;

    // 结构标记方法
    public void markStructureDirty() {
        this.structureCheckDirty = true;
        this.lastStructureCheckTime = 0;
        markDirty();

        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    // 进度同步方法
    private void syncProgress() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    }

    // 物品栏同步方法
    private void syncInventory() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private final IItemHandler hopperHandler = new IItemHandler() {
        @Override public int getSlots() { return 1; }

        @Nonnull @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? inventory.getStackInSlot(1) : ItemStack.EMPTY;
        }

        @Nonnull @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0) return ItemStack.EMPTY;
            ItemStack output = inventory.getStackInSlot(1);
            if (output.isEmpty()) return ItemStack.EMPTY;

            int toExtract = Math.min(amount, output.getCount());
            ItemStack result = output.copy();
            result.setCount(toExtract);

            if (!simulate) {
                output.shrink(toExtract);
                if (output.isEmpty()) {
                    inventory.setStackInSlot(1, ItemStack.EMPTY);
                }
                markDirty();
            }
            return result;
        }

        @Override public int getSlotLimit(int slot) { return 64; }
    };

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        // 结构检查
        long currentTime = world.getTotalWorldTime();
        if (currentTime - lastStructureCheckTime > CHECK_INTERVAL || structureCheckDirty) {
            boolean lastValid = structureValid;
            structureValid = isStructureValid();
            lastStructureCheckTime = currentTime;
            structureCheckDirty = false;

            if (lastValid != structureValid) {
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
                markDirty();
            }
        }

        // 没有有效结构则重置进度
        if (!structureValid) {
            if (progress != 0) {
                progress = 0;
                syncProgress();
            }
            return;
        }

        // 获取当前配方
        CrusherRecipe recipe = getRecipeForInput(inventory.getStackInSlot(0));
        boolean progressChanged = false;

        if (recipe != null && canProcess(recipe)) {
            maxProgress = recipe.getProcessingTime();

            progress++;
            progressChanged = true;

            if (progress >= maxProgress) {
                processItem(recipe);
                progress = 0;
                syncProgress();
            }

            // 每10刻播放一次工作音效
            if (soundCooldown <= 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND,
                        SoundCategory.BLOCKS, 0.5F, 0.8F);
                soundCooldown = 10;
            } else {
                soundCooldown--;
            }
        } else {
            if (progress != 0) {
                progress = 0;
                progressChanged = true;
            }
        }

        // 进度变化时同步
        if (progressChanged) {
            syncProgress();
        }
    }

    // 获取匹配的配方
    @Nullable
    private CrusherRecipe getRecipeForInput(ItemStack input) {
        if (input.isEmpty()) return null;

        // 1.12.2修复：优先检查精确匹配
        for (CrusherRecipe recipe : CRUSHER_RECIPES) {
            if (recipe.input instanceof ItemStack) {
                ItemStack recipeStack = (ItemStack) recipe.input;
                if (ItemStack.areItemsEqual(recipeStack, input) &&
                        ItemStack.areItemStackTagsEqual(recipeStack, input)) {
                    return recipe;
                }
            }
        }

        // 常规匹配
        for (CrusherRecipe recipe : CRUSHER_RECIPES) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    // 检查是否可以处理
    private boolean canProcess(CrusherRecipe recipe) {
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        ItemStack result = recipe.getOutput();
        result.setCount(recipe.getOutputCount());

        // 检查输入是否足够
        if (input.getCount() < 1) return false;

        // 检查输出槽是否可接受主产物
        if (output.isEmpty()) return true;
        if (!output.isItemEqual(result)) return false;

        // 检查额外产物空间
        int totalOutput = output.getCount() + result.getCount();
        if (recipe.hasExtraOutput()) {
            totalOutput += recipe.getExtraOutput().getCount();
        }

        return totalOutput <= output.getMaxStackSize();
    }

    // 尝试合并产出物 (修复版)
    private boolean tryMergeOutput(ItemStack stack) {
        if (stack.isEmpty()) return true;

        ItemStack current = inventory.getStackInSlot(1);
        if (current.isEmpty()) {
            inventory.setStackInSlot(1, stack.copy());
            return true;
        }

        if (current.isItemEqual(stack) &&
                current.getCount() + stack.getCount() <= current.getMaxStackSize()) {
            // 创建新物品堆栈并设置数量
            ItemStack newStack = current.copy();
            newStack.setCount(current.getCount() + stack.getCount());
            inventory.setStackInSlot(1, newStack);
            return true;
        }

        return false;
    }

    // 处理物品
    private void processItem(CrusherRecipe recipe) {
        ItemStack input = inventory.getStackInSlot(0);

        // 创建产出物副本
        ItemStack mainOutput = recipe.getOutput().copy();
        mainOutput.setCount(recipe.getOutputCount());

        // 处理额外产出
        ItemStack extraOutput = ItemStack.EMPTY;
        if (recipe.hasExtraOutput() && world.rand.nextFloat() < recipe.getExtraOutputChance()) {
            extraOutput = recipe.getExtraOutput().copy();
        }

        // 尝试合并额外产出
        if (!extraOutput.isEmpty()) {
            if (!tryMergeOutput(extraOutput)) {
                // 无法合并额外产出，放弃处理
                return;
            }
        }

        // 尝试合并主产出
        if (!tryMergeOutput(mainOutput)) {
            // 无法合并主产出，放弃处理
            return;
        }

        // 消耗输入
        input.shrink(1);
        markDirty();
    }

    // 结构验证方法（1.12.2修复版）
    private boolean isStructureValid() {
        if (world == null) return false;

        // 获取破碎机朝向
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockCrusher)) return false;

        EnumFacing facing = state.getValue(BlockCrusher.FACING);

        // 根据朝向计算偏移方向
        EnumFacing right = facing.rotateY();
        EnumFacing back = facing.getOpposite();

        // ===== 底层检查 (Y-1) =====
        BlockPos belowPos = pos.down();
        BlockPos[] baseFramePositions = {
                belowPos,                   // 破碎机正下方（必须是钢铁框架）
                belowPos.offset(right),     // 右侧
                belowPos.offset(back),      // 后方
                belowPos.offset(right).offset(back) // 右后方
        };

        // 检查所有底层位置是否都是钢铁框架
        for (BlockPos framePos : baseFramePositions) {
            if (world.getBlockState(framePos).getBlock() != BlockRegistryHandler.BLOCK_STEEL_FRAME) {
                return false;
            }
        }

        // ===== 当前层检查 (Y) =====
        int steelFrameCount = 0;
        boolean hasCrusher = false;

        // 破碎机自身位置
        hasCrusher = (world.getBlockState(pos).getBlock() == BlockRegistryHandler.BLOCK_CRUSHER);

        // 其他三个位置 - 钢铁框架
        BlockPos[] framePositionsCurrent = {
                pos.offset(right),      // 右侧
                pos.offset(back),       // 后方
                pos.offset(right).offset(back) // 右后方
        };

        for (BlockPos framePos : framePositionsCurrent) {
            if (world.getBlockState(framePos).getBlock() == BlockRegistryHandler.BLOCK_STEEL_FRAME) {
                steelFrameCount++;
            }
        }

        return hasCrusher && steelFrameCount >= 3;
    }

    // ==================== NBT 和网络通信 ====================
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        progress = compound.getInteger("Progress");
        // 防止进度值溢出
        maxProgress = compound.getInteger("MaxProgress");
        progress = Math.min(progress, maxProgress);
        structureValid = compound.getBoolean("StructureValid");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("Progress", progress);
        compound.setInteger("MaxProgress", maxProgress);
        compound.setBoolean("StructureValid", structureValid);
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
            if (facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(hopperHandler);
            }
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
    }

    public int getProgressScaled(int scale) {
        return maxProgress > 0 ? progress * scale / maxProgress : 0;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setBoolean("StructureValid", structureValid);
        tag.setInteger("Progress", progress);
        tag.setInteger("MaxProgress", maxProgress);
        tag.setTag("Inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        structureValid = tag.getBoolean("StructureValid");
        progress = tag.getInteger("Progress");
        maxProgress = tag.getInteger("MaxProgress");
        inventory.deserializeNBT(tag.getCompoundTag("Inventory"));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }
}