package me.dawars.craftingpillars.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITilePacketHandler {

	@SideOnly (Side.CLIENT)
	void handleTilePacket(PacketCraftingPillar payload);

}
