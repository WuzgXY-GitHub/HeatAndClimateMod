package defeatedcrow.hac.main.api.orevein;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.main.worldgen.OreSetDC;
import net.minecraft.block.Block;

public class VeinTable implements IVeinTable {

	public final List<OreSet> table = new ArrayList<OreSet>();
	public OreSet layerStone;
	public final EnumVein vein;
	public int tableCount = 1;

	public VeinTable(EnumVein veinIn, @Nonnull OreSet layer1) {
		layerStone = layer1;
		vein = veinIn;
	}

	public void addOreToTable1(OreSet... stacks) {
		for (OreSet ore : stacks) {
			if (ore.getWeight() > 0) {
				table.add(ore);
				tableCount += ore.getWeight();
			}
		}
	}

	/* IVeinTable */

	@Override
	public OreSet getLayerBlock() {
		return layerStone;
	}

	@Override
	public EnumVein getType() {
		return vein;
	}

	@Override
	public List<OreSet> getOreTable() {
		List<OreSet> ret = ImmutableList.copyOf(table);
		return ret;
	}

	@Override
	public void addOreToTable(int weight, Block block, int meta) {
		if (block != null && weight > 0) {
			BlockSet set = new BlockSet(block, meta);
			table.add(new OreSetDC(weight, set));
			tableCount += weight;
		}
	}

	@Override
	public void addOreToTable(int weight, Block block, int meta, Block secondBlock, int secondMeta, int secondChance) {
		if (block != null && secondBlock != null && weight > 0) {
			BlockSet set = new BlockSet(block, meta);
			BlockSet set2 = new BlockSet(block, meta);
			table.add(new OreSetDC(weight, set, set2, secondChance));
			tableCount += weight;
		}
	}

	@Override
	public void removeOreFromTable(Block block, int meta) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			List<OreSet> removes = Lists.newArrayList();
			for (OreSet target : table) {
				if (target.getOre().equals(set)) {
					removes.add(target);
				}
			}

			for (OreSet del : removes) {
				table.remove(del);
				tableCount -= del.getWeight();
			}
		}
	}

}
