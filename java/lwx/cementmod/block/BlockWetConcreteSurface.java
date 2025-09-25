package lwx.cementmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWetConcreteSurface extends Block implements ITileEntityProvider {

    public BlockWetConcreteSurface() {
        super(Material.CLAY);
        this.setUnlocalizedName("wet_concrete_surface");
        this.setRegistryName("wet_concrete_surface");
        this.setHardness(0.8F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityWetConcreteSurface();
    }

    public static class TileEntityWetConcreteSurface extends TileEntity {

        private IBlockState originalBlock;

        // 必须有无参构造函数
        public TileEntityWetConcreteSurface() {}

        public void setOriginalBlock(IBlockState state) {
            this.originalBlock = state;
            this.markDirty();
        }

        public IBlockState getOriginalBlock() {
            return originalBlock;
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            if (originalBlock != null) {
                NBTTagCompound blockTag = new NBTTagCompound();
                Block block = originalBlock.getBlock();
                int meta = block.getMetaFromState(originalBlock);
                blockTag.setString("block", block.getRegistryName().toString());
                blockTag.setInteger("meta", meta);
                compound.setTag("OriginalBlock", blockTag);
            }
            return compound;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            if (compound.hasKey("OriginalBlock")) {
                NBTTagCompound blockTag = compound.getCompoundTag("OriginalBlock");
                String blockName = blockTag.getString("block");
                int meta = blockTag.getInteger("meta");
                Block block = Block.getBlockFromName(blockName);
                if (block != null) {
                    originalBlock = block.getStateFromMeta(meta);
                }
            }
        }

        // 添加获取更新标签的方法
        @Override
        public NBTTagCompound getUpdateTag() {
            return writeToNBT(new NBTTagCompound());
        }

        @Override
        public SPacketUpdateTileEntity getUpdatePacket() {
            return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
        }

        @Override
        public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
            readFromNBT(pkt.getNbtCompound());
        }
    }
}