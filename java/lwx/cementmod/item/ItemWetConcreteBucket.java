package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.block.BlockConcreteCauldron;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.block.BlockWetConcreteBucketPlaced;
import lwx.cementmod.fluids.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;

public class ItemWetConcreteBucket extends Item {
    // 设置最大耐久度
    private static final int MAX_DURABILITY = 16;

    public ItemWetConcreteBucket() {
        this.setUnlocalizedName(CementMod.MODID + ".wetconcretebucket");
        this.setRegistryName("wet_concrete_bucket");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setMaxDamage(MAX_DURABILITY); // 设置最大耐久度
        this.setNoRepair(); // 设置不可修复
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        // 检查是否在潜行状态下右键
        if (player.isSneaking()) {
            RayTraceResult raytrace = rayTrace(world, player, false);

            if (raytrace == null || raytrace.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.PASS, itemStack);
            }

            BlockPos pos = raytrace.getBlockPos();
            EnumFacing side = raytrace.sideHit;
            BlockPos placePos = pos.offset(side);

            // 检查是否可以放置
            if (player.canPlayerEdit(placePos, side, itemStack) &&
                    world.mayPlace(BlockRegistryHandler.BLOCK_WET_CONCRETE_BUCKET_PLACED, placePos, false, side, player)) {

                // 获取当前耐久度并计算状态值 (0-8)
                int damage = itemStack.getItemDamage();
                int state = (int) Math.floor((MAX_DURABILITY - damage - 1) * 8.0 / MAX_DURABILITY);
                state = Math.min(8, Math.max(0, state)); // 确保在0-8范围内

                // 放置方块并设置状态
                IBlockState blockState = BlockRegistryHandler.BLOCK_WET_CONCRETE_BUCKET_PLACED.getDefaultState()
                        .withProperty(BlockWetConcreteBucketPlaced.DURABILITY, state);
                world.setBlockState(placePos, blockState);

                // 播放放置声音
                world.playSound(player, placePos, SoundType.METAL.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!player.capabilities.isCreativeMode) {
                    // 消耗物品
                    itemStack.shrink(1);
                }

                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }

            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        } else {
            // 原有的倒出流体逻辑
            RayTraceResult raytrace = rayTrace(world, player, false); // 修改为 false

            if (raytrace == null || raytrace.typeOfHit != RayTraceResult.Type.BLOCK) {
                CementMod.logger.debug("未命中方块或无效的射线追踪结果");
                return new ActionResult<>(EnumActionResult.PASS, itemStack);
            }

            BlockPos pos = raytrace.getBlockPos();
            EnumFacing side = raytrace.sideHit;
            BlockPos placePos = pos.offset(side);

            // 检查耐久度
            if (itemStack.getItemDamage() >= MAX_DURABILITY - 1) {
                CementMod.logger.debug("湿混凝土桶已耗尽");
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }

            // 1. 检查是否点击了炼药锅
            IBlockState clickedState = world.getBlockState(pos);
            if (clickedState.getBlock() == Blocks.CAULDRON) {
                return handleCauldronInteraction(world, player, hand, itemStack, pos, clickedState);
            }

            // 2. 检查是否点击了混凝土炼药锅
            if (clickedState.getBlock() instanceof BlockConcreteCauldron) {
                // 混凝土炼药锅已经处理了自己的交互，这里不需要额外处理
                return new ActionResult<>(EnumActionResult.PASS, itemStack);
            }

            // 3. 确保玩家可以编辑该位置
            if (!player.canPlayerEdit(placePos, side, itemStack)) {
                CementMod.logger.debug("玩家无法编辑位置: " + placePos);
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }

            // 4. 检查目标位置是否可以放置流体
            IBlockState targetState = world.getBlockState(placePos);
            Block targetBlock = targetState.getBlock();

            boolean canPlace = world.isAirBlock(placePos) ||
                    targetBlock.isReplaceable(world, placePos) ||
                    (targetBlock instanceof IFluidBlock);

            if (!canPlace) {
                CementMod.logger.debug("目标位置不可放置流体: " + placePos + ", 方块: " + targetBlock);
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }

            // 5. 尝试使用Forge的流体放置方法
            FluidStack fluidStack = new FluidStack(ModFluids.WET_CONCRETE, 1000);
            boolean placed = FluidUtil.tryPlaceFluid(player, world, placePos, itemStack, fluidStack).isSuccess();

            if (placed) {
                CementMod.logger.info("成功放置流体在位置: " + placePos);
                // 播放放置声音
                world.playSound(player, placePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!player.capabilities.isCreativeMode) {
                    // 消耗耐久度
                    itemStack.damageItem(1, player); // 修改为每次消耗1点耐久度
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }

            // 6. 如果Forge方法失败，尝试手动放置
            CementMod.logger.debug("Forge流体放置方法失败，尝试手动放置");
            if (world.mayPlace(ModFluids.WET_CONCRETE_BLOCK, placePos, false, side, player)) {
                IBlockState fluidState = ModFluids.WET_CONCRETE_BLOCK.getDefaultState();
                if (world.setBlockState(placePos, fluidState)) {
                    CementMod.logger.info("手动放置流体成功在位置: " + placePos);

                    // 播放放置声音
                    world.playSound(player, placePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!player.capabilities.isCreativeMode) {
                        // 消耗耐久度
                        itemStack.damageItem(1, player); // 修改为每次消耗1点耐久度
                        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
                    }
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
                }
            }

            // 粘稠液体倒出声
            world.playSound(null, player.getPosition(),
                    SoundEvents.ITEM_BUCKET_EMPTY_LAVA,
                    SoundCategory.BLOCKS, 1.0F, 0.7F);

            CementMod.logger.debug("所有放置方法均失败");
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
    }

    /**
     * 处理与炼药锅的交互
     */
    // 在原有的 handleCauldronInteraction 方法中，修改为：
    private ActionResult<ItemStack> handleCauldronInteraction(World world, EntityPlayer player, EnumHand hand,
                                                              ItemStack itemStack, BlockPos pos, IBlockState cauldronState) {
        // 移除水位检查，直接转换
        if (cauldronState.getBlock() == Blocks.CAULDRON) {
            return replaceWithConcreteCauldron(world, player, hand, itemStack, pos);
        }
        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    /**
     * 将炼药锅替换为混凝土炼药锅
     */
    private ActionResult<ItemStack> replaceWithConcreteCauldron(World world, EntityPlayer player, EnumHand hand,
                                                                ItemStack itemStack, BlockPos pos) {
        // 使用静态方法转换炼药锅
        if (BlockConcreteCauldron.convertVanillaCauldron(world, pos, player)) {
            // 播放倒水声音
            world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.capabilities.isCreativeMode) {
                // 消耗耐久度
                itemStack.damageItem(1, player);
            }

            CementMod.logger.info("成功将湿混凝土倒入炼药锅，替换为混凝土炼药锅");
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, itemStack);
    }

    // 显示剩余耐久度而不是损坏值
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) stack.getItemDamage() / (double) MAX_DURABILITY;
    }
}