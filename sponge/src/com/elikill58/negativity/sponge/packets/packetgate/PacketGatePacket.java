package com.elikill58.negativity.sponge.packets.packetgate;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;

public class PacketGatePacket extends AbstractPacket {
	
	private PacketEvent event;
	
	public PacketGatePacket(NPacket nPacket, Object nmsPacket, Player p, PacketEvent event) {
		super(nPacket.getPacketType(), nmsPacket, nPacket, SpongeEntityManager.getPlayer(p));
		//super(type, packet, null, SpongeEntityManager.getPlayer(p));
		this.event = event;
	}
	
	public PacketEvent getPacketGateEvent() {
		return event;
	}

}