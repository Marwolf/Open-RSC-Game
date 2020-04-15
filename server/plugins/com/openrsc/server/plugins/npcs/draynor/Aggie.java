package com.openrsc.server.plugins.npcs.draynor;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Aggie implements TalkNpcTrigger {

	private static final int SKIN_PASTE = 0;
	private static final int FROGS = 1;
	private static final int MADWITCH = 2;

	private static final int RED_DYE = 4;
	private static final int DONT_HAVE = 5;
	private static final int WITHOUT_DYE = 6;
	private static final int YELLOW_DYE = 7;
	private static final int BLUE_DYE = 8;
	private static final int DYES = 9;
	private static final int MAKEME = 10;
	private static final int HAPPY = 11;

	@Override
	public void onTalkNpc(Player player, final Npc npc) {
		aggieDialogue(player, npc, -1);
	}

	public void aggieDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			npcsay(p, n, "What can I help you with?");
			if (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
				int choice = multi(p, n,
					"Could you think of a way to make pink skin paste",
					"What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch you can't help me",
					"Can you make dyes for me please");
				if (choice == 0) {
					aggieDialogue(p, n, Aggie.SKIN_PASTE);
				} else if (choice == 1) {
					aggieDialogue(p, n, Aggie.MAKEME);
				} else if (choice == 2) {
					aggieDialogue(p, n, Aggie.FROGS);
				} else if (choice == 3) {
					aggieDialogue(p, n, Aggie.MADWITCH);
				} else if (choice == 4) {
					aggieDialogue(p, n, Aggie.DYES);
				}
			} else {
				int choiceOther = multi(p, n,
					"What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch you can't help me",
					"Can you make dyes for me please");
				if (choiceOther == 0) {
					aggieDialogue(p, n, Aggie.MAKEME);
				} else if (choiceOther == 1) {
					aggieDialogue(p, n, Aggie.FROGS);
				} else if (choiceOther == 2) {
					aggieDialogue(p, n, Aggie.MADWITCH);
				} else if (choiceOther == 3) {
					aggieDialogue(p, n, Aggie.DYES);
				}
			}

			return;
		}
		switch (cID) {
			case Aggie.DYES:
				npcsay(p, n,
					"What sort of dye would you like? Red, yellow or Blue?");
				int menu13 = multi(p, n,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
				if (menu13 == 0) {
					aggieDialogue(p, n, Aggie.RED_DYE);
				} else if (menu13 == 1) {
					aggieDialogue(p, n, Aggie.YELLOW_DYE);
				} else if (menu13 == 2) {
					aggieDialogue(p, n, Aggie.BLUE_DYE);
				} else if (menu13 == 3) {
					aggieDialogue(p, n, Aggie.HAPPY);
				}
				break;
			case Aggie.SKIN_PASTE:
				if (p.getCarriedItems().hasCatalogID(ItemId.ASHES.id(), Optional.of(false))
					&& (p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))
					|| p.getCarriedItems().hasCatalogID(ItemId.FLOUR.id(), Optional.of(false)))
					&& (p.getCarriedItems().hasCatalogID(ItemId.BUCKET_OF_WATER.id(), Optional.of(false))
					|| p.getCarriedItems().hasCatalogID(ItemId.JUG_OF_WATER.id(), Optional.of(false)))
					&& p.getCarriedItems().hasCatalogID(ItemId.REDBERRIES.id(), Optional.of(false))) {
					npcsay(p, n,
						"Yes I can, you have the ingredients for it already");
					npcsay(p, n, "Would you like me to mix you some?");
					int menu = multi(p, n, false, //do not send over
						"Yes please, mix me some skin paste",
						"No thankyou, I don't need paste");
					if (menu == 0) {
						say(p, n, "Yes please, mix me some skin paste");
						npcsay(p, n,
							"That should be simple, hand the things to Aggie then");
						Functions.mes(p,
							"You hand ash, flour, water and redberries to Aggie",
							"She tips it into a cauldron and mutters some words");
						p.getCarriedItems().remove(new Item(ItemId.ASHES.id()));
						if (p.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id())) == -1) {
							p.getCarriedItems().remove(new Item(ItemId.FLOUR.id()));
						}
						if (p.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id())) == -1) {
							p.getCarriedItems().remove(new Item(ItemId.JUG_OF_WATER.id()));
						}
						p.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id()));
						npcsay(p, n,
							"Tourniquet, Fenderbaum, Tottenham, MonsterMunch, MarbleArch");
						Functions.mes(p, "Aggie hands you the skin paste");
						give(p, ItemId.PASTE.id(), 1);
						npcsay(p, n, "There you go dearie, your skin potion",
							"That will make you look good at the Varrock dances");
					} else if (menu == 1) {
						say(p, n, "No thank you, I don't need skin paste");
						npcsay(p, n, "Okay dearie, thats always your choice");
					}
				} else {
					npcsay(p,
						n,
						"Why, its one of my most popular potions",
						"The women here, they like to have smooth looking skin",
						"(and I must admit, some of the men buy it too)",
						"I can make it for you, just get me whats needed");
					say(p, n, "What do you need to make it?");
					npcsay(p, n, "Well deary, you need a base for the paste",
						"That's a mix of ash, flour and water",
						"Then you need red berries to colour it as you want",
						"bring me those four items and I will make you some");
				}
				break;
			case Aggie.FROGS:
				npcsay(p,
					n,
					"Oh, not for years, but if you meet a talking chicken,",
					"You have probably met the professor in the Manor north of here",
					"A few years ago it was flying fish, that machine is a menace");
				break;
			case Aggie.MADWITCH:
				npcsay(p, n, "Oh, you like to call a witch names, do you?");
				if (ifheld(p, ItemId.COINS.id(), 20)) {
					Functions.mes(p,
						"Aggie waves her hands about, and you seem to be 20 coins poorer");
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					npcsay(p, n,
						"Thats a fine for insulting a witch, you should learn some respect");
				} else if (p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))) {
					Functions.mes(p, "Aggie waves her hands near you, and you seem to have lost some flour");
					p.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
					npcsay(p, n, "Thankyou for your kind present of flour",
						"I am sure you never meant to insult me");
				} else {
					npcsay(p, n,
						"You should be careful about insulting a Witch",
						"You never know what shape you could wake up in");
				}
				break;
			case Aggie.MAKEME:
				npcsay(p,
					n,
					"I mostly just make what I find pretty",
					"I sometimes make dye for the womens clothes, brighten the place up",
					"I can make red,yellow and blue dyes would u like some");
				int menu2 = multi(p, n,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
				if (menu2 == 0) {
					aggieDialogue(p, n, Aggie.RED_DYE);
				} else if (menu2 == 1) {
					aggieDialogue(p, n, Aggie.YELLOW_DYE);
				} else if (menu2 == 2) {
					aggieDialogue(p, n, Aggie.BLUE_DYE);
				} else if (menu2 == 3) {
					aggieDialogue(p, n, Aggie.HAPPY);
				}
				break;
			case Aggie.YELLOW_DYE:
				npcsay(p,
					n,
					"Yellow is a strange colour to get, comes from onion skins",
					"I need 2 onions, and 5 coins to make yellow");
				int menu4 = multi(p, n, false, //do not send over
					"Okay, make me some yellow dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu4 == 0) {
					if (!ifheld(p, ItemId.ONION.id(), 2)) {
						Functions.mes(p, "You don't have enough onions to make the yellow dye!");
					} else if (!ifheld(p, ItemId.COINS.id(), 5)) {
						Functions.mes(p, "You don't have enough coins to pay for the dye!");
					} else {
						say(p, n, "Okay, make me some yellow dye please");
						Functions.mes(p, "You hand the onions and payment to Aggie");
						p.getCarriedItems().remove(new Item(ItemId.ONION.id(), 2));
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						Functions.mes(p, "she takes a yellow bottle from nowhere and hands it to you");
						give(p, ItemId.YELLOWDYE.id(), 1);
					}
				} else if (menu4 == 1) {
					say(p, n, "I don't think I have all the ingredients yet");
					aggieDialogue(p, n, Aggie.DONT_HAVE);
				} else if (menu4 == 2) {
					say(p, n, "I can do without dye at that price");
					aggieDialogue(p, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.RED_DYE:
				npcsay(p, n, "3 lots of Red berries, and 5 coins, to you");
				int menu3 = multi(p, n, false, //do not send over
					"Okay, make me some red dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu3 == 0) {
					if (!ifheld(p, ItemId.REDBERRIES.id(), 3)) {
						Functions.mes(p, "You don't have enough berries to make the red dye!");
					} else if (!ifheld(p, ItemId.COINS.id(), 5)) {
						Functions.mes(p, "You don't have enough coins to pay for the dye!");
					} else {
						say(p, n, "Okay, make me some red dye please");
						Functions.mes(p, "You hand the berries and payment to Aggie");
						p.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id(), 3));
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						Functions.mes(p, "she takes a red bottle from nowhere and hands it to you");
						give(p, ItemId.REDDYE.id(), 1);
					}
				} else if (menu3 == 1) {
					say(p, n, "I don't think I have all the ingredients yet");
					aggieDialogue(p, n, Aggie.DONT_HAVE);
				} else if (menu3 == 2) {
					say(p, n, "I can do without dye at that price");
					aggieDialogue(p, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.BLUE_DYE:
				npcsay(p, n, "2 woad leaves, and 5 coins, to you");
				int menu6 = multi(p, n, false, //do not send over
					"Okay, make me some blue dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu6 == 0) {
					if (!ifheld(p, ItemId.WOAD_LEAF.id(), 2)) {
						Functions.mes(p, "You don't have enough woad leaves to make the blue dye!");
					} else if (!ifheld(p, ItemId.COINS.id(), 5)) {
						Functions.mes(p, "You don't have enough coins to pay for the dye!");
					} else {
						say(p, n, "Okay, make me some blue dye please");
						Functions.mes(p, "You hand the woad leaves and payment to Aggie");
						p.getCarriedItems().remove(new Item(ItemId.WOAD_LEAF.id(), 2));
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						Functions.mes(p,
							"she takes a blue bottle from nowhere and hands it to you");
						give(p, ItemId.BLUEDYE.id(), 1);
					}
				} else if (menu6 == 1) {
					say(p, n, "I don't think I have all the ingredients yet");
					say(p, n, "Where on earth am I meant to find woad leaves?");
					npcsay(p, n, "I'm not entirely sure",
						"I used to go and nab the stuff from the public gardens in Falador",
						"It hasn't been growing there recently though");
				} else if (menu6 == 2) {
					say(p, n, "I can do without dye at that price");
					aggieDialogue(p, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.DONT_HAVE:
				npcsay(p,
					n,
					"You know what you need to get now, come back when you have them",
					"goodbye for now");
				break;
			case Aggie.WITHOUT_DYE:
				npcsay(p,
					n,
					"Thats your choice, but I would think you have killed for less",
					"I can see it in your eyes");
				break;
			case Aggie.HAPPY:
				npcsay(p, n, "You are easily pleased with yourself then",
					"when you need dyes, come to me");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.AGGIE.id();
	}
}
