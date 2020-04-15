package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.mes;

public class UndergroundPassSmearDollOfIban implements UseInvTrigger {

	/**
	 * A underground pass class for preparing the doll of iban.
	 * Smearing (using items on the doll of iban) to finally complete it.
	 **/

	@Override
	public boolean blockUseInv(Player p, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id());
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			p.message("you rub the ashes into the doll");
			p.getCarriedItems().remove(new Item(ItemId.IBANS_ASHES.id()));
			if (!p.getCache().hasKey("ash_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("ash_on_doll", true);
			}
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			mes(p, "you crumble the doves skeleton into dust");
			p.message("and rub it into the doll");
			p.getCarriedItems().remove(new Item(ItemId.IBANS_CONSCIENCE.id()));
			if (!p.getCache().hasKey("cons_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("cons_on_doll", true);
			}
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			mes(p, "you pour the strange liquid over the doll");
			p.message("it seeps into the cotton");
			p.getCarriedItems().remove(new Item(ItemId.IBANS_SHADOW.id()));
			if (!p.getCache().hasKey("shadow_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("shadow_on_doll", true);
			}
		}
	}
}
