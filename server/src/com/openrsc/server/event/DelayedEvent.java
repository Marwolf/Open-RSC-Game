package com.openrsc.server.event;

import com.openrsc.server.Server;
import com.openrsc.server.ServerEventHandler;
import com.openrsc.server.model.entity.player.Player;


public abstract class DelayedEvent {

	protected final ServerEventHandler handler = Server.getServer().getEventHandler();
	protected int delay = 600;
	protected Player owner;
	protected boolean matchRunning = true;
	protected boolean gameEvent;
	private long lastRun = System.currentTimeMillis();

	public DelayedEvent(Player owner, int delay) {
		this.owner = owner;
		this.delay = delay;
	}

	public boolean belongsTo(Player player) {
		return owner != null && owner.equals(player);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public Object getIdentifier() {
		return null;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean is(DelayedEvent e) {
		return (e.getIdentifier() != null && e.getIdentifier().equals(getIdentifier()));
	}

	public abstract void run();

	public void setLastRun(long time) {
		lastRun = time;
	}

	public final boolean shouldRemove() {
		return !matchRunning;
	}

	public final boolean shouldRun() {
		return matchRunning && System.currentTimeMillis() - lastRun >= delay;
	}

	public final void stop() {
		matchRunning = false;
	}

	public int timeTillNextRun() {
		int time = (int) (delay - (System.currentTimeMillis() - lastRun));
		return time < 0 ? 0 : time;
	}

	public final void updateLastRun() {
		lastRun = System.currentTimeMillis();
	}

	public boolean isGameEvent() {
		return gameEvent;
	}
}
