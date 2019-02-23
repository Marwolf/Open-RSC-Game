package com.openrsc.server.model.entity.npc;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.npcYell;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;

public class NpcBehavior {

	protected long lastMovement;
	protected long lastTackleAttempt;
	private static final int[] TACKLING_XP = {7, 10, 15, 20};

	protected Npc npc;

	protected Mob target;
	protected Mob lastTarget;
	private State state = State.ROAM;

	public NpcBehavior(Npc npc) {
		this.npc = npc;
	}

	public void tick() {

		if (state == State.ROAM) {

			if (npc.inCombat()) {
				state = State.COMBAT;
				return;
			} else if (npc.isBusy()) {
				return;
			}

			target = null;
			if (System.currentTimeMillis() - lastMovement > 3000
				&& System.currentTimeMillis() - npc.getCombatTimer() > 3000
				&& npc.finishedPath()) {
				lastMovement = System.currentTimeMillis();
				lastTarget = null;
				int rand = DataConversions.random(0, 1);
				if (!npc.isBusy() && rand == 1 && !npc.isRemoved()) {
					int newX = DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX());
					int newY = DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY());
					if (!grandTreeGnome(npc) || npc.getLocation().equals(new Point(0,0))) {
						npc.walk(newX, newY);
					}
					else {
						Point p = walkablePoint(npc, Point.location(npc.getLoc().minX(), npc.getLoc().minY()),
								Point.location(npc.getLoc().maxX(), npc.getLoc().maxY()));
						npc.walk(p.getX(), p.getY());
					}
				}
			}
			if (System.currentTimeMillis() - npc.getCombatTimer() > 3000
				&& ((npc.getDef().isAggressive()
				&& !(npc.getID() == 40 && npc.getX() >= 208 && npc.getX() <= 211 && npc.getY() >= 545 && npc.getY() <= 546)) // Skeleton in draynor manor
				|| (npc.getLocation().inWilderness() && npc.getID() != 342 && npc.getID() != 233 && npc.getID() != 234 && npc.getID() != 235))
				|| (npc.getX() > 274 && npc.getX() < 283 && npc.getY() > 432 && npc.getY() < 441) // Black Knight's Fortress
			) {

				// We loop through all players in view.
				for (Player p : npc.getViewArea().getPlayersInView()) {

					int range = 1;
					switch (npc.getID()) {
						case 232: // Bandit
							range = 5;
							break;
						case 66: // Black Knight
							range = 10;
							break;
						default:
							break;
					}

					if (!canAggro(p) || !p.withinRange(npc, range))
						continue; // Can't aggro or is not in range.

					state = State.AGGRO;
					target = p;
					if (npc.getID() == 232) // Bandit
						npcTalk(p, npc, "You shall not pass");
					else if (npc.getID() == 66) // Black Knight
						npcTalk(p, npc, "Die intruder!!");
					break;
				}
			}
			if (System.currentTimeMillis() - lastTackleAttempt > 3000 &&
					npc.getDef().getName().toLowerCase().equals("gnome baller") && !(npc.getID() == 609 || npc.getID() == 610)) {
				for (Player p : npc.getViewArea().getPlayersInView()) {
					int range = 1;
					if (!p.withinRange(npc, range) || !hasItem(p, ItemId.GNOME_BALL.id())
							|| p.getCache().hasKey("gnomeball_npc"))
						continue; // Not in range, does not have a gnome ball or a gnome baller already has ball.
					
					//set tackle
					state = State.TACKLE;
					target = p;
				}
			}
		} else if (state == State.AGGRO) {

			// There should not be combat or aggro. Let's resume roaming.
			if (target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || target.inCombat()) {
				setRoaming();
			}

			// Target is not in range.
			else if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
				|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {
				setRoaming();
			}

			// Combat with another target - set state.
			else {
				if (npc.inCombat() && npc.getOpponent() != target) {
					target = npc.getOpponent();
					state = State.COMBAT;
				}

				lastMovement = System.currentTimeMillis();
				if (!checkTargetCombatTimer()) {
					npc.walkToEntity(target.getX(), target.getY());
					if (npc.withinRange(target, 1)
						&& npc.canReach(target)
						&& !target.inCombat()) {
						setFighting(target);
					}
				}
			}

		} else if (state == State.COMBAT) {
			lastTarget = target;
			target = npc.getOpponent();
			if (target == null || npc.isRemoved() || target.isRemoved()) {
				setRoaming();
			}
			if (npc.inCombat()) {
				if (DataConversions.inArray(Constants.GameServer.NPCS_THAT_DO_RETREAT, npc.getID())) {
					if (npc.getSkills().getLevel(Skills.HITPOINTS) <=
						Math.ceil(npc.getSkills().getMaxStat(Skills.HITPOINTS) * 0.20)) {
						if (npc.getSkills().getLevel(Skills.HITPOINTS) > 0
							&& npc.getOpponent().getHitsMade() >= 3) {
							retreat();
						}
					}
				}
			} else if (!npc.inCombat()) {
				if ((npc.getDef().isAggressive() &&
					lastTarget != null &&
					lastTarget.getCombatLevel() <= ((npc.getNPCCombatLevel() * 2) + 1)
				) || npc.getLocation().inWilderness()) {
					state = State.AGGRO;
					if (lastTarget != null)
						target = lastTarget;
				} else {
					setRoaming();
				}
			}

		} else if (state == State.TACKLE) {
			// There should not be tackle. Let's resume roaming.
			if (target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || target.inCombat() || target.isBusy()) {
				setRoaming();
			}
			// Target is not in range.
			else if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
				|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {
				setRoaming();
			}
			
			if (target.isPlayer()) {
				lastTackleAttempt = System.currentTimeMillis();
				Player p = (Player) target;
				showBubble(p, new Item(ItemId.GNOME_BALL.id()));
				message(p, "the gnome trys to tackle you");
				if (DataConversions.random(0, 1) == 0) {
					//successful avoiding tackles gives agility xp
					p.playerServerMessage(MessageType.QUEST, "You manage to push him away");
					npcYell(p, npc, "grrrrr");
					p.incExp(Skills.AGILITY, TACKLING_XP[DataConversions.random(0,3)], true);
				} else {
					p.playerServerMessage(MessageType.QUEST, "he takes the ball...");
					p.playerServerMessage(MessageType.QUEST, "and pushes you to the floor");
					removeItem(p, ItemId.GNOME_BALL.id(), 1);
					p.damage((int)(Math.ceil(p.getSkills().getLevel(Skills.HITPOINTS)*0.05)));
					playerTalk(p, null, "ouch");
					npcYell(p, npc, "yeah");
					p.getCache().set("gnomeball_npc", npc.getID());
				}
				tackle_retreat();
			}
		} else if (state == State.RETREAT || state == State.TACKLE_RETREAT) {
			if (npc.finishedPath()) setRoaming();
		}
	}

	public void retreat() {
		state = State.RETREAT;
		if (npc.getOpponent().isPlayer()) {
			Player victimPlayer = ((Player) npc.getOpponent());
			victimPlayer.resetAll();
			victimPlayer.message("Your opponent is retreating");
			ActionSender.sendSound(victimPlayer, "retreat");
		}
		npc.setLastCombatState(CombatState.RUNNING);
		npc.getOpponent().setLastCombatState(CombatState.WAITING);
		npc.resetCombatEvent();

		Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
			DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
		npc.walk(walkTo.getX(), walkTo.getY());
	}
	
	public void tackle_retreat() {
		state = State.TACKLE_RETREAT;
		npc.setLastCombatState(CombatState.RUNNING);
		target.setLastCombatState(CombatState.WAITING);
		npc.resetCombatEvent();

		Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
			DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
		npc.walk(walkTo.getX(), walkTo.getY());
	}

	private boolean canAggro(Mob p) {
		boolean outOfBounds = !p.getLocation().inBounds(npc.getLoc().minX - 4, npc.getLoc().minY - 4,
			npc.getLoc().maxX + 4, npc.getLoc().maxY + 4);

		boolean playerOccupied = p.inCombat();
		boolean playerCombatTimeout = System.currentTimeMillis()
			- p.getCombatTimer() < (p.getCombatState() == CombatState.RUNNING
			|| p.getCombatState() == CombatState.WAITING ? 3000 : 1500);

		boolean shouldAttack = p.getCombatLevel() <= ((npc.getNPCCombatLevel() * 2) + 1)
			|| npc.getLocation().inWilderness();

		boolean closeEnough = npc.canReach(p);

		return closeEnough && shouldAttack
			&& (p instanceof Player && (!((Player)p).isInvulnerable(npc) && !((Player)p).isInvisible(npc)))
			&& !outOfBounds && !playerOccupied && !playerCombatTimeout;
	}
	
	private boolean grandTreeGnome(Npc npc) {
		String npcName = npc.getDef().getName();
		return npcName.equalsIgnoreCase("gnome child") || npcName.equalsIgnoreCase("gnome local");
	}
	
	private Point walkablePoint(Npc npc, Point minP, Point maxP) {
		int currX = npc.getX();
		int currY = npc.getY();
		int radius = 8;
		int newX = DataConversions.random(Math.max(minP.getX(),currX-radius), Math.min(maxP.getX(),currX+radius));
		int newY = DataConversions.random(Math.max(minP.getY(),currY-radius), Math.min(maxP.getY(),currY+radius));
		if (Point.location(newX, newY).inBounds(680, 491, 696, 511)) {
			return Point.location(currX, currY);
		}
		return Point.location(newX, newY);
	}

	public State getBehaviorState() {
		return state;
	}

	boolean isChasing() {
		return state == State.AGGRO;
	}

	public void setChasing(Player player) {
		state = State.AGGRO;
		target = player;
	}

	Player getChasedPlayer() {
		if (target.isPlayer())
			return (Player) target;

		return null;
	}

	public boolean checkTargetCombatTimer() {
		return (System.currentTimeMillis() - target.getCombatTimer()
			< (target.getCombatState() == CombatState.RUNNING
			|| target.getCombatState() == CombatState.WAITING ? 3000 : 1500)
		);
	}

	public Mob getChaseTarget() {
		return target;
	}

	private void setRoaming() {
		state = State.ROAM;
	}

	private void setFighting(Mob target) {
		npc.startCombat(target);
		state = State.COMBAT;
	}

	public void onKill(Mob killed) {

	}

	enum State {
		ROAM, AGGRO, COMBAT, RETREAT, TACKLE, TACKLE_RETREAT;
	}
}
