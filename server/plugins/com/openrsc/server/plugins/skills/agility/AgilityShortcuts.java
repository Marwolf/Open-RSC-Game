package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class AgilityShortcuts implements OpLocTrigger,
	UseLocTrigger {

	private static final int SHORTCUT_FALADOR_HANDHOLD = 693;
	private static final int SHORTCUT_BRIMHAVEN_SWING = 694;
	private static final int SHORTCUT_BRIMHAVEN_BACK_SWING = 695;
	private static final int SHORTCUT_EDGE_DUNGEON_SWING = 684;
	private static final int SHORTCUT_EDGE_DUNGEON_BACK_SWING = 685;
	private static final int SHORTCUT_WEST_COALTRUCKS_LOG = 681;
	private static final int SHORTCUT_EAST_COALTRUCKS_LOG = 680;
	private static final int SHILO_VILLAGE_ROCKS_TO_BRIDGE = 710;
	private static final int SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP = 691;
	private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING = 628;
	private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK = 627;
	private static final int SHORTCUT_YANILLE_AGILITY_LEDGE = 614;
	private static final int SHORTCUT_YANILLE_AGILITY_LEDGE_BACK = 615;
	private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE = 636;
	private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP = 633;
	private static final int SHORTCUT_YANILLE_PIPE = 656;
	private static final int SHORTCUT_YANILLE_PIPE_BACK = 657;
	private static final int GREW_ISLAND_ROPE_ATTACH = 662;
	private static final int GREW_ISLAND_ROPE_ATTACHED = 663;
	private static final int GREW_ISLAND_SWING_BACK = 664;
	private static final int EAST_KARAMJA_LOG = 692;
	private static final int EAST_KARAMJA_STONES = 701;
	private static final int YANILLE_CLIMBING_ROCKS = 1029;
	private static final int YANILLE_WATCHTOWER_HANDHOLDS = 658;
	private static final int TAVERLY_PIPE = 1236;
	private static final int TAVERLY_PIPE_RETURN = 1237;
	private static final int ENTRANA_RUBBLE = 1286;

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return inArray(obj.getID(), SHORTCUT_YANILLE_PIPE,
			SHORTCUT_YANILLE_PIPE_BACK,
			SHORTCUT_YANILLE_PILE_OF_RUBBLE,
			SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP,
			SHORTCUT_YANILLE_AGILITY_LEDGE,
			SHORTCUT_YANILLE_AGILITY_LEDGE_BACK, SHORTCUT_FALADOR_HANDHOLD,
			SHORTCUT_BRIMHAVEN_SWING, SHORTCUT_BRIMHAVEN_BACK_SWING,
			SHORTCUT_EDGE_DUNGEON_SWING, SHORTCUT_EDGE_DUNGEON_BACK_SWING,
			SHORTCUT_WEST_COALTRUCKS_LOG, SHORTCUT_EAST_COALTRUCKS_LOG,
			SHORTCUT_YANILLE_AGILITY_ROPESWING,
			SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK,
			GREW_ISLAND_ROPE_ATTACHED,
			GREW_ISLAND_SWING_BACK,
			EAST_KARAMJA_LOG,
			EAST_KARAMJA_STONES,
			YANILLE_CLIMBING_ROCKS,
			YANILLE_WATCHTOWER_HANDHOLDS,
			SHILO_VILLAGE_ROCKS_TO_BRIDGE,
			SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP,
			TAVERLY_PIPE,
			TAVERLY_PIPE_RETURN,
			ENTRANA_RUBBLE);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		p.setBusy(true);
		switch (obj.getID()) {
			case SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP:
				if (getCurrentLevel(p, Skills.AGILITY) < 32) {
					p.message("You need an agility level of 32 to climb the rocks");
					p.setBusy(false);
					return;
				}
				Functions.mes(p, "The bridge beyond this fence looks very unsafe.");
				Functions.mes(p, "However, you could try to negotiate it if you're feeling very agile.");
				p.message("Would you like to try?");
				int jumpMenu = multi(p,
					"No thanks! It looks far too dangerous!",
					"Yes, I'm totally brave and quite agile!");
				if (jumpMenu == 0) {
					Functions.mes(p, "You decide that common sense is the better part of valour.",
						"And stop yourself from being hurled to what must be an ");
					p.message("inevitable death.");
				} else if (jumpMenu == 1) {
					Functions.mes(p, "You prepare to negotiate the bridge fence...");
					Functions.mes(p, "You run and jump...");
					if (succeed(p, 32)) {
						p.message("...and land perfectly on the other side!");
						if (p.getX() >= 460) { // back
							p.teleport(458, 828);
						} else {
							p.teleport(460, 828);
						}
					} else {
						p.message("...slip and fall incompetently into the river below!");
						p.teleport(458, 832);
						say(p, null, "* Ahhhhhhhhhh! *");
						p.damage((getCurrentLevel(p, Skills.HITS) / 10));
						delay(500);
						p.teleport(458, 836);
						p.damage((getCurrentLevel(p, Skills.HITS) / 10));
						delay(1000);
						say(p, null, "* Gulp! *");
						delay(1500);
						p.teleport(459, 841);
						say(p, null, "* Gulp! *");
						delay(1000);
						p.message("You just manage to drag your pitiful frame onto the river bank.");
						say(p, null, "* Gasp! *");
						p.damage((getCurrentLevel(p, Skills.HITS) / 10));
						delay(1000);
						p.message("Though you nearly drowned in the river!");
					}
				}
				break;
			case SHILO_VILLAGE_ROCKS_TO_BRIDGE:
				if (getCurrentLevel(p, Skills.AGILITY) < 32) {
					p.message("You need an agility level of 32 to climb the rocks");
					p.setBusy(false);
					return;
				}
				Functions.mes(p, "These rocks look quite dangerous to climb.",
					"But you may be able to scale them.");
				p.message("Would you like to try?");
				int menu = multi(p,
					"Yes, I can easily climb this!",
					"Nope, I'm sure I'll probably fall!");
				if (menu == 0) {
					if (succeed(p, 32)) {
						Functions.mes(p, "You manage to climb the rocks succesfully and pick");
						if (obj.getX() == 450) {
							p.message("a route though the trecherous embankment to the top.");
							p.teleport(452, 829);
						} else {
							p.message("a route though the trecherous embankment to the bottom.");
							p.teleport(449, 828);
						}
					} else {
						p.teleport(450, 828);
						Functions.mes(p, "You fall and hurt yourself.");
						p.damage((getCurrentLevel(p, Skills.HITS) / 10));
						delay(500);
						p.teleport(449, 828);
					}
				} else if (menu == 1) {
					p.message("You decide not to climb the rocks.");
				}
				break;
			case SHORTCUT_FALADOR_HANDHOLD:
				if (getCurrentLevel(p, Skills.AGILITY) < 5) {
					p.message("You need an agility level of 5 to climb the wall");
					p.setBusy(false);
					return;
				}
				p.message("You climb over the wall");
				teleport(p, 338, 555);
				p.incExp(Skills.AGILITY, 50, true);
				break;
			case SHORTCUT_BRIMHAVEN_SWING:
				if (getCurrentLevel(p, Skills.AGILITY) < 10) {
					p.message("You need an agility level of 10 to attempt to swing on this vine");
					p.setBusy(false);
					return;
				}
				p.message("You grab the vine and try and swing across");
				delay(1000);
				teleport(p, 511, 669);
				p.message("You skillfully swing across the stream");
				say(p, null, "Aaaaahahah");
				p.incExp(Skills.AGILITY, 20, true);
				break;
			case SHORTCUT_BRIMHAVEN_BACK_SWING:
				if (getCurrentLevel(p, Skills.AGILITY) < 10) {
					p.message("You need an agility level of 10 to attempt to swing on this vine");
					p.setBusy(false);
					return;
				}
				p.message("You grab the vine and try and swing across");
				delay(1000);
				teleport(p, 508, 668);
				p.message("You skillfully swing across the stream");
				say(p, null, "Aaaaahahah");
				p.incExp(Skills.AGILITY, 20, true);
				break;
			case SHORTCUT_EDGE_DUNGEON_SWING:
				if (getCurrentLevel(p, Skills.AGILITY) < 15) {
					p.message("You need an agility level of 15 to attempt to swing on this rope");
					p.setBusy(false);
					return;
				}
				delay(1000);
				teleport(p, 207, 3221);
				p.message("You skillfully swing across the hole");
				p.incExp(Skills.AGILITY, 40, true);
				break;
			case SHORTCUT_EDGE_DUNGEON_BACK_SWING:
				if (getCurrentLevel(p, Skills.AGILITY) < 15) {
					p.message("You need an agility level of 15 to attempt to swing on this rope");
					p.setBusy(false);
					return;
				}
				delay(1000);
				teleport(p, 206, 3225);
				p.message("You skillfully swing across the hole");
				p.incExp(Skills.AGILITY, 40, true);
				break;
			case SHORTCUT_WEST_COALTRUCKS_LOG:
				if (getCurrentLevel(p, Skills.AGILITY) < 20) {
					p.message("You need an agility level of 20 to attempt balancing along this log");
					p.setBusy(false);
					return;
				}
				p.message("You stand on the slippery log");
				for (int x = 595; x >= 592; x--) {
					teleport(p, x, 458);
					delay(650);
				}
				p.message("and you walk across");
				p.incExp(Skills.AGILITY, 34, true);
				break;
			case SHORTCUT_EAST_COALTRUCKS_LOG:
				if (getCurrentLevel(p, Skills.AGILITY) < 20) {
					p.message("You need an agility level of 20 to attempt balancing along this log");
					p.setBusy(false);
					return;
				}
				p.message("You stand on the slippery log");
				for (int x = 595; x <= 598; x++) {
					teleport(p, x, 458);
					delay(650);
				}
				p.message("and you walk across");
				p.incExp(Skills.AGILITY, 34, true);
				break;
			// CONTINUE SHORTCUTS.
			case SHORTCUT_YANILLE_AGILITY_ROPESWING:
				if (getCurrentLevel(p, Skills.AGILITY) < 57) {
					p.message("You need an agility level of 57 to attempt to swing on this rope");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to swing on the rope");
						p.setBusy(false);
						return;
					}
				}
				p.message("You grab the rope and try and swing across");
				if (!succeed(p, 57, 77)) {
					Functions.mes(p, "You miss the opposite side and fall to the level below");
					teleport(p, 596, 3534);
					p.setBusy(false);
					return;
				}
				delay(2200);
				teleport(p, 596, 3581);
				p.message("You skillfully swing across the hole");
				p.incExp(Skills.AGILITY, 110, true);
				break;
			case SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK:
				if (getCurrentLevel(p, Skills.AGILITY) < 57) {
					p.message("You need an agility level of 57 to attempt to swing on this rope");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to swing on the rope");
						p.setBusy(false);
						return;
					}
				}
				p.message("You grab the rope and try and swing across");
				if (!succeed(p, 57, 77)) {
					Functions.mes(p, "You miss the opposite side and fall to the level below");
					teleport(p, 598, 3536);
					p.setBusy(false);
					return;
				}
				delay(2200);
				teleport(p, 598, 3585);
				p.message("You skillfully swing across the hole");
				p.incExp(Skills.AGILITY, 110, true);
				break;

			case SHORTCUT_YANILLE_AGILITY_LEDGE:
				if (getCurrentLevel(p, Skills.AGILITY) < 40) {
					p.message("You need an agility level of 40 to attempt balancing along this log");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to balance on the ledge");
						p.setBusy(false);
						return;
					}
				}
				p.message("You put your foot on the ledge and try to edge across");
				delay(2200);
				if (!succeed(p, 40, 65)) {
					Functions.mes(p, "you lose your footing and fall to the level below");
					teleport(p, 603, 3520);
					p.setBusy(false);
					return;
				}
				teleport(p, 601, 3563);
				p.setBusyTimer(1000);
				p.message("You skillfully balance across the hole");
				p.incExp(Skills.AGILITY, 90, true);
				break;
			case SHORTCUT_YANILLE_AGILITY_LEDGE_BACK:
				if (getCurrentLevel(p, Skills.AGILITY) < 40) {
					p.message("You need an agility level of 40 to attempt balancing along this log");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to balance on the ledge");
						p.setBusy(false);
						return;
					}
				}
				p.message("You put your foot on the ledge and try to edge across");
				delay(2200);
				if (!succeed(p, 40, 65)) {
					Functions.mes(p, "you lose your footing and fall to the level below");
					teleport(p, 603, 3520);
					p.setBusy(false);
					return;
				}
				p.setBusyTimer(1000);
				teleport(p, 601, 3557);
				p.message("You skillfully balance across the hole");
				p.incExp(Skills.AGILITY, 90, true);
				break;

			case SHORTCUT_YANILLE_PILE_OF_RUBBLE:
				if (getCurrentLevel(p, Skills.AGILITY) < 67) {
					p.message("You need an agility level of 67 to attempt to climb down the rubble");
					p.setBusy(false);
					return;
				}
				teleport(p, 580, 3525);
				p.message("You climb down the pile of rubble");
				break;
			case SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP:
				if (getCurrentLevel(p, Skills.AGILITY) < 67) {
					p.message("You need an agility level of 67 to attempt to climb up the rubble");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to climb up the rubble");
						p.setBusy(false);
						return;
					}
				}
				teleport(p, 582, 3573);
				p.message("You climb up the pile of rubble");
				p.incExp(Skills.AGILITY, 54, true);
				break;

			case SHORTCUT_YANILLE_PIPE:
				if (getCurrentLevel(p, Skills.AGILITY) < 49) {
					p.message("You need an agility level of 49 to attempt to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to squeeze through the pipe");
						p.setBusy(false);
						return;
					}
				}
				p.message("You squeeze through the pipe");
				delay(2200);
				teleport(p, 608, 3568);
				p.incExp(Skills.AGILITY, 30, true);
				break;
			case SHORTCUT_YANILLE_PIPE_BACK:
				if (getCurrentLevel(p, Skills.AGILITY) < 49) {
					p.message("You need an agility level of 49 to attempt to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to squeeze through the pipe");
						p.setBusy(false);
						return;
					}
				}
				p.message("You squeeze through the pipe");
				delay(2200);
				teleport(p, 605, 3568);
				p.incExp(Skills.AGILITY, 30, true);
				break;
			case GREW_ISLAND_ROPE_ATTACHED:
				if (getCurrentLevel(p, Skills.AGILITY) < 30) {
					p.message("You need an agility level of 30 to attempt to swing across the stream");
					p.setBusy(false);
					return;
				}
				p.message("You grab the rope and try and swing across");
				delay(2200);
				teleport(p, 664, 755);
				p.message("You skillfully swing across the stream");
				p.incExp(Skills.AGILITY, 50, true);
				break;
			case GREW_ISLAND_SWING_BACK:
				p.message("You grab the rope and try and swing across");
				delay(2200);
				teleport(p, 666, 755);
				p.message("You skillfully swing across the stream");
				p.incExp(Skills.AGILITY, 50, true);
				break;
			case EAST_KARAMJA_LOG:
				if (getCurrentLevel(p, Skills.AGILITY) < 32) {
					p.message("You need an agility level of 32 to attempt balancing along this log");
					p.setBusy(false);
					return;
				}
				p.message("You attempt to walk over the the slippery log..");
				delay(1900);
				if (!succeed(p, 32)) {
					teleport(p, 368, 781);
					delay(650);
					p.message("@red@You fall into the stream!");
					p.message("You lose some health");
					teleport(p, 370, 776);
					p.damage(1);
					p.setBusy(false);
					return;
				}
				if (p.getX() <= 367) {
					teleport(p, 368, 781);
					delay(650);
					teleport(p, 370, 781);
				} else {
					teleport(p, 368, 781);
					delay(650);
					teleport(p, 366, 781);
				}
				p.message("...and make it without any problems!");
				p.incExp(Skills.AGILITY, 10, true);
				break;
			case EAST_KARAMJA_STONES:
				p.setBusyTimer(1500);
				if (getCurrentLevel(p, Skills.AGILITY) < 32) {
					p.message("You need an agility level of 32 to step on these stones");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too fatigued to continue.");
						p.setBusy(false);
						return;
					}
				}
				p.message("You jump onto the rock");
				if (p.getY() <= 805) {
					teleport(p, 347, 806);
					delay(650);
					if (!succeed(p, 32)) {
						delay(900);
						teleport(p, 341, 809);
						p.message("@red@!!! You Fall !!!");
						Functions.mes(p, "You get washed up on the other side of the river...",
							"After being nearly half drowned");
						p.damage((int) (p.getSkills().getLevel(Skills.HITS) / 4) + 2);
						p.setBusy(false);
						return;
					}
					teleport(p, 346, 808);
				} else {
					teleport(p, 346, 807);
					delay(650);
					if (!succeed(p, 32)) {
						delay(900);
						teleport(p, 341, 805);
						p.message("@red@!!! You Fall !!!");
						Functions.mes(p, "You get washed up on the other side of the river...",
							"After being nearly half drowned");
						p.damage((int) (p.getSkills().getLevel(Skills.HITS) / 4) + 2);
						p.setBusy(false);
						return;
					}
					teleport(p, 347, 805);
				}
				p.message("And cross the water without problems.");
				p.incExp(Skills.AGILITY, 10, true);
				break;
			case YANILLE_CLIMBING_ROCKS:
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to climb up the wall");
						p.setBusy(false);
						return;
					}
				}
				if (getCurrentLevel(p, Skills.AGILITY) < 15) {
					p.message("You need an agility level of 15 to climb the wall");
					p.setBusy(false);
					return;
				}
				p.message("You climb over the wall");
				teleport(p, 624, 741);
				p.incExp(Skills.AGILITY, 40, true);
				break;
			case YANILLE_WATCHTOWER_HANDHOLDS:
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to climb up the wall");
						p.setBusy(false);
						return;
					}
				}
				if (getCurrentLevel(p, Skills.AGILITY) < 18) {
					p.message("You need an agility level of 18 to climb the wall");
					p.setBusy(false);
					return;
				}
				p.message("You climb up the wall");
				p.teleport(637, 1680);
				p.message("And climb in through the window");
				p.incExp(Skills.AGILITY, 50, true);
				break;

			case TAVERLY_PIPE_RETURN:
			if (getCurrentLevel(p, Skills.AGILITY) < 70) {
				p.message("You need an agility level of 70 to attempt to squeeze through the pipe");
				p.setBusy(false);
				return;
			}
			if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
				if (p.getFatigue() >= p.MAX_FATIGUE) {
					p.message("You are too tired to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
			}
			p.message("You squeeze through the pipe");
			teleport(p, 372, 3352);
			p.incExp(Skills.AGILITY, 30, true);
			p.setBusy(false);
			break;

			case TAVERLY_PIPE:
			if (getCurrentLevel(p, Skills.AGILITY) < 70) {
				p.message("You need an agility level of 70 to attempt to squeeze through the pipe");
				p.setBusy(false);
				return;
			}
			if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
				if (p.getFatigue() >= p.MAX_FATIGUE) {
					p.message("You are too tired to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
			}
			p.message("You squeeze through the pipe");
			teleport(p, 375, 3352);
			p.incExp(Skills.AGILITY, 30, true);
			p.setBusy(false);
			break;
			case ENTRANA_RUBBLE:
				if (getCurrentLevel(p, Skills.AGILITY) < 55) {
					p.message("You need an agility level of 55 to climb the rubble");
					p.setBusy(false);
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to climb the rubble");
						p.setBusy(false);
						return;
					}
				}
				delay(p.getWorld().getServer().getConfig().GAME_TICK);
				if (p.getLocation().getY() < 550) {
					teleport(p, 434, 551);
					p.incExp(Skills.AGILITY, 15, true);
				} else {
					teleport(p, 434, 549);
					p.incExp(Skills.AGILITY, 15, true);
				}
				p.setBusy(false);
				break;
		}

		p.setBusy(false);
	}

	boolean succeed(Player player, int req) {
		return Formulae.calcProductionSuccessful(req, getCurrentLevel(player, Skills.AGILITY), false, req + 30);
	}

	boolean succeed(Player player, int req, int lvlStopFail) {
		return Formulae.calcProductionSuccessful(req, getCurrentLevel(player, Skills.AGILITY), true, lvlStopFail);
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return obj.getID() == GREW_ISLAND_ROPE_ATTACH && item.getCatalogId() == ItemId.ROPE.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == GREW_ISLAND_ROPE_ATTACH && item.getCatalogId() == ItemId.ROPE.id()) {
			p.message("you tie the rope to the tree");
			p.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
			p.getWorld().replaceGameObject(obj,
				new GameObject(p.getWorld(), obj.getLocation(), 663, obj.getDirection(), obj
					.getType()));
			p.getWorld().delayedSpawnObject(obj.getLoc(), 60000);
		}
	}

	// HERRING SPAWN I CHEST ROOM SINISTER CHEST = 362, 614, 3564
}
