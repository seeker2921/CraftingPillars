package me.dawars.CraftingPillars.renderer;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.util.List;

import javax.swing.Renderer;

import org.lwjgl.opengl.GL11;

import com.google.common.primitives.SignedBytes;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import me.dawars.CraftingPillars.CraftingPillars;
import me.dawars.CraftingPillars.client.CustomParticle;
import me.dawars.CraftingPillars.tiles.TileEntityBrewingPillar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class RenderBrewingPillar extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler
{
	private static final ResourceLocation TEXTURE_BREWINGPILLAR = new ResourceLocation(CraftingPillars.id + ":textures/models/brewingPillar.png");
	
	public static ModelBase model = new ModelBase()
	{
		
	};
	
	private ModelRenderer CraftingBottom;
	private ModelRenderer CraftingBotSlab;
	private ModelRenderer Pillar;
	private ModelRenderer WorkbenchSlab;
	private ModelRenderer WorkbenchTop;
	private ModelRenderer Holder1;
	private ModelRenderer Holder2;
	private ModelRenderer Holder3;
	private ModelRenderer Holder4;
	
	private ModelRenderer pillarBottom;
    
	private RenderingHelper.ItemRender itemRenderer;
//	private RenderingHelper.ItemRender resultRenderer;
	
	public RenderBrewingPillar()
	{
		itemRenderer = new RenderingHelper.ItemRender(false, false);
//		resultRenderer = new RenderingHelper.ItemRender(false, false);
		
		model.textureWidth = 128;
		model.textureHeight = 64;
		
		pillarBottom = new ModelRenderer(model, 0, 33);
		pillarBottom.addBox(0F, 0F, 0F, 12, 3, 12);
		pillarBottom.setRotationPoint(-6F, 21F, 6F);
		pillarBottom.setTextureSize(128, 64);
		pillarBottom.mirror = true;
		setRotation(pillarBottom, 0F, 1.570796F, 0F);
		
		
		CraftingBottom = new ModelRenderer(model, 0, 0);
	      CraftingBottom.addBox(0F, 0F, 0F, 16, 2, 16);
	      CraftingBottom.setRotationPoint(-8F, 22F, -8F);
	      CraftingBottom.setTextureSize(128, 64);
	      CraftingBottom.mirror = true;
	      setRotation(CraftingBottom, 0F, 0F, 0F);
	      CraftingBotSlab = new ModelRenderer(model, 0, 18);
	      CraftingBotSlab.addBox(0F, 0F, 0F, 14, 1, 14);
	      CraftingBotSlab.setRotationPoint(-7F, 21F, -7F);
	      CraftingBotSlab.setTextureSize(128, 64);
	      CraftingBotSlab.mirror = true;
	      setRotation(CraftingBotSlab, 0F, 0F, 0F);
	      Pillar = new ModelRenderer(model, 0, 39);
	      Pillar.addBox(0F, 0F, 0F, 6, 10, 6);
	      Pillar.setRotationPoint(-3F, 11F, -3F);
	      Pillar.setTextureSize(128, 64);
	      Pillar.mirror = true;
	      setRotation(Pillar, 0F, 0F, 0F);
	      WorkbenchSlab = new ModelRenderer(model, 0, 46);
	      WorkbenchSlab.addBox(0F, 0F, 0F, 8, 1, 8);
	      WorkbenchSlab.setRotationPoint(-4F, 10F, -4F);
	      WorkbenchSlab.setTextureSize(128, 64);
	      WorkbenchSlab.mirror = true;
	      setRotation(WorkbenchSlab, 0F, 0F, 0F);
	      WorkbenchTop = new ModelRenderer(model, 73, 3);
	      WorkbenchTop.addBox(0F, 0F, 0F, 10, 2, 10);
	      WorkbenchTop.setRotationPoint(-5F, 8F, -5F);
	      WorkbenchTop.setTextureSize(128, 64);
	      WorkbenchTop.mirror = true;
	      setRotation(WorkbenchTop, 0F, 0F, 0F);
	      Holder2 = new ModelRenderer(model, 0, 0);
	      Holder2.addBox(0F, 0F, 0F, 1, 1, 3);
	      Holder2.setRotationPoint(-0.5F, 13F, 3F);
	      Holder2.setTextureSize(128, 64);
	      Holder2.mirror = true;
	      setRotation(Holder2, 0F, 0F, 0F);
	      Holder1 = new ModelRenderer(model, 0, 0);
	      Holder1.addBox(0F, 0F, 0F, 3, 1, 1);
	      Holder1.setRotationPoint(3F, 13F, -0.5F);
	      Holder1.setTextureSize(128, 64);
	      Holder1.mirror = true;
	      setRotation(Holder1, 0F, 0F, 0F);
	      Holder4 = new ModelRenderer(model, 0, 0);
	      Holder4.addBox(-3F, 0F, 0F, 3, 1, 1);
	      Holder4.setRotationPoint(-3F, 13F, -0.5F);
	      Holder4.setTextureSize(128, 64);
	      Holder4.mirror = true;
	      setRotation(Holder4, 0F, 0F, 0F);
	      Holder3 = new ModelRenderer(model, 0, 0);
	      Holder3.addBox(0F, 0F, -3F, 1, 1, 3);
	      Holder3.setRotationPoint(-0.5F, 13F, -3F);
	      Holder3.setTextureSize(128, 64);
	      Holder3.mirror = true;
	      setRotation(Holder3, 0F, 0F, 0F);
	}
	
	public void render(float f, boolean connected)
	{
		if(connected)
		{
			pillarBottom.render(f);
		}
		else
		{
			CraftingBottom.render(f);
			CraftingBotSlab.render(f);
		}
	    Pillar.render(f);
	    WorkbenchSlab.render(f);
	    WorkbenchTop.render(f);
	    Holder2.render(f);
	    Holder1.render(f);
	    Holder4.render(f);
	    Holder3.render(f);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
	{
		glPushMatrix();
		glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
		glRotatef(180F, 1F, 0F, 0F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_BREWINGPILLAR);
		render(0.0625F, tile.worldObj.getBlockId(tile.xCoord, tile.yCoord-1, tile.zCoord) == CraftingPillars.blockExtendPillar.blockID);
		glPopMatrix();
		
		TileEntityBrewingPillar pillarTile = (TileEntityBrewingPillar) tile;
		EntityItem citem = new EntityItem(tile.worldObj);
		
		glPushMatrix();
		
			glTranslated(x+0.5D, y, z+0.5D);

			
		
			//Input
			if(pillarTile.getStackInSlot(4) != null)
			{
				glPushMatrix();
					glScalef(1.1F,  1.1F,  1.1F);
					citem.hoverStart = -pillarTile.rot;
					citem.setEntityItemStack(pillarTile.getStackInSlot(4));
					itemRenderer.render(citem, 0F, 1F, 0F, pillarTile.showNum);
				glPopMatrix();
			}
			
			//Bottles
			for(int i = 0; i < 4; i++)
			{
				if(pillarTile.getStackInSlot(i) != null)
				{
					int rotI = i;
					if(i == 3) rotI = 0;
					if(i == 0) rotI = 3;
					glPushMatrix();
						//rotate i * pi / 2 rad (i * 90 degree)
						glRotatef(-rotI*90, 0, 1, 0);
						glTranslatef(0.4F, 0F, 0F);
						
						citem.hoverStart = 0F;
						citem.setEntityItemStack(pillarTile.getStackInSlot(i));
						itemRenderer.render(citem, 0, 0.45F, 0, false);
						
						//processed item
						if (pillarTile.canBrew() && pillarTile.getBrewTime() > 0) {
		                    int j = pillarTile.getStackInSlot(i).getItemDamage();
		                    int k = TileEntityBrewingPillar.getPotionResult(j, pillarTile.getStackInSlot(4));
		                    List list = Item.potion.getEffects(j);
		                    List list1 = Item.potion.getEffects(k);
		                    if (((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null)) || !ItemPotion.isSplash(j) && ItemPotion.isSplash(k))
		                    {
	                        	glPushMatrix();
//									glTranslatef(-(float) (pillarTile.getBrewTime()/ 350F), 0.2F + (float) Math.sqrt(pillarTile.getBrewTime()/ 350F), 0);
									glTranslatef(-.4F, 0, 0);
									glScalef(0.5F, 0.5F, 0.5F);

									citem.hoverStart = 0F;
									ItemStack processedItem = pillarTile.getStackInSlot(4).copy();
									processedItem.stackSize = 1;
									citem.setEntityItemStack(processedItem);
									itemRenderer.render(citem, -.1F + (float)Math.sqrt((1.3F-(pillarTile.getBrewTime()/ 350F))/* *0.8F*/)*.8F, 1.5F + pillarTile.getBrewTime()/ 350F, 0.01F, false);
								glPopMatrix();
		                    }
		                }
					glPopMatrix();
					
					float subX = 0;
						float subZ = 0;

						if(rotI == 0)
							subX = 0.4F;
						if(rotI == 2)
							subX = -0.4F;
						if(rotI == 1)
							subZ = 0.4F;
						if(rotI == 3)
							subZ = -0.4F;
						
					if(pillarTile.showNum)
					{
						glPushMatrix();
							glTranslatef(subX, 0F, subZ);
							
							glDisable(GL_LIGHTING);
							RenderingHelper.renderFloatingTextWithBackground(0, 0.8F, 0, 0.1F, pillarTile.getStackInSlot(i).getDisplayName(), Color.WHITE.getRGB(), new Color(0F, 0F, 0F, 0.5F));
							glEnable(GL_LIGHTING);

						glPopMatrix();

					}
				}
			}
			
		glPopMatrix();
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		glPushMatrix();
		glTranslated(0, 1.0D, 0);
		glRotatef(180F, 1F, 0F, 0F);
		glRotatef(90F, 0F, 1F, 0F);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_BREWINGPILLAR);
		render(0.0625F, false);
		glPopMatrix();
	}
	
	@Override
	// No TileEntity here can't use
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		
		return false;
	}
	
	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}
	
	@Override
	public int getRenderId()
	{
		return CraftingPillars.brewingillarRenderID;
	}
}
