package lwx.cementmod.block;

import lwx.cementmod.CementMod;
import lwx.cementmod.CreativeTabs.TabCementMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockDeepClay extends Block
{
    public BlockDeepClay()
    {
        super(Material.CLAY);
        this.setUnlocalizedName(CementMod.MODID + ".deepclay");
        this.setRegistryName("deep_clay");
        this.setHarvestLevel("shovel",2);
        this.setHardness(0.5F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
        this.setSoundType(SoundType.GLASS);
    }

    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        // 使用自定义的黏土音效类型
        return new SoundType(1.0F, 1.0F,
                SoundEvents.BLOCK_GRAVEL_BREAK,
                SoundEvents.BLOCK_GRAVEL_STEP,
                SoundEvents.BLOCK_GRAVEL_PLACE,
                SoundEvents.BLOCK_GRAVEL_HIT,
                SoundEvents.BLOCK_GRAVEL_FALL
        );
    }
}
