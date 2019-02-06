package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.external.ItemId;

public class BailingBucket implements InvActionExecutiveListener, InvActionListener {

	@Override
	public void onInvAction(Item item, Player player) {
		if (player.isBusy())
			return;
		FishingTrawler trawler = World.getWorld().getFishingTrawler(player);
		if (trawler != null && (trawler.getShipAreaWater().inBounds(player.getLocation())
			|| trawler.getShipArea().inBounds(player.getLocation()))) {
			player.setBusyTimer(650);
			// 1st stage boat
			if(player.getY() >= 741 && player.getY() <= 743) {
				player.message("you bail a little water...");
			}
			else {
				player.message("you begin to bail a bucket load of water");
			}
			sleep(650);
			trawler.bailWater();
		} else {
			// player.message("");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == ItemId.BAILING_BUCKET.id();
	}

}
