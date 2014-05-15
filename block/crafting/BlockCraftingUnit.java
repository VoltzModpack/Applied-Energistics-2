package appeng.block.crafting;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockCrafting;
import appeng.client.texture.ExtraTextures;
import appeng.core.features.AEFeature;
import appeng.tile.crafting.TileCraftingTile;

public class BlockCraftingUnit extends AEBaseBlock
{

	public final static int BASE_DAMAGE = 0;
	public final static int BASE_MONITOR = 1;
	public final static int BASE_STORAGE = 2;
	public final static int BASE_ACCELERATOR = 3;

	static public boolean checkType(int meta, int type)
	{
		return (meta & 7) == type;
	}

	public BlockCraftingUnit() {
		super( BlockCraftingUnit.class, Material.iron );
		hasSubtypes = true;
		setfeature( EnumSet.of( AEFeature.Crafting ) );
		setTileEntiy( TileCraftingTile.class );
	}

	@Override
	public IIcon getIcon(int direction, int metadata)
	{
		switch (metadata)
		{
		case BASE_MONITOR:
			if ( direction == 0 )
				return ExtraTextures.BlockCraftingStorageMonitor.getIcon();
			break;
		case BASE_STORAGE:
			return ExtraTextures.BlockCraftingStorage1k.getIcon();
		case BASE_ACCELERATOR:
			return ExtraTextures.BlockCraftingAccelerator.getIcon();
		case BASE_MONITOR | 8:
			if ( direction == 0 )
				return ExtraTextures.BlockCraftingStorageMonitorFit.getIcon();
			break;
		case BASE_STORAGE | 8:
			return ExtraTextures.BlockCraftingStorage1kFit.getIcon();
		case BASE_ACCELERATOR | 8:
			return ExtraTextures.BlockCraftingAcceleratorFit.getIcon();
		case BASE_DAMAGE | 8:
			return ExtraTextures.BlockCraftingUnitFit.getIcon();
		}

		return super.getIcon( direction, metadata );
	}

	@Override
	protected Class<? extends BaseBlockRender> getRenderer()
	{
		return RenderBlockCrafting.class;
	}

	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, Block junk)
	{
		TileCraftingTile cp = getTileEntity( w, x, y, z );
		if ( cp != null )
			cp.updateMultiBlock();
	}

	public ItemStack getItemStack(World world, int x, int y, int z)
	{
		TileCraftingTile ct = getTileEntity( world, x, y, z );

		int meta = world.getBlockMetadata( x, y, z ) & 7;
		if ( ct != null && meta == BASE_STORAGE )
		{
			return createStackForBytes( ct.getStorageBytes() );
		}

		return new ItemStack( this, 1, meta );
	}

	private ItemStack createStackForBytes(long storageBytes)
	{
		ItemStack itemDetails = new ItemStack( this, 1, BASE_STORAGE );
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong( "bytes", storageBytes );
		itemDetails.setTagCompound( tag );
		return itemDetails;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> out = new ArrayList();

		ItemStack is = getItemStack( world, x, y, z );
		if ( is != null )
			out.add( is );

		return out;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return getItemStack( world, x, y, z );
	}

	@Override
	public void getSubBlocks(Item i, CreativeTabs c, List l)
	{
		l.add( new ItemStack( this, 1, BASE_DAMAGE ) );
		l.add( new ItemStack( this, 1, BASE_MONITOR ) );
		l.add( new ItemStack( this, 1, BASE_ACCELERATOR ) );
		l.add( createStackForBytes( 1024 ) );
		l.add( createStackForBytes( 1024 * 4 ) );
		l.add( createStackForBytes( 1024 * 16 ) );
		l.add( createStackForBytes( 1024 * 64 ) );
	}

	@Override
	public Class getItemBlockClass()
	{
		return ItemBlockCraftingUnit.class;
	}

}