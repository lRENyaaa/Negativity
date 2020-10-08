package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.XRAY;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.ray.BlockRay.BlockRayBuilder;
import com.elikill58.negativity.api.ray.BlockRay.RayResult;
import com.elikill58.negativity.api.ray.BlockRayResult;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.Negativity;

public class XRay extends Cheat implements Listeners {

	private static final List<Material> ORES = Arrays.asList(Materials.COAL_ORE, Materials.IRON_ORE, Materials.GOLD_ORE,
			Materials.DIAMOND_ORE, Materials.EMERALD_ORE, Materials.REDSTONE_ORE, Materials.QUARTZ_ORE, Materials.LAPIS_ORE);
	private static final List<Material> IMPORTANT_ORES = Arrays.asList(Materials.GOLD_ORE, Materials.DIAMOND_ORE,
			Materials.EMERALD_ORE, Materials.REDSTONE_ORE, Materials.QUARTZ_ORE, Materials.LAPIS_ORE);
	private static final List<Material> MINING_BLOCK = Arrays.asList(Materials.STONE, Materials.ANDESITE, Materials.GRANITE, Materials.DIORITE, Materials.GRAVEL);
	private static final long TIME_MINING = 10000;
	
	public XRay() {
		super(XRAY, CheatCategory.WORLD, Materials.EMERALD_ORE, false, false);
	}
	
	@EventListener
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		Block b = e.getBlock();
		
		// Minerate counter
		if(checkActive("minerate"))
			np.getAccount().getMinerate().addMine(MinerateType.fromId(e.getBlock().getType().getId()), p);
		
		if(checkActive("mining-direction")) {
			long time = System.currentTimeMillis();
			boolean isMining = (time - np.longs.get(XRAY, "is-mining", 0l)) < TIME_MINING;
			if(ORES.contains(b.getType())) {
				np.ints.set(XRAY, "mining-ore", 5);
			} else {
				int timeMiningOre = np.ints.get(XRAY, "mining-ore", 0);
				if(timeMiningOre > 0) {
					if(isMining) {
						BlockRayResult blockResult = new BlockRayBuilder(p.getLocation().add(0, 1.5, 0), p)
									.neededType(IMPORTANT_ORES.toArray(new Material[0])).build().compile();
						// search for ore
						Location loc = b.getLocation();
						if(blockResult.getRayResult().equals(RayResult.NEEDED_FOUND) && blockResult.hasBlockExceptSearched()
								&& blockResult.getBlock().getLocation().distance(p.getLocation()) > 2) {
							Location lastLoc = np.locations.get(XRAY, "mining-loc", null);
							if(lastLoc != null && blockIsJustAround(loc, lastLoc)) {
								Negativity.alertMod(ReportType.WARNING, p, this, 80, "mining-direction", "Found " + blockResult.getType()
											+ ", timeMining: " + timeMiningOre);
							}
							np.locations.set(XRAY, "mining-loc", loc);
						} else
							np.ints.set(XRAY, "mining-ore", timeMiningOre - 1);
					} else
						np.ints.set(XRAY, "mining-ore", timeMiningOre - 1);
				}
			}
			if(MINING_BLOCK.contains(b.getType()))
				np.longs.set(XRAY, "is-mining", time);
		}
	}
	
	private boolean blockIsJustAround(Location loc1, Location loc2) {
		return (loc1.getBlockX() - loc2.getBlockX() <= 1) && (loc1.getBlockY() - loc2.getBlockY() <= 1) && (loc1.getBlockZ() - loc2.getBlockZ() <= 1);
	}
}