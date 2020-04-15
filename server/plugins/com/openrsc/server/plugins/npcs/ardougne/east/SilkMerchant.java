package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.time.Instant;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SilkMerchant implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCache().hasKey("silkStolen") && (Instant.now().getEpochSecond() < p.getCache().getLong("silkStolen") + 1200)) {
			npcsay(p, n, "Do you really think I'm going to buy something",
				"That you have just stolen from me",
				"guards guards");

			Npc attacker = ifnearvisnpc(p, NpcId.GUARD_ARDOUGNE.id(), 5); // Guard

			if (attacker != null)
				attacker.setChasing(p);

		} else if (p.getCarriedItems().hasCatalogID(ItemId.SILK.id(), Optional.of(false))) {
			say(p, n, "Hello I have some fine silk from Al Kharid to sell to you");
			npcsay(p, n, "Ah I may be intersted in that",
				"What sort of price were you looking at per piece of silk?");
			int menu = multi(p, n, "20 coins", "80 coins", "120 coins", "200 coins");
			if (menu == 0) {
				npcsay(p, n, "Ok that suits me");
				p.getCarriedItems().remove(new Item(ItemId.SILK.id()));
				give(p, ItemId.COINS.id(), 20);
			} else if (menu == 1) {
				npcsay(p, n, "80 coins that's a bit steep", "How about 40 coins");
				int reply2 = multi(p, n, "Ok 40 sounds good", "50 and that's my final price", "No that is not enough");
				if (reply2 == 0) {
					p.getCarriedItems().remove(new Item(ItemId.SILK.id()));
					give(p, ItemId.COINS.id(), 40);
				} else if (reply2 == 1) {
					npcsay(p, n, "Done");
					p.getCarriedItems().remove(new Item(ItemId.SILK.id()));
					give(p, ItemId.COINS.id(), 50);
				}
			} else if (menu == 2) {
				npcsay(p, n, "You'll never get that much for it",
					"I'll be generous and give you 50 for it");
				int reply = multi(p, n, false, "Ok I guess 50 will do", "I'll give it to you for 60", "No that is not enough");
				if (reply == 0) {
					say(p, n, "Ok I guess 50 will do");
					p.getCarriedItems().remove(new Item(ItemId.SILK.id()));
					give(p, ItemId.COINS.id(), 50);
				} else if (reply == 1) {
					say(p, n, "I'll give it you for 60");
					npcsay(p, n, "You drive a hard bargain", "but I guess that will have to do");
					p.getCarriedItems().remove(new Item(ItemId.SILK.id()));
					give(p, ItemId.COINS.id(), 60);
				} else if (reply == 2) {
					say(p, n, "No that is not enough");
				}
			} else if (menu == 3) {
				npcsay(p, n, "Don't be ridiculous that is far to much",
					"You insult me with that price");
			}
		} else {
			npcsay(p, n, "I buy silk",
				"If you get any silk to sell bring it here");
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.SILK_MERCHANT.id();
	}
}
