package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.message;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.impl.CustomProjectileEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestUngadulu;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.transform;

public class LegendsQuestHolyWater implements InvActionListener, InvActionExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener {

	public boolean compareItemsIds(Item item1, Item item2, int idA, int idB) {
		return item1.getID() == idA && item2.getID() == idB || item1.getID() == idB && item2.getID() == idA;
	}
	
	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.ENCHANTED_VIAL.id());
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		// simple random for the moment
		message(player, 0, "You pour some of the sacred water into the enchanted vial.",
				"You now have a vial of holy water.");
		player.getInventory().replace(ItemId.ENCHANTED_VIAL.id(), ItemId.HOLY_WATER_VIAL.id());
		if(!player.getCache().hasKey("remaining_blessed_bowl")) {
			player.getCache().set("remaining_blessed_bowl", DataConversions.random(1, 15));
		} else {
			int remain = player.getCache().getInt("remaining_blessed_bowl");	
			if(remain > 1) {
				player.getCache().put("remaining_blessed_bowl", remain - 1);
			} 
			// empty the bowl
			else {
				player.message("The pure water in the golden bowl has run out...");
				player.getInventory().replace(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.BLESSED_GOLDEN_BOWL.id());
				player.getCache().remove("remaining_blessed_bowl");
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == ItemId.HOLY_WATER_VIAL.id();
	}

	@Override
	public void onInvAction(Item item, Player player) {
		if (!player.getInventory().wielding(ItemId.HOLY_WATER_VIAL.id())) {
			player.message("You need to equip this item to throw it.");
		}
		else {
			Npc ungadulu = getNearestNpc(player, LegendsQuestUngadulu.UNGADULU, 4);
			if (ungadulu == null || player.getQuestStage(Constants.Quests.LEGENDS_QUEST) > 3) {
				player.message("You see no one suitable to throw it at.");
			}
			else {
				player.message("You throw the holy watervial at Ungadulu.");
				removeItem(player, item.getID(), 1);
				player.playSound("projectile");
				Server.getServer().getGameEventHandler().add(new CustomProjectileEvent(player, ungadulu, 1) {
					@Override
					public void doSpell() {
					}
				});
				ungadulu = transform(ungadulu, LegendsQuestUngadulu.EVIL_UNGADULU, true);
				npcTalk(player, ungadulu, "Vile serpent...you will pay for that...");
				ungadulu = transform(ungadulu, LegendsQuestUngadulu.UNGADULU, true);
				npcTalk(player, ungadulu, "What...what happened...why am I all wet?");
				
				// 5 min of holy water effect tops
				if (player.getCache().hasKey("holy_water_neiz")) {
					Server.getServer().getEventHandler().add(new SingleEvent(player, 300000) {
						public void action() {
							player.getCache().remove("holy_water_neiz");
						}
					});
				}
			}
		}
	}

}
