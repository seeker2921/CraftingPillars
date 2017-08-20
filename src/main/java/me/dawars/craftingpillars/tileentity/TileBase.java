package me.dawars.craftingpillars.tileentity;

import me.dawars.craftingpillars.network.ITilePacketHandler;
import me.dawars.craftingpillars.network.PacketCraftingPillar;
import me.dawars.craftingpillars.network.PacketHandler;
import me.dawars.craftingpillars.network.PacketTile;
import me.dawars.craftingpillars.util.helpers.ServerHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Contains various functions from ThermalExpansion and Forestry.
 */
public class TileBase extends TileEntity implements ITilePacketHandler {
    private static final Random rand = new Random();


    protected final boolean updateOnInterval(int tickInterval) {
        return worldObj.getTotalWorldTime() % tickInterval == 0;
    }

    public void callBlockUpdate() {

        IBlockState state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

    public void markChunkDirty() {

        worldObj.markChunkDirty(pos, this);
    }

    @Override
    public void onChunkUnload() {

        if (!tileEntityInvalid) {
            invalidate();
        }
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    /* NETWORK METHODS */
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {

        return PacketHandler.toTilePacket(getTilePacket(), getPos());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

        PacketHandler.handleNBTPacket(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {

        return PacketHandler.toNBTTag(getTilePacket(), super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {

        PacketHandler.handleNBTPacket(tag);
    }


    /**
     * Construct a packet to send
     *
     * @return
     */
    public PacketCraftingPillar getTilePacket() {

        return new PacketTile(this);
    }

    @Override
    public void handleTilePacket(PacketCraftingPillar payload) {

    }


    public void sendTilePacket(Side side) {

        if (worldObj == null) {
            return;
        }
        if (side == Side.CLIENT && ServerHelper.isServerWorld(worldObj)) {
            PacketHandler.sendToAllAround(getTilePacket(), this);
        } else if (side == Side.SERVER && ServerHelper.isClientWorld(worldObj)) {
            PacketHandler.sendToServer(getTilePacket());
        }
    }

}
