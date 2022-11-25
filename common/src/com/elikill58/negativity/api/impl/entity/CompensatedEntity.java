package com.elikill58.negativity.api.impl.entity;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class CompensatedEntity extends AbstractEntity {

	private final int entityId;
	private final EntityType type;
	private final World world;
	private Location location;
	
	public CompensatedEntity(int entityId, EntityType type, World world) {
		this.entityId = entityId;
		this.type = type;
		this.world = world;
	}

	@Override
	public boolean isOnGround() {
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public double getEyeHeight() {
		return 0;
	}

	@Override
	public Location getEyeLocation() {
		return null;
	}

	@Override
	public Vector getRotation() {
		return null;
	}

	@Override
	public EntityType getType() {
		return type;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public String getEntityId() {
		return String.valueOf(entityId);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}

	@Override
	public void sendMessage(String msg) {}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getDefault() {
		return this;
	}
	
}
