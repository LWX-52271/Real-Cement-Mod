
package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCementBag extends Item {
    private static final String INVENTORY_TAG = "CementBagInventory";
    private static final String INITIALIZED_TAG = "Initialized";

    public ItemCementBag() {
        this.setUnlocalizedName(CementMod.MODID + ".cementbag");
        this.setRegistryName("cement_bag");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);

        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add("§7用于大型建筑项目");
        tooltip.add("§6右键打开§r");
        tooltip.add("§e容量: 9袋水泥§r");
        tooltip.add("§c警告: 携带过多会感到沉重§r");

        // 显示当前水泥粉数量
        ItemStackHandler handler = getInventory(stack);
        int cementCount = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                cementCount += handler.getStackInSlot(i).getCount();
            }
        }
        tooltip.add("§a当前水泥粉: " + cementCount + "袋§r");
    }

    // 新方法：初始化水泥袋
    private void initializeBag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        // 标记为已初始化
        stack.getTagCompound().setBoolean(INITIALIZED_TAG, true);

        ItemStackHandler handler = new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemCementPowder;
            }
        };

        // 填充9个水泥粉
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, new ItemStack(ItemRegistryHandler.CEMENT_POWDER, 1));
        }

        saveInventory(stack, handler);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // 射线追踪获取玩家看向的方块
        RayTraceResult raytrace = rayTrace(world, player, true);

        // 如果玩家看向的是水方块
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = raytrace.getBlockPos();
            IBlockState state = world.getBlockState(pos);

            if (state.getMaterial() == Material.WATER) {
                // 检查水泥袋中是否有水泥粉
                ItemStackHandler handler = getInventory(stack);
                int slotWithCement = findSlotWithCementPowder(handler);

                if (slotWithCement >= 0) {
                    // 客户端直接返回成功
                    if (world.isRemote) {
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }

                    // 消耗一个水泥粉
                    ItemStack cementStack = handler.getStackInSlot(slotWithCement);
                    cementStack.shrink(1);
                    if (cementStack.isEmpty()) {
                        handler.setStackInSlot(slotWithCement, ItemStack.EMPTY);
                    }

                    // 保存物品栏
                    saveInventory(stack, handler);

                    // 替换水方块为硬化混凝土
                    world.setBlockState(pos, BlockRegistryHandler.BLOCK_HARDENED_CONCRETE.getDefaultState());

                    // 播放声音效果
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE,
                            SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // 添加粒子效果
                    world.playEvent(2001, pos, Block.getStateId(state));

                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }

        // 如果不是点击水方块，则打开GUI

        // 如果是新水泥袋，初始化
        if (!stack.hasTagCompound() || !stack.getTagCompound().getBoolean(INITIALIZED_TAG)) {
            initializeBag(stack);
        }

        if (!world.isRemote) {
            // 服务器端打开GUI
            player.openGui(CementMod.instance, 5, world,
                    hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // 查找包含水泥粉的槽位
    private int findSlotWithCementPowder(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemCementPowder) {
                return i;
            }
        }
        return -1;
    }

    // 获取水泥袋物品栏
    public static ItemStackHandler getInventory(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                // 只允许放入水泥粉
                return stack.getItem() instanceof ItemCementPowder;
            }
        };

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(INVENTORY_TAG)) {
            handler.deserializeNBT(stack.getTagCompound().getCompoundTag(INVENTORY_TAG));
        }
        return handler;
    }

    // 保存水泥袋物品栏
    public static void saveInventory(ItemStack stack, ItemStackHandler handler) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag(INVENTORY_TAG, handler.serializeNBT());
    }

    // 事件监听器 - 检测玩家背包中的水泥袋
    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // 计算背包中水泥袋的总数量
            int cementBagCount = 0;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemCementBag) {
                    cementBagCount += stack.getCount();
                }
            }

            // 应用效果（如果携带水泥袋）
            if (cementBagCount > 0) {
                applyHeavyEffects(player, cementBagCount);
            }
        }
    }

    // 应用沉重效果
    private void applyHeavyEffects(EntityPlayer player, int bagCount) {
        // 计算效果等级（每4袋增加一级）
        int effectLevel = Math.min(4, (bagCount + 3) / 4); // 最高4级

        // 添加缓慢效果（ID 2）
        player.addPotionEffect(new PotionEffect(
                Potion.getPotionById(2),  // 缓慢效果
                40,                      // 持续时间（2秒）
                effectLevel - 1,         // 效果等级（0=I级，1=II级，以此类推）
                false,                   // 环境粒子效果
                false                    // 是否显示图标
        ));

        // 添加挖掘疲劳效果（ID 4）当携带超过8袋时
        if (bagCount >= 8) {
            int miningLevel = Math.min(2, (bagCount - 8) / 4); // 最高2级
            player.addPotionEffect(new PotionEffect(
                    Potion.getPotionById(4),  // 挖掘疲劳效果
                    40,                       // 持续时间
                    miningLevel,              // 效果等级
                    false,
                    false
            ));
        }

        // 添加生命值减少效果（ID 7）当携带超过16袋时
        if (bagCount >= 16) {
            player.addPotionEffect(new PotionEffect(
                    Potion.getPotionById(7),  // 瞬间伤害（但低等级表现为生命值减少）
                    40,
                    0,
                    false,
                    false
            ));
        }
    }
}