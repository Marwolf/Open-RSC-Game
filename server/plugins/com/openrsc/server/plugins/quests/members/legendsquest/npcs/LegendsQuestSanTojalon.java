package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerNpcRunListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerNpcRunExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.sleep;

public class LegendsQuestSanTojalon implements PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener,
	PlayerNpcRunListener, PlayerNpcRunExecutiveListener {

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	private void attackMessage(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			npcTalk(p, n, "You have entered the Viyeldi caves and  your bravery must be tested.");
			n.setChasing(p);
			npcTalk(p, n, "Prepare yourself...San Tojalon will test your mettle.");
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCache().hasKey("cavernous_opening"))
				|| (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions"));
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 1) {
				p.getCache().set("viyeldi_companions", 2);
			}
			message(p, 1300, "A nerve tingling scream echoes around you as you slay the dead Hero.",
				"@yel@San Tojalon: Ahhhggggh",
				"@yel@San Tojalon: Forever must I live in this torment till this beast is slain...");
			sleep(650);
			LegendsQuestNezikchened.demonFight(p);
		}
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCache().hasKey("cavernous_opening")) {
			if (hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) || hasItem(p, ItemId.A_RED_CRYSTAL.id()) || hasItem(p, ItemId.A_GLOWING_RED_CRYSTAL.id())) {
				npcTalk(p, n, "A fearsome foe you are, and bettered me once have you done already.");
				p.message("Your opponent is retreating");
				n.remove();
			} else {
				npcTalk(p, n, "You have proved yourself of the honour..");
				p.resetCombatEvent();
				n.resetCombatEvent();
				p.message("Your opponent is retreating");
				npcTalk(p, n, "");
				n.remove();
				message(p, 1300, "A piece of crystal forms in midair and falls to the floor.",
					"You place the crystal in your inventory.");
				addItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !hasItem(p, ItemId.A_CHUNK_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockPlayerNpcRun(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions");
	}

	@Override
	public void onPlayerNpcRun(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			message(p, 1300, "As you try to make your escape,",
				"the Viyeldi fighter is recalled by the demon...",
				"@yel@Nezikchened : Ha, ha ha!",
				"@yel@Nezikchened : Run then fetid worm...and never touch my totem again...");
		}

	}
}
