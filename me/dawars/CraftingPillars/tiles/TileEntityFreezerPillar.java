package me.dawars.CraftingPillars.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import me.dawars.CraftingPillars.Blobs;
import me.dawars.CraftingPillars.CraftingPillars;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityFreezerPillar extends BaseTileEntity implements IInventory, ISidedInventory, IFluidHandler
{
	private ItemStack[] inventory = new ItemStack[this.getSizeInventory()];
	public final FluidTank tank = new FluidTank((int) FluidContainerRegistry.BUCKET_VOLUME * 16);

	public int freezingTime;
	public boolean showNum = false;
	public boolean isEmpty = true;

	public List<Blobs> blobs;
	
	public TileEntityFreezerPillar()
	{
		this.blobs = new ArrayList<Blobs>();
	}
	
	public void addBlob()
	{
		blobs.add(new Blobs(random.nextInt(12)+2.5F, random.nextInt(9)+4.5F, random.nextInt(12)+2.5F, 4));
	}
	
	public void removeBlob()
	{
		blobs.remove(random.nextInt(blobs.size()));
	}
	public int[][][] texIndieces = null;
	
	@Override
	public void updateEntity()
	{
		if(worldObj.isRemote)
		{
			if(this.texIndieces == null)
			{
				this.texIndieces = new int[16][16][16];
				for(int i = 0; i < 16; i++)
					for(int j = 0; j < 16; j++)
						for(int k = 0; k < 16; k++)
							this.texIndieces[i][j][k] = random.nextInt(256);
			}
			EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
			if((player.posX-this.xCoord) * (player.posX-this.xCoord) + (player.posY-this.yCoord) * (player.posY-this.yCoord) + (player.posZ-this.zCoord) * (player.posZ-this.zCoord) < 128)
			{
				while(this.blobs.size() < this.tank.getFluidAmount()/FluidContainerRegistry.BUCKET_VOLUME)
					this.addBlob();
				while(this.blobs.size() > this.tank.getFluidAmount()/FluidContainerRegistry.BUCKET_VOLUME)
					this.removeBlob();
				
				int i = random.nextInt(16);
				int j = random.nextInt(16);
				int k = random.nextInt(16);
				this.texIndieces[i][j][k]++;
				this.texIndieces[i][j][k] %= 256;
				
				for(i = 0; i < this.blobs.size(); i++)
					this.blobs.get(i).update(0.1F);
			}
		}
//		else {
			if(canFreeze())
			{
				if(this.freezingTime > 0)
					this.freezingTime--;
				else
					freezeWater();
			} else {
				this.freezingTime = 150;
			}
//		}
		
		super.updateEntity();
	}

	public boolean canFreeze() {
		if(this.isEmpty)
			return false;
		if(this.tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME)
		{
			if(this.inventory[0] == null || this.inventory[0].stackSize < this.getInventoryStackLimit())
				return true;
		}
		return false;
	}

	
	private void freezeWater() {
		this.freezingTime = 150;

		if(!this.worldObj.isRemote)
		{
			ItemStack itemstack = new ItemStack(Block.ice, 1);
			if(this.inventory[0] == null)
				this.inventory[0] = itemstack.copy();
			else if(this.inventory[0].isItemEqual(itemstack))
				inventory[0].stackSize += itemstack.stackSize;
			
			this.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
			if(this.tank.getFluidAmount() <= 0)
				this.isEmpty = true;
			else
				this.isEmpty = false;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.onInventoryChanged();
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if(nbt.hasKey("tank"))
			this.tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("tank")));
		this.inventory = new ItemStack[this.getSizeInventory()];
		NBTTagList nbtlist = nbt.getTagList("Items");
		for(int i = 0; i < nbtlist.tagCount(); i++)
		{
			NBTTagCompound nbtslot = (NBTTagCompound) nbtlist.tagAt(i);
			int j = nbtslot.getByte("Slot") & 255;
			
			if((j >= 0) && (j < this.getSizeInventory()))
				this.inventory[j] = ItemStack.loadItemStackFromNBT(nbtslot);
		}
		
		this.freezingTime = nbt.getInteger("freezingTime");
		this.showNum = nbt.getBoolean("showNum");
		this.isEmpty = nbt.getBoolean("isEmpty");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if(tank.getFluid() != null)
			nbt.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
		NBTTagList nbtlist = new NBTTagList();
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			if(this.inventory[i] != null)
			{
				NBTTagCompound nbtslot = new NBTTagCompound();
				nbtslot.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(nbtslot);
				nbtlist.appendTag(nbtslot);
			}
		}
		nbt.setTag("Items", nbtlist);
		
		nbt.setInteger("freezingTime", this.freezingTime);
		nbt.setBoolean("showNum", this.showNum);
		nbt.setBoolean("isEmpty", this.isEmpty);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		NBTTagCompound nbt = pkt.data;
		this.readFromNBT(nbt);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();
		
		if(!this.worldObj.isRemote)
			CraftingPillars.proxy.sendToPlayers(this.getDescriptionPacket(), this.worldObj, this.xCoord, this.yCoord, this.zCoord, 64);
	}
	
	public void dropItemFromSlot(int slot, int amount, EntityPlayer player)
	{
		if(!this.worldObj.isRemote && this.getStackInSlot(slot) != null)
		{
			EntityItem itemEntity = new EntityItem(this.worldObj, player.posX, player.posY, player.posZ);
			itemEntity.setEntityItemStack(this.decrStackSize(slot, amount));
			this.worldObj.spawnEntityInWorld(itemEntity);
			
			this.onInventoryChanged();
		}
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.inventory[slot];
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.inventory[slot] = stack;
		
		if(stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.onInventoryChanged();
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = null;
		
		if(this.inventory[slot] != null)
		{
			if(this.inventory[slot].stackSize <= amount)
			{
				stack = this.inventory[slot];
				this.inventory[slot] = null;
				this.onInventoryChanged();
			}
			else
			{
				stack = this.inventory[slot].splitStack(amount);
				
				if(this.inventory[slot].stackSize == 0)
				{
					this.inventory[slot] = null;
				}
				
				this.onInventoryChanged();
			}
		}
		
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			this.setInventorySlotContents(slot, null);
		}
		
		return stack;
	}
	
	@Override
	public String getInvName()
	{
		return "Freezer Pillar";
	}
	
	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public void openChest()
	{
	}
	
	@Override
	public void closeChest()
	{
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[]{0};
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(doFill && resource.amount > 0)
			this.isEmpty = false;
		int res = this.tank.fill(resource, doFill);
		this.onInventoryChanged();
		return res;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return this.drain(from, resource.amount, doDrain);
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		FluidStack res = this.tank.drain(maxDrain, doDrain);
		if(this.tank.getFluidAmount() <= 0)
			this.isEmpty = true;
		this.onInventoryChanged();
		return res;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return new FluidTankInfo[] { tank.getInfo() };
	}
}
