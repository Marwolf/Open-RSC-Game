package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

/**
 * @author n0m
 */
public class KingBlackDragonPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Npc attackerNpc = ((Npc) attacker);
			return attackerNpc.getID() == 477;
		} else if (defender.isNpc()) {
			Npc defenderNpc = ((Npc) defender);
			return defenderNpc.getID() == 477;
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isPlayer()) {
			if (attacker.getSkills().getLevel(Skills.PRAYER) > 1)
				attacker.getSkills().setLevel(Skills.PRAYER, 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(Skills.PRAYER) > 1)
				defender.getSkills().setLevel(Skills.PRAYER, 1);
		}

	}

}
