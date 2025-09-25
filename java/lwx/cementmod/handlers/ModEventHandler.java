package lwx.cementmod.handlers;

import lwx.cementmod.block.BlockConcreteCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModEventHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) return; // 只在服务器端处理

        BlockPos pos = event.getPos();
        IBlockState state = event.getWorld().getBlockState(pos);
        EntityPlayer player = event.getEntityPlayer();

        // 检查是否是右键原版空炼药锅
        if (state.getBlock() == Blocks.CAULDRON && state.getValue(BlockCauldron.LEVEL) == 0) {
            // 尝试转换为自定义炼药锅
            if (BlockConcreteCauldron.convertVanillaCauldron(event.getWorld(), pos, player)) {
                event.setCanceled(true); // 取消后续默认行为（如放置沙子）
            }
        }
    }
}