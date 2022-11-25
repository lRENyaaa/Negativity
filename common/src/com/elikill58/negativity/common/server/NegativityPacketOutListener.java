package com.elikill58.negativity.common.server;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.impl.entity.CompensatedEntity;
import com.elikill58.negativity.api.impl.entity.CompensatedPlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnPlayer;
import com.elikill58.negativity.universal.Adapter;

public class NegativityPacketOutListener implements Listeners {

	@EventListener
	public void onPacketSend(PacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		PacketType type = e.getPacket().getPacketType();
		if(type.equals(PacketType.Server.SPAWN_ENTITY)) {
			NPacketPlayOutSpawnEntity spawn = (NPacketPlayOutSpawnEntity) e.getPacket();
			CompensatedEntity et = new CompensatedEntity(spawn.entityId, spawn.type, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z));
			p.getWorld().addEntity(et);
		} else if(type.equals(PacketType.Server.SPAWN_PLAYER)) {
			NPacketPlayOutSpawnPlayer spawn = (NPacketPlayOutSpawnPlayer) e.getPacket();
			Player cible = Adapter.getAdapter().getPlayer(spawn.uuid);
			if(cible == null) {
				Adapter.getAdapter().debug("Can't find player for UUID " + spawn.uuid);
				CompensatedPlayer et = new CompensatedPlayer(spawn.entityId, spawn.uuid, p.getWorld());
				et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z));
				p.getWorld().addEntity(et);
			} else
				p.getWorld().addEntity(cible);
		}
	}
}
