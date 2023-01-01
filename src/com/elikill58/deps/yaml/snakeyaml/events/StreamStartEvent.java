package com.elikill58.deps.yaml.snakeyaml.events;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class StreamStartEvent extends Event {
	public StreamStartEvent(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public boolean is(final ID id) {
		return ID.StreamStart == id;
	}
}
