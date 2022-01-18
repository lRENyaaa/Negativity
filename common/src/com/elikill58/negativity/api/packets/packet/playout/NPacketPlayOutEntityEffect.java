package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.potion.PotionEffectType;

public class NPacketPlayOutEntityEffect implements NPacketPlayOut {

	public int entityId;
	public PotionEffectType type;
	public byte amplifier;
	public int duration;
	/**
	 * Warn: the meaning of this variable change over the time.<br>
	 * We not support it.
	 */
	public byte e; // don't support this variable

	public NPacketPlayOutEntityEffect() {}
	
	public NPacketPlayOutEntityEffect(int entityId, byte typeByte, byte amplifier, int duration, byte e) {
		this.entityId = entityId;
		this.type = PotionEffectType.fromByte(typeByte);
		this.amplifier = amplifier;
		this.duration = duration;
		this.e = e;
	}
	
	public boolean isMaxDuration() {
		return duration >= 32767;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_EFFECT;
	}
}