package com.elikill58.negativity.protocols;

import org.bukkit.Bukkit;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerInteractEvent;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.common.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class AutoClick extends Cheat implements Listeners {

	public static final DataType<Integer> CLICKS = new DataType<Integer>("clicks", "Clicks", () -> new IntegerDataCounter());

	public static final int CLICK_ALERT = Adapter.getAdapter().getConfig().getInt("cheats.autoclick.click_alert");
	
	public AutoClick() {
		super(CheatKeys.AUTO_CLICK, true, Materials.FISHING_ROD, CheatCategory.COMBAT, true, "auto-click", "autoclic");
		Bukkit.getScheduler().runTaskTimer(SpigotNegativity.getInstance(), () -> {
			for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
				NegativityAccount account = np.getAccount();
				if (account.getMostClicksPerSecond() < np.ACTUAL_CLICK) {
					account.setMostClicksPerSecond(np.ACTUAL_CLICK);
				}
				recordData(p.getUniqueId(), CLICKS, np.ACTUAL_CLICK);
				np.LAST_CLICK = np.ACTUAL_CLICK;
				np.ACTUAL_CLICK = 0;
				if (np.SEC_ACTIVE < 2) {
					np.SEC_ACTIVE++;
					return;
				}
			}
		}, 20, 20);
	}
	
	@EventListener
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().name().contains("AIR")) {
			manageClick(e.getPlayer(), e);
		}
	}
	
	/*@EventHandler
	public void onPacket(PacketReceiveEvent e) {
		if(e.getPacket().getPacketType() == PacketType.Client.ARM_ANIMATION)
			manageClick(e.getPlayer(), e);
	}*/
		
	private void manageClick(Player p, PlayerInteractEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		ItemStack inHand = p.getItemInHand();
		if (inHand != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(inHand.getType().getId())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(inHand.getType().getId());
				if (ib.getWhen().isClick() && ib.isForThisCheat(this))
					return;
			}
		np.ACTUAL_CLICK++;
		int ping = p.getPing(), click = np.ACTUAL_CLICK - (ping / 9);
		if (click > CLICK_ALERT && np.hasDetectionActive(this)) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(np.ACTUAL_CLICK * 2.5),
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + np.getAccount().getMostClicksPerSecond() + " Ping: " + ping,
							hoverMsg("main", "%click%", np.ACTUAL_CLICK));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		int currentClick = np.ACTUAL_CLICK;
		DataCounter<Integer> counter = data.getData(CLICKS);
		counter.add(currentClick);
		if(counter.getMax() == 0)
			return null;
		return Utils.coloredMessage("&aCurrent&7/&cMaximum&7/&6Average&7: &a" + currentClick + "&7/&c" + counter.getMax() + "&7/&6" + counter.getAverage() + " &7clicks");
	}
}
