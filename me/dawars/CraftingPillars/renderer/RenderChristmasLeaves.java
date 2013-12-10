package me.dawars.CraftingPillars.renderer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import me.dawars.CraftingPillars.CraftingPillars;
import me.dawars.CraftingPillars.blocks.ChristmasLeavesBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderChristmasLeaves extends BlockRenderingHelper implements ISimpleBlockRenderingHandler
{
	@Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        
        drawBlock(renderer, block, block.getIcon(block.blockID, metadata));
        drawBlock(renderer, block, ((ChristmasLeavesBlock) block).glowing);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
	
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
    	int bb = setBrightness(world, x, y, z, block);

        Tessellator t = Tessellator.instance;
        t.setBrightness(160);


        Icon tex = ((ChristmasLeavesBlock) block).glowing;
        
        renderer.renderFaceXPos(block, x, y, z, tex);
		renderer.renderFaceXNeg(block, x, y, z, tex);
		renderer.renderFaceZPos(block, x, y, z, tex);
		renderer.renderFaceZNeg(block, x, y, z, tex);
		renderer.renderFaceYPos(block, x, y, z, tex);
		renderer.renderFaceYNeg(block, x, y, z, tex);
		
        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
	        
        return true;
    }
    
    protected static int setBrightness(IBlockAccess blockAccess, int i, int j, int k, Block block) {
		int brightness = block.getMixedBrightnessForBlock(blockAccess, i, j, k);

		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess,
				i, j, k));

		float f = 1.0F;

		int l = block.colorMultiplier(blockAccess, i, j, k);
		float f1 = (float) (l >> 16 & 255) / 255.0F;
		float f2 = (float) (l >> 8 & 255) / 255.0F;
		float f3 = (float) (l & 255) / 255.0F;
		float f4;

		if (EntityRenderer.anaglyphEnable) {
			float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f5;
			f2 = f4;
			f3 = f6;
		}

		tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
		return brightness;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}
	
	@Override
	public int getRenderId() {
		return CraftingPillars.christmasLeavesRenderID;
	}


}
