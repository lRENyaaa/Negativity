package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import org.json.parser.JSONParser;
import org.json.parser.ParseException;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.common.events.EventType;
import com.elikill58.negativity.common.inventory.Inventory;
import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.common.item.ItemBuilder;
import com.elikill58.negativity.common.item.ItemRegistrar;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.events.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.impl.events.PlayerCheatEvent;
import com.elikill58.negativity.spigot.impl.events.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.impl.events.ShowAlertPermissionEvent;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemBuilder;
import com.elikill58.negativity.spigot.impl.item.SpigotItemRegistrar;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.config.BukkitConfigAdapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.logger.JavaLoggerAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotAdapter extends Adapter {

	private JavaPlugin pl;
	private final ConfigAdapter config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpigotNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private final SpigotItemRegistrar itemRegistrar;

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = new BukkitConfigAdapter.PluginConfig(pl);
		this.translationProviderFactory = new NegativityTranslationProviderFactory(
				pl.getDataFolder().toPath().resolve("lang"), "Negativity", "CheatHover");
		this.logger = new JavaLoggerAdapter(pl.getLogger());
		this.itemRegistrar = new SpigotItemRegistrar();
	}

	@Override
	public String getName() {
		return "spigot";
	}

	@Override
	public ConfigAdapter getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void debug(String msg) {
		if (UniversalUtils.DEBUG)
			pl.getLogger().info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) {
		return pl.getResource("assets/negativity/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		UniversalUtils.init();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));
		// SpigotNegativity.trySendProxyPing();
		// SpigotNegativity.setupValue();
		// for(Player p : Utils.getOnlinePlayers())
		// SpigotNegativity.manageAutoVerif(p);
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		try {
			getConfig().load();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to reload configuration", e);
		}
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, CheatHover hover) {
		// SpigotNegativity.alertMod(type, (Player) p, c, reliability, proof, hover);
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Object data = new JSONParser().parse(response);
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					Object isMcleaks = json.get("isMcleaks");
					if (isMcleaks != null) {
						return Boolean.getBoolean(isMcleaks.toString());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(NegativityPlayer.getCached(temp.getUniqueId()).getPlayer());
		return list;
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}

	@Override
	public double[] getTPS() {
		try {
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			return (double[]) server.getClass().getField("recentTps").get(server);
		} catch (Exception e) {
			SpigotNegativity.getInstance().getLogger().warning("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
			e.printStackTrace();
			return new double[] { 20, 20, 20 };
		}
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return new SpigotLocation(w, x, y, z);
	}

	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpigotInventory(inventoryName, size, holder);
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		new ClickableText().addRunnableHoverEvent(message, hover, command).sendToPlayer((org.bukkit.entity.Player) p.getDefaultPlayer());
	}

	@Override
	public Event callEvent(EventType type, Object... args) {
		switch (type) {
		case CHEAT:
			return new PlayerCheatEvent((Player) args[0], (Cheat) args[1], (int) args[2]);
		case CHEAT_ALERT:
			return new PlayerCheatAlertEvent((ReportType) args[0], (Player) args[1], (Cheat) args[2], (int) args[3],
					(boolean) args[4], (int) args[5], (String) args[6], (CheatHover) args[7], (int) args[8]);
		case PACKET_CLEAR:
			return new PlayerPacketsClearEvent((Player) args[0], (NegativityPlayer) args[1]);
		case SHOW_PERM:
			return new ShowAlertPermissionEvent((Player) args[0], (NegativityPlayer) args[1], (boolean) args[2]);
		case OWN:
			break;
		}
		return null;
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new SpigotItemBuilder(type);
	}
}
