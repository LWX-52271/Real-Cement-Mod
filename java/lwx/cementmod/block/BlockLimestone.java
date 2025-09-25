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

public class BlockLimestone extends Block
{
    public BlockLimestone()
    {
        super(Material.ROCK);
        this.setUnlocalizedName(CementMod.MODID + ".limestone");
        this.setRegistryName("limestone");
        this.setHarvestLevel("pickaxe",2);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setCreativeTab(TabCementMod.TAB_CEMENT_MOD);
    }

    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        // 类似石头的音效但更清脆
        return new SoundType(1.2F, 1.0F,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundEvents.BLOCK_STONE_STEP,
                SoundEvents.BLOCK_STONE_PLACE,
                SoundEvents.BLOCK_STONE_HIT,
                SoundEvents.BLOCK_STONE_FALL
        );
    }
}
