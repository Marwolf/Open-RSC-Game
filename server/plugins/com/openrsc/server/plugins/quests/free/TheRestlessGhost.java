package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TheRestlessGhost implements QuestInterface, TakeObjTrigger,
	TalkNpcTrigger, OpLocTrigger,
	UseLocTrigger {

	private static final int GHOST_COFFIN_OPEN = 40;
	private static final int GHOST_COFFIN_CLOSED = 39;

	@Override
	public int getQuestId() {
		return Quests.THE_RESTLESS_GHOST;
	}

	@Override
	public String getQuestName() {
		return "The restless ghost";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed the restless ghost quest");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.THE_RESTLESS_GHOST), true);
		player.message("@gre@You haved gained 1 quest point!");

	}

	private void ghostDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
			if (p.getQuestStage(this) == -1) {
				p.message("The ghost doesn't appear interested in talking");
				return;
			}
			if (cID == -1) {
				if (p.getQuestStage(this) == 3) {
					say(p, n, "Hello ghost, how are you?");
					npcsay(p, n, "How are you doing finding my skull?");
					if (!p.getCarriedItems().hasCatalogID(ItemId.QUEST_SKULL.id(), Optional.of(false))) {
						say(p, n, "Sorry, I can't find it at the moment");
						npcsay(p,
							n,
							"Ah well keep on looking",
							"I'm pretty sure it's somewhere in the tower south west from here",
							"There's a lot of levels to the tower, though",
							"I suppose it might take a little while to find");
						// kosher: this condition made player need to restart skull process incl. skeleton fight
						p.getCache().remove("tried_grab_skull");
					} else {
						say(p, n, "I have found it");
						npcsay(p,
							n,
							"Hurrah now I can stop being a ghost",
							"You just need to put it in my coffin over there",
							"And I will be free");
					}
					return;
				}
				if (p.getQuestStage(this) == 0
					|| !p.getCarriedItems().getEquipment().hasEquipped(ItemId.AMULET_OF_GHOSTSPEAK.id())) {
					say(p, n, "Hello ghost, how are you?");
					npcsay(p, n, "Wooo wooo wooooo");
					int choice = multi(p, n,
						"Sorry I don't speak ghost",
						"Ooh that's interesting",
						"Any hints where I can find some treasure?");
					if (choice == 0) {
						ghostDialogue(p, n, Ghost.DONTSPEAK);
					} else if (choice == 1) {
						npcsay(p, n, "Woo wooo", "Woooooooooooooooooo");
						int choice2 = multi(p, n,
							"Did he really?", "Yeah that's what I thought");
						if (choice2 == 0) {
							npcsay(p, n, "Woo");
							int choice3 = multi(p, n,
								"My brother had exactly the same problem",
								"Goodbye. Thanks for the chat");
							if (choice3 == 0) {
								npcsay(p, n, "Woo Wooooo",
									"Wooooo Woo woo woo");
								int choice4 = multi(
									p,
									n,
									"Goodbye. Thanks for the chat",
									"You'll have to give me the recipe some time");
								if (choice4 == 0) {
									ghostDialogue(p, n, Ghost.GOODBYE);
								} else if (choice4 == 1) {
									npcsay(p, n, "Wooooooo woo woooooooo");
									int choice6 = multi(p, n,
										"Goodbye. Thanks for the chat",
										"Hmm I'm not sure about that");
									if (choice6 == 0) {
										ghostDialogue(p, n, Ghost.GOODBYE);
									} else if (choice6 == 1) {
										ghostDialogue(p, n, Ghost.NOTSURE);
									}
								}
							} else if (choice3 == 1) {
								npcsay(p, n, "Wooo wooo",
									"Wooo woooooooooooooooo");
								int choice7 = multi(p, n,
									"Goodbye. Thanks for the chat",
									"Hmm I'm not sure about that");
								if (choice7 == 0) {
									ghostDialogue(p, n, Ghost.GOODBYE);
								} else if (choice7 == 1) {
									ghostDialogue(p, n, Ghost.NOTSURE);
								}
							}
						} else if (choice2 == 1) {
							npcsay(p, n, "Wooo woooooooooooooo");
							int choice5 = multi(p, n,
								"Goodbye. Thanks for the chat",
								"Hmm I'm not sure about that");
							if (choice5 == 0) {
								ghostDialogue(p, n, Ghost.GOODBYE);
							} else if (choice5 == 1) {
								ghostDialogue(p, n, Ghost.NOTSURE);
							}
						}
					} else if (choice == 2) {
						npcsay(p, n, "Wooooooo woo!");
						int choice8 = multi(p, n, false, //do not send over
							"Sorry I don't speak ghost",
							"Thank you. You've been very helpful");
						if (choice8 == 0) {
							say(p, n, "Sorry I don't speak ghost");
							ghostDialogue(p, n, Ghost.DONTSPEAK);
						} else if (choice8 == 1) {
							say(p, n, "Thank you. You've been very helpfull");
							npcsay(p, n, "Wooooooo");
						}
					}
				} else {
					say(p, n, "Hello ghost, how are you?");
					npcsay(p, n, "Not very good actually");
					say(p, n, "What's the problem then?");
					npcsay(p, n, "Did you just understand what I said?");
					int choice = multi(p, n, false, //do not send over
						"Yep, now tell me what the problem is",
						"No, you sound like you're speaking nonsense to me",
						"Wow, this amulet works");
					if (choice == 0) {
						say(p, n, "Yep, now tell me what the problem is");
						npcsay(p, n,
							"Wow this is incredible, I didn't expect any one to understand me again");
						say(p, n, "Yes, yes I can understand you",
							"But have you any idea why you're doomed to be a ghost?");
						npcsay(p, n, "I'm not sure");
						say(
							p,
							n,
							"I've been told a certain task may need to be completed",
							"So you can rest in peace");
						npcsay(p, n, "I should think it is probably because ",
							"A warlock has come along and stolen my skull",
							"If you look inside my coffin there",
							"you'll find my corpse without a head on it");
						say(p, n,
							"Do you know where this warlock might be now?");
						npcsay(p,
							n,
							"I think it was one of the warlocks who lives in the big tower",
							"In the sea southwest from here");
						say(p, n,
							"Ok I will try and get the skull back for you, so you can rest in peace.");
						npcsay(p,
							n,
							"Ooh thank you. That would be such a great relief",
							"It is so dull being a ghost");
						p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
					} else if (choice == 1) {
						say(p, n, "No");
						npcsay(p, n,
							"Oh that's a pity. You got my hopes up there");
						say(p, n, "Yeah, it is pity. Sorry");
						npcsay(p, n, "Hang on a second. You can understand me");
						int choice2 = multi(p, n, "No I can't", "Yep clever aren't I");
						if (choice2 == 0) {
							npcsay(p, n,
								"I don't know, the first person I can speak to in ages is a moron");
						} else if (choice2 == 1) {
							npcsay(p, n, "I'm impressed",
								"You must be very powerfull",
								"I don't suppose you can stop me being a ghost?");
							int choice3 = multi(p, n,
								"Yes, Ok. Do you know why you're a ghost?",
								"No, you're scary");
							if (choice3 == 0) {
								ghostDialogue(p, n, Ghost.WHY);
							} else if (choice3 == 1) {
								ghostDialogue(p, n, Ghost.SCARY);
							}
						}
					} else if (choice == 2) {
						say(p, n, "Wow, this amulet works");
						npcsay(p,
							n,
							"Oh its your amulet that's doing it. I did wonder",
							"I don't suppose you can help me? I don't like being a ghost");
						int choice3 = multi(p, n,
							"Yes, Ok. Do you know why you're a ghost?",
							"No, you're scary");
						if (choice3 == 0) {
							ghostDialogue(p, n, Ghost.WHY);
						} else if (choice3 == 1) {
							ghostDialogue(p, n, Ghost.SCARY);
						}
					}
				}
				return;
			}
			switch (cID) {
				case Ghost.DONTSPEAK:
					npcsay(p, n, "Woo woo?");
					say(p, n, "Nope still don't understand you");
					npcsay(p, n, "Woooooooo");
					say(p, n, "Never mind");
					break;
				case Ghost.GOODBYE:
					npcsay(p, n, "Wooo wooo");
					break;
				case Ghost.NOTSURE:
					npcsay(p, n, "Wooo woo");
					say(p, n, "Well if you insist");
					npcsay(p, n, "Wooooooooo");
					say(p, n, "Ah well, better be off now");
					npcsay(p, n, "Woo");
					say(p, n, "Bye");
					break;
				case Ghost.WHY:
					npcsay(p, n,
						"No, I just know I can't do anything much like this");
					say(
						p,
						n,
						"I've been told a certain task may need to be completed",
						"So you can rest in peace");
					npcsay(p, n, "I should think it is probably because ",
						"a warlock has come along and stolen my skull",
						"If you look inside my coffin there",
						"you'll find my corpse without a head on it");
					say(p, n, "Do you know where this warlock might be now?");
					npcsay(p,
						n,
						"I think it was one of the warlocks who lives in the big tower",
						"In the sea southwest from here");
					say(p, n,
						"Ok I will try and get the skull back for you, so you can rest in peace.");
					npcsay(p, n,
						"Ooh thank you. That would be such a great relief",
						"It is so dull being a ghost");
					p.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
					break;
				case Ghost.SCARY:
					break;
			}
		}
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
			ghostDialogue(p, n, -1);
		}
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if (obj.getID() == GHOST_COFFIN_OPEN || obj.getID() == GHOST_COFFIN_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, GHOST_COFFIN_OPEN, "You open the coffin");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, GHOST_COFFIN_CLOSED, "You close the coffin");
			} else {
				if (player.getQuestStage(this) > 0) {
					player.message("There's a skeleton without a skull in here");
				} else if (player.getQuestStage(this) == -1) {
					player.message("Theres a nice and complete skeleton in here!");
				} else {
					player.message("You search the coffin and find some human remains");
				}
			}
		}
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player player) {
		if (obj.getID() == GHOST_COFFIN_OPEN && player.getQuestStage(this) == 3
			&& item.getCatalogId() == ItemId.QUEST_SKULL.id()) {
			addnpc(player.getWorld(), NpcId.GHOST_RESTLESS.id(), 102, 675, 30);
			Functions.mes(player, "You put the skull in the coffin");
			player.getCarriedItems().remove(new Item(ItemId.QUEST_SKULL.id()));
			//on completion cache key no longer needed
			player.getCache().remove("tried_grab_skull");
			Npc npc = ifnearvisnpc(player, NpcId.GHOST_RESTLESS.id(), 8);
			if (npc != null) {
				npc.remove();
			}
			Functions.mes(player, "The ghost has vanished",
				"You think you hear a faint voice in the air", "Thank you");
			player.sendQuestComplete(Quests.THE_RESTLESS_GHOST);
			return;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.GHOST_RESTLESS.id();
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item,
							   Player player) {
		return item.getCatalogId() == ItemId.QUEST_SKULL.id() && obj.getID() == GHOST_COFFIN_OPEN;
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return obj.getID() == GHOST_COFFIN_OPEN || obj.getID() == GHOST_COFFIN_CLOSED;
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.QUEST_SKULL.id();
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		Npc skeleton = ifnearvisnpc(p, NpcId.SKELETON_RESTLESS.id(), 10);
		if (i.getID() == ItemId.QUEST_SKULL.id()) {
			// spawn-place
			if (i.getX() == 218 && i.getY() == 3521) {
				if (p.getQuestStage(Quests.THE_RESTLESS_GHOST) != 3) {
					say(p, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
					return;
				} else if (!p.getCache().hasKey("tried_grab_skull")) {
					p.getCache().store("tried_grab_skull", true);
					p.getWorld().unregisterItem(i);
					give(p, ItemId.QUEST_SKULL.id(), 1);
					if (skeleton == null) {
						//spawn skeleton and give message
						p.message("Out of nowhere a skeleton appears");
						skeleton = addnpc(p.getWorld(), NpcId.SKELETON_RESTLESS.id(), 217, 3520, 100);
						skeleton.setChasing(p);
					} else {
						skeleton.setChasing(p);
					}

				}
				// allow if player had at least one time tried grab skull
				else {
					p.getWorld().unregisterItem(i);
					give(p, ItemId.QUEST_SKULL.id(), 1);
				}

			}
			// allow wild if post-quest
			else if (p.getQuestStage(Quests.THE_RESTLESS_GHOST) == -1 && i.getY() <= 425) {
				p.getWorld().unregisterItem(i);
				give(p, ItemId.QUEST_SKULL.id(), 1);
			} else {
				say(p, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
				return;
			}
		}
	}

	class Ghost {
		public static final int DONTSPEAK = 0;
		public static final int GOODBYE = 1;
		public static final int NOTSURE = 2;
		public static final int WHY = 3;
		public static final int SCARY = 4;
	}
}
