package lwx.cementmod.item;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import lwx.cementmod.block.BlockWetConcreteBucketPlaced;
import lwx.cementmod.block.BlockWetConcreteSurface;
import lwx.cementmod.registry.BlockRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTrowel extends Item {
    // 设置最大耐久度
    private static final int MAX_DURABILITY = 256;
    // 特殊模式切换NBT标签
    private static final String MODE_TAG = "TrowelMode";
    // 水泥状态NBT标签
    private static final String HAS_CONCRETE_TAG = "HasConcrete";
    // 混凝土量NBT标签
    private static final String CONCRETE_AMOUNT_TAG = "ConcreteAmount";
    // 最大混凝土容量
    private static final int MAX_CONCRETE_AMOUNT = 8;

    // 空框架到湿润框架的映射
    private static final Map<Block, Block> EMPTY_TO_WET_MAP = new HashMap<>();
    // 湿润框架到固化框架的映射
    private static final Map<Block, Block> WET_TO_SOLID_MAP = new HashMap<>();
    // 原方块到湿混凝土表面的映射
    private static final Map<Block, Block> BLOCK_TO_SURFACE_MAP = new HashMap<>();
    // 快速固化映射（直接空到固）
    private static final Map<Block, Block> EMPTY_TO_SOLID_MAP = new HashMap<>();

    // 初始化映射
    static {
        // 混凝土框架映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_EMPTY, BlockRegistryHandler.BLOCK_CONCRETE_FRAME_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_WET, BlockRegistryHandler.BLOCK_CONCRETE_FRAME_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CONCRETE_FRAME_EMPTY, BlockRegistryHandler.BLOCK_CONCRETE_FRAME_SOLID);

        // 各种尺寸混凝土板映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_4_EMPTY, BlockRegistryHandler.BLOCK_16_16_4_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_4_WET, BlockRegistryHandler.BLOCK_16_16_4_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_4_EMPTY, BlockRegistryHandler.BLOCK_16_16_4_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_6_EMPTY, BlockRegistryHandler.BLOCK_16_16_6_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_6_WET, BlockRegistryHandler.BLOCK_16_16_6_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_6_EMPTY, BlockRegistryHandler.BLOCK_16_16_6_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16x16x8_EMPTY, BlockRegistryHandler.BLOCK_16x16x8_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16x16x8_WET, BlockRegistryHandler.BLOCK_16x16x8_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16x16x8_EMPTY, BlockRegistryHandler.BLOCK_16x16x8_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_10_EMPTY, BlockRegistryHandler.BLOCK_16_16_10_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_10_WET, BlockRegistryHandler.BLOCK_16_16_10_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_10_EMPTY, BlockRegistryHandler.BLOCK_16_16_10_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_12_EMPTY, BlockRegistryHandler.BLOCK_16_16_12_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_12_WET, BlockRegistryHandler.BLOCK_16_16_12_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_12_EMPTY, BlockRegistryHandler.BLOCK_16_16_12_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_14_EMPTY, BlockRegistryHandler.BLOCK_16_16_14_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_14_WET, BlockRegistryHandler.BLOCK_16_16_14_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_14_EMPTY, BlockRegistryHandler.BLOCK_16_16_14_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_16_16_16_EMPTY, BlockRegistryHandler.BLOCK_16_16_16_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_16_WET, BlockRegistryHandler.BLOCK_16_16_16_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_16_16_16_EMPTY, BlockRegistryHandler.BLOCK_16_16_16_SOLID);

        // 混凝土楼梯映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_STAIR_EMPTY, BlockRegistryHandler.BLOCK_CEMENT_STAIR_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_STAIR_WET, BlockRegistryHandler.BLOCK_CEMENT_STAIR_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_STAIR_EMPTY, BlockRegistryHandler.BLOCK_CEMENT_STAIR_SOLID);

        // 倒置混凝土楼梯映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_EMPTY, BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_WET, BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_EMPTY, BlockRegistryHandler.BLOCK_INVERTED_CEMENT_STAIR_SOLID);

        // 倾斜水泥栏杆映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_WET, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_EMPTY, BlockRegistryHandler.BLOCK_INCLINED_CEMENT_RAILING_SOLID);

        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_WET, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_EMPTY, BlockRegistryHandler.BLOCK_END_OF_CEMENT_RAILING_SOLID);

        // 水泥柱映射
        EMPTY_TO_WET_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET);
        WET_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_WET, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_SOLID);
        EMPTY_TO_SOLID_MAP.put(BlockRegistryHandler.BLOCK_CEMENT_PILLAR_EMPTY, BlockRegistryHandler.BLOCK_CEMENT_PILLAR_SOLID);

        // 常见方块到湿混凝土表面的映射 - 扩展更多方块
        BLOCK_TO_SURFACE_MAP.put(Blocks.STONE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.COBBLESTONE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.STONEBRICK, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.BRICK_BLOCK, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.PLANKS, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.DIRT, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.GRASS, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.GRAVEL, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.SAND, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.SANDSTONE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.NETHERRACK, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.END_STONE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.OBSIDIAN, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.QUARTZ_BLOCK, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(Blocks.PRISMARINE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(BlockRegistryHandler.BLOCK_HARDENED_CONCRETE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
        BLOCK_TO_SURFACE_MAP.put(BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE, BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE);
    }

    // 抹子工作模式枚举 - 新增快速固化模式
    public enum TrowelMode {
        PLACEMENT("placement"),    // 放置模式 - 从湿混凝土桶提取混凝土并填充框架
        SMOOTHING("smoothing"),    // 平滑模式 - 平滑混凝土表面或给其他方块涂湿混凝土
        QUICK_CURE("quick_cure"),  // 快速固化模式 - 直接填充并固化（消耗更多混凝土）
        DETAILING("detailing"),    // 细节模式 - 添加混凝土纹理
        RESTORING("restoring"),    // 恢复模式 - 恢复原方块
        SCULPTING("sculpting");    // 雕刻模式 - 雕刻混凝土表面

        private final String name;

        TrowelMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static TrowelMode getNextMode(TrowelMode current) {
            TrowelMode[] modes = values();
            return modes[(current.ordinal() + 1) % modes.length];
        }
    }

    public ItemTrowel() {
        this.setUnlocalizedName(CementMod.MODID + ".trowel");
        this.setRegistryName("trowel");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setMaxDamage(MAX_DURABILITY);
        this.setNoRepair();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // 获取当前模式
        TrowelMode mode = getMode(stack);

        // 潜行右键切换模式
        if (player.isSneaking()) {
            cycleMode(stack);
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation(
                        "message.cementmod.trowel_mode." + getMode(stack).getName()
                ));
            }
            return EnumActionResult.SUCCESS;
        }

        // 任何模式下都可以从湿混凝土桶中获取水泥
        if (block == BlockRegistryHandler.BLOCK_WET_CONCRETE_BUCKET_PLACED) {
            return handleConcreteExtraction(player, world, pos, state, stack);
        }

        // 模式1: 放置模式 - 填充框架
        if (mode == TrowelMode.PLACEMENT) {
            if (isConcreteFrameEmpty(block)) {
                return fillConcreteFrame(player, world, pos, state, stack);
            }
        }

        // 模式2: 平滑模式 - 平滑混凝土表面或给其他方块涂湿混凝土
        if (mode == TrowelMode.SMOOTHING) {
            if (isWetConcreteBlock(block)) {
                return smoothConcrete(player, world, pos, state, stack);
            } else if (canConvertToSurface(block) && hasConcrete(stack)) {
                return applyConcreteSurface(player, world, pos, state, stack);
            }
        }

        // 模式3: 快速固化模式 - 直接填充并固化框架
        if (mode == TrowelMode.QUICK_CURE) {
            if (isConcreteFrameEmpty(block)) {
                return quickCureConcreteFrame(player, world, pos, state, stack);
            } else if (isWetConcreteBlock(block)) {
                return smoothConcrete(player, world, pos, state, stack);
            }
        }

        // 模式4: 细节模式 - 添加混凝土纹理
        if (mode == TrowelMode.DETAILING && isConcreteBlock(block)) {
            return addConcreteDetails(player, world, pos, facing, stack);
        }

        // 模式5: 恢复模式 - 恢复原方块
        if (mode == TrowelMode.RESTORING && block == BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE) {
            return restoreOriginalBlock(player, world, pos, stack);
        }

        // 模式6: 雕刻模式 - 雕刻混凝土表面
        if (mode == TrowelMode.SCULPTING && isSculptableBlock(block)) {
            return sculptConcrete(player, world, pos, facing, stack);
        }

        // 通用功能：修复混凝土裂缝
        if (isCrackedConcrete(block) && hasConcrete(stack)) {
            return repairCrackedConcrete(player, world, pos, state, stack);
        }

        return EnumActionResult.PASS;
    }

    // 从湿混凝土桶中提取混凝土 - 改进：支持多次提取
    private EnumActionResult handleConcreteExtraction(EntityPlayer player, World world, BlockPos pos,
                                                      IBlockState state, ItemStack stack) {
        int currentAmount = getConcreteAmount(stack);

        // 检查抹子是否已满
        if (currentAmount >= MAX_CONCRETE_AMOUNT) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_concrete_full"));
            }
            return EnumActionResult.FAIL;
        }

        int durability = state.getValue(BlockWetConcreteBucketPlaced.DURABILITY);

        if (durability > 0) {
            // 计算可以提取的量
            int extractAmount = Math.min(MAX_CONCRETE_AMOUNT - currentAmount, durability);

            // 减少桶的耐久度
            world.setBlockState(pos, state.withProperty(BlockWetConcreteBucketPlaced.DURABILITY, durability - extractAmount));

            // 增加抹子的混凝土量
            setConcreteAmount(stack, currentAmount + extractAmount);
            setHasConcrete(stack, true);

            // 播放声音
            world.playSound(player, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

            // 消耗抹子耐久度
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(extractAmount, player);
            }

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_got_concrete",
                        currentAmount + extractAmount, MAX_CONCRETE_AMOUNT));
            }

            return EnumActionResult.SUCCESS;
        } else {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.bucket_empty"));
            }
            return EnumActionResult.FAIL;
        }
    }

    // 填充混凝土框架 - 改进：消耗混凝土量
    private EnumActionResult fillConcreteFrame(EntityPlayer player, World world, BlockPos pos, IBlockState oldState, ItemStack stack) {
        // 检查抹子是否有足够水泥
        if (getConcreteAmount(stack) < 1) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_no_concrete"));
            }
            return EnumActionResult.FAIL;
        }

        Block oldBlock = oldState.getBlock();
        Block wetBlock = EMPTY_TO_WET_MAP.get(oldBlock);

        if (wetBlock != null) {
            // 创建新状态，保留原有状态的所有属性
            IBlockState newState = copyBlockStateProperties(oldState, wetBlock.getDefaultState());
            world.setBlockState(pos, newState);

            // 减少混凝土量
            setConcreteAmount(stack, getConcreteAmount(stack) - 1);
            if (getConcreteAmount(stack) <= 0) {
                setHasConcrete(stack, false);
            }

            // 播放填充声音
            world.playSound(player, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_used_concrete", getConcreteAmount(stack)));
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 快速固化混凝土框架 - 新功能：直接填充并固化
    private EnumActionResult quickCureConcreteFrame(EntityPlayer player, World world, BlockPos pos, IBlockState oldState, ItemStack stack) {
        // 检查抹子是否有足够水泥（快速固化消耗更多）
        if (getConcreteAmount(stack) < 2) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_insufficient_concrete"));
            }
            return EnumActionResult.FAIL;
        }

        Block oldBlock = oldState.getBlock();
        Block solidBlock = EMPTY_TO_SOLID_MAP.get(oldBlock);

        if (solidBlock != null) {
            // 创建新状态，保留原有状态的所有属性
            IBlockState newState = copyBlockStateProperties(oldState, solidBlock.getDefaultState());
            world.setBlockState(pos, newState);

            // 减少混凝土量（消耗2单位）
            setConcreteAmount(stack, getConcreteAmount(stack) - 2);
            if (getConcreteAmount(stack) <= 0) {
                setHasConcrete(stack, false);
            }

            // 播放固化声音
            world.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.7F, 1.2F);

            // 额外消耗耐久度
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(3, player);
            }

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_quick_cured", getConcreteAmount(stack)));
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 平滑混凝土表面
    private EnumActionResult smoothConcrete(EntityPlayer player, World world, BlockPos pos, IBlockState oldState, ItemStack stack) {
        Block oldBlock = oldState.getBlock();
        Block solidBlock = WET_TO_SOLID_MAP.get(oldBlock);

        if (solidBlock != null) {
            IBlockState newState = copyBlockStateProperties(oldState, solidBlock.getDefaultState());
            world.setBlockState(pos, newState);

            // 播放平滑声音
            world.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.7F, 1.2F);

            // 消耗抹子耐久度
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(2, player);
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 给其他方块涂湿混凝土表面 - 改进：消耗混凝土量
    private EnumActionResult applyConcreteSurface(EntityPlayer player, World world, BlockPos pos, IBlockState oldState, ItemStack stack) {
        // 检查抹子是否有足够水泥
        if (getConcreteAmount(stack) < 1) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_no_concrete"));
            }
            return EnumActionResult.FAIL;
        }

        Block oldBlock = oldState.getBlock();
        Block surfaceBlock = BLOCK_TO_SURFACE_MAP.get(oldBlock);

        if (surfaceBlock != null) {
            IBlockState newState = surfaceBlock.getDefaultState();
            world.setBlockState(pos, newState);

            // 保存原方块信息到TileEntity
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof BlockWetConcreteSurface.TileEntityWetConcreteSurface) {
                BlockWetConcreteSurface.TileEntityWetConcreteSurface surfaceTE = (BlockWetConcreteSurface.TileEntityWetConcreteSurface) te;
                surfaceTE.setOriginalBlock(oldState);
            }

            // 减少混凝土量
            setConcreteAmount(stack, getConcreteAmount(stack) - 1);
            if (getConcreteAmount(stack) <= 0) {
                setHasConcrete(stack, false);
            }

            // 播放涂抹声音
            world.playSound(player, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

            // 消耗抹子耐久度
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
            }

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_applied_surface", getConcreteAmount(stack)));
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 恢复原方块
    private EnumActionResult restoreOriginalBlock(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof BlockWetConcreteSurface.TileEntityWetConcreteSurface) {
            BlockWetConcreteSurface.TileEntityWetConcreteSurface surfaceTE = (BlockWetConcreteSurface.TileEntityWetConcreteSurface) te;
            IBlockState originalState = surfaceTE.getOriginalBlock();

            if (originalState != null) {
                world.setBlockState(pos, originalState);
                world.playSound(player, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.7F, 1.0F);

                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }

                if (!world.isRemote) {
                    player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_restored_block"));
                }

                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
    }

    // 添加混凝土纹理
    private EnumActionResult addConcreteDetails(EntityPlayer player, World world, BlockPos pos,
                                                EnumFacing facing, ItemStack stack) {
        // 播放细节添加声音
        world.playSound(player, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 0.5F, 1.5F);

        // 消耗抹子耐久度
        if (!player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
        }

        // 发送粒子效果（客户端）
        if (world.isRemote) {
            spawnDetailParticles(world, pos, facing);
        }

        if (!world.isRemote) {
            player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_added_details"));
        }

        return EnumActionResult.SUCCESS;
    }

    // 雕刻混凝土表面 - 新功能
    private EnumActionResult sculptConcrete(EntityPlayer player, World world, BlockPos pos,
                                            EnumFacing facing, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // 检查是否可以雕刻（硬化的混凝土方块）
        if (block == BlockRegistryHandler.BLOCK_HARDENED_CONCRETE ||
                block == BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE) {

            // 播放雕刻声音
            world.playSound(player, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 0.7F, 0.8F);

            // 消耗更多耐久度
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(3, player);
            }

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_sculpted"));
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 修复裂缝混凝土 - 新功能
    private EnumActionResult repairCrackedConcrete(EntityPlayer player, World world, BlockPos pos,
                                                   IBlockState oldState, ItemStack stack) {
        Block oldBlock = oldState.getBlock();

        // 这里假设有对应的未裂缝版本
        Block repairedBlock = getRepairedVersion(oldBlock);

        if (repairedBlock != null && getConcreteAmount(stack) >= 1) {
            IBlockState newState = copyBlockStateProperties(oldState, repairedBlock.getDefaultState());
            world.setBlockState(pos, newState);

            // 减少混凝土量
            setConcreteAmount(stack, getConcreteAmount(stack) - 1);
            if (getConcreteAmount(stack) <= 0) {
                setHasConcrete(stack, false);
            }

            // 播放修复声音
            world.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("message.cementmod.trowel_repaired_crack"));
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    // 辅助方法：获取修复后的方块版本
    private Block getRepairedVersion(Block crackedBlock) {
        if (crackedBlock == BlockRegistryHandler.BLOCK_CRACKED_HARDENED_CONCRETE) {
            return BlockRegistryHandler.BLOCK_HARDENED_CONCRETE;
        }
        // 可以添加更多裂缝方块的修复映射
        return null;
    }

    // 辅助方法：检查是否是裂缝混凝土
    private boolean isCrackedConcrete(Block block) {
        return block == BlockRegistryHandler.BLOCK_CRACKED_HARDENED_CONCRETE;
        // 可以添加更多裂缝混凝土方块的检查
    }

    // 辅助方法：检查是否可雕刻
    private boolean isSculptableBlock(Block block) {
        return block == BlockRegistryHandler.BLOCK_HARDENED_CONCRETE ||
                block == BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE ||
                isWetConcreteBlock(block) ||
                WET_TO_SOLID_MAP.containsValue(block);
    }

    // 辅助方法：复制方块状态属性
    private IBlockState copyBlockStateProperties(IBlockState sourceState, IBlockState targetState) {
        IBlockState resultState = targetState;

        for (IProperty<?> property : sourceState.getPropertyKeys()) {
            if (resultState.getProperties().containsKey(property)) {
                resultState = copyPropertyValue(property, sourceState, resultState);
            }
        }

        return resultState;
    }

    // 辅助方法：复制单个属性值
    private <T extends Comparable<T>> IBlockState copyPropertyValue(IProperty<T> property, IBlockState sourceState, IBlockState targetState) {
        return targetState.withProperty(property, sourceState.getValue(property));
    }

    @SideOnly(Side.CLIENT)
    private void spawnDetailParticles(World world, BlockPos pos, EnumFacing facing) {
        // 生成粒子效果表示添加了细节
        // 实际实现会根据您的粒子系统而定
    }

    // 检查抹子是否有水泥
    private boolean hasConcrete(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(HAS_CONCRETE_TAG)) {
            return false;
        }
        return stack.getTagCompound().getBoolean(HAS_CONCRETE_TAG);
    }

    // 设置抹子的水泥状态
    private void setHasConcrete(ItemStack stack, boolean hasConcrete) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean(HAS_CONCRETE_TAG, hasConcrete);
    }

    // 获取混凝土量
    private int getConcreteAmount(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(CONCRETE_AMOUNT_TAG)) {
            return 0;
        }
        return stack.getTagCompound().getInteger(CONCRETE_AMOUNT_TAG);
    }

    // 设置混凝土量
    private void setConcreteAmount(ItemStack stack, int amount) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(CONCRETE_AMOUNT_TAG, Math.max(0, Math.min(amount, MAX_CONCRETE_AMOUNT)));

        // 自动更新hasConcrete状态
        if (amount > 0) {
            setHasConcrete(stack, true);
        } else if (amount == 0) {
            setHasConcrete(stack, false);
        }
    }

    // 检查是否是空的混凝土框架
    private boolean isConcreteFrameEmpty(Block block) {
        return EMPTY_TO_WET_MAP.containsKey(block);
    }

    // 检查是否是湿润的混凝土框架
    private boolean isWetConcreteBlock(Block block) {
        return WET_TO_SOLID_MAP.containsKey(block);
    }

    // 检查是否可以转换为湿混凝土表面
    private boolean canConvertToSurface(Block block) {
        return BLOCK_TO_SURFACE_MAP.containsKey(block);
    }

    // 检查是否是混凝土类方块
    private boolean isConcreteBlock(Block block) {
        return block == BlockRegistryHandler.BLOCK_HARDENED_CONCRETE ||
                block == BlockRegistryHandler.BLOCK_REINFORCED_CONCRETE ||
                isConcreteFrameEmpty(block) ||
                isWetConcreteBlock(block) ||
                EMPTY_TO_WET_MAP.containsValue(block) ||
                WET_TO_SOLID_MAP.containsValue(block) ||
                block == BlockRegistryHandler.BLOCK_WET_CONCRETE_SURFACE;
    }

    // 获取当前模式
    private TrowelMode getMode(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(MODE_TAG)) {
            return TrowelMode.PLACEMENT;
        }

        try {
            return TrowelMode.valueOf(stack.getTagCompound().getString(MODE_TAG));
        } catch (IllegalArgumentException e) {
            return TrowelMode.PLACEMENT;
        }
    }

    // 切换模式
    private void cycleMode(ItemStack stack) {
        TrowelMode currentMode = getMode(stack);
        TrowelMode nextMode = TrowelMode.getNextMode(currentMode);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setString(MODE_TAG, nextMode.name());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        // 添加模式信息
        TrowelMode mode = getMode(stack);
        tooltip.add(I18n.format("tooltip.cementmod.trowel_mode") + ": " +
                I18n.format("tooltip.cementmod.trowel_mode." + mode.getName()));

        // 添加混凝土量信息
        int concreteAmount = getConcreteAmount(stack);
        if (concreteAmount > 0) {
            tooltip.add(I18n.format("tooltip.cementmod.trowel_concrete_amount", concreteAmount, MAX_CONCRETE_AMOUNT));
        } else {
            tooltip.add(I18n.format("tooltip.cementmod.trowel_no_concrete_status"));
        }

        // 添加使用说明
        tooltip.add(I18n.format("tooltip.cementmod.trowel_usage"));

        // 添加模式特定说明
        switch (mode) {
            case PLACEMENT:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.placement.desc"));
                break;
            case SMOOTHING:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.smoothing.desc"));
                break;
            case QUICK_CURE:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.quick_cure.desc"));
                break;
            case DETAILING:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.detailing.desc"));
                break;
            case RESTORING:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.restoring.desc"));
                break;
            case SCULPTING:
                tooltip.add(I18n.format("tooltip.cementmod.trowel_mode.sculpting.desc"));
                break;
        }

        // 添加通用说明
        tooltip.add(I18n.format("tooltip.cementmod.trowel_any_mode_concrete"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) stack.getItemDamage() / (double) MAX_DURABILITY;
    }

    // 新增：显示混凝土量的叠加条
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        // 当有混凝土时显示蓝色，否则显示默认的耐久颜色
        if (getConcreteAmount(stack) > 0) {
            return 0x3498db; // 蓝色
        }
        return super.getRGBDurabilityForDisplay(stack);
    }
}