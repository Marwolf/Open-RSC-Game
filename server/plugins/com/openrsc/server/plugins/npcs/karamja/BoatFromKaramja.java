package com.openrsc.server.plugins.npcs.karamja;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.IndirectTalkToNpcTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class BoatFromKaramja implements
	TalkNpcTrigger, IndirectTalkToNpcTrigger,
	OpLocTrigger {

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		int option = multi(p, n, "Can I board this ship?",
			"Does Karamja have any unusual customs then?");
		if (option == 0) {
			onIndirectTalkToNpc(p, n);
		} else if (option == 1) {
			npcsay(p, n, "I'm not that sort of customs officer");
		}
	}

	@Override
	public void onIndirectTalkToNpc(Player p, final Npc n) {
		npcsay(p, n, "You need to be searched before you can board");
		int sub_opt = multi(p, n, "Why?",
			"Search away I have nothing to hide",
			"You're not putting your hands on my things");
		if (sub_opt == 0) {
			npcsay(p, n,
				"Because Asgarnia has banned the import of intoxicating spirits");
		} else if (sub_opt == 1) {
			if (p.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id(), Optional.of(false))) {
				npcsay(p, n, "Aha trying to smuggle rum are we?");
				Functions.mes(p, "The customs officer confiscates your rum");
				p.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
			} else {
				npcsay(p,
					n,
					"Well you've got some odd stuff, but it's all legal",
					"Now you need to pay a boarding charge of 30 gold");
				int pay_opt = multi(p, n, false, "Ok", "Oh, I'll not bother then");
				if (pay_opt == 0) {
					if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30)) != -1) {
						say(p, n, "Ok");
						Functions.mes(p, "You pay 30 gold", "You board the ship");
						teleport(p, 269, 648);
						p.message("The ship arrives at Port Sarim");
					} else { // not enough money
						say(p, n,
							"Oh dear I don't seem to have enough money");
					}
				} else if (pay_opt == 1) {
					say(p, n, "Oh, I'll not bother then");
				}
			}
		} else if (sub_opt == 2) {
			npcsay(p, n, "You're not getting on this ship then");
		}
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 161 || (obj.getID() == 162) || (obj.getID() == 163)) {
			if (command.equals("board")) {
				if (p.getY() != 713) {
					return;
				}
				Npc officer = ifnearvisnpc(p, NpcId.CUSTOMS_OFFICER.id(), 4);
				if (officer != null) {
					officer.initializeIndirectTalkScript(p);
				} else {
					p.message("I need to speak to the customs officer before boarding the ship.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICER.id();
	}

	@Override
	public boolean blockIndirectTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICER.id();
	}

	@Override
	public boolean blockOpLoc(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 161 && arg0.getLocation().equals(Point.location(326, 710)))
			|| (arg0.getID() == 163 && arg0.getLocation().equals(Point.location(319, 710)))
			|| (arg0.getID() == 162 && arg0.getLocation().equals(Point.location(324, 710)));
	}
}
