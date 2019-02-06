package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Eating implements InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.isEdible();
	}

	@Override
	public void onInvAction(Item item, Player player) {
		if (item.isEdible()) {
			if (player.cantConsume()) {
				return;
			}
			player.setConsumeTimer(1200);
			ActionSender.sendSound(player, "eat");

			int id = item.getID();
			if (id == ItemId.SPECIAL_DEFENSE_CABBAGE.id() || id == ItemId.CABBAGE.id()) {
				player.message("You eat the " + item.getDef().getName()
					+ ". Yuck!");
				if (id == ItemId.SPECIAL_DEFENSE_CABBAGE.id()) {
					int lv = player.getSkills().getMaxStat(Skills.DEFENSE);
					int newStat = player.getSkills().getLevel(Skills.DEFENSE) + 1;
					if (newStat <= (lv + 1))
						player.getSkills().setLevel(Skills.DEFENSE, newStat);
				}
			} else if (id == ItemId.CHOCOLATE_BOMB.id() || id == ItemId.GNOME_WAITER_CHOCOLATE_BOMB.id()) {
				message(player, "You eat the choc bomb");
				player.message("it tastes great");
			} else if (id == ItemId.VEGBALL.id() || id == ItemId.GNOME_WAITER_VEGBALL.id()) {
				message(player, "You eat the veg ball");
				player.message("it tastes quite good");
			} else if (id == ItemId.WORM_HOLE.id() || id == ItemId.GNOME_WAITER_WORM_HOLE.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				playerTalk(player, null, "yuck");
				player.message("that was awful");
			} else if (id == ItemId.TANGLED_TOADS_LEGS.id() || id == ItemId.GNOME_WAITER_TANGLED_TOADS_LEGS.id()) {
				message(player, "You eat the tangled toads legs");
				player.message("it tastes.....slimey");
			} else if (id == ItemId.ROCK_CAKE.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				playerTalk(player, null, "Ow! I nearly broke a tooth!");
				player.message("You feel strangely heavier and more tired");
			} else if (id == ItemId.EQUA_LEAVES.id())
				player.message("You eat the leaves..chewy but tasty");

			else if (id == ItemId.LEMON.id())
				player.message("You eat the lemon. Yuck!");

			else if (id == ItemId.LEMON_SLICES.id() || id == ItemId.DICED_LEMON.id()) {
				player.message("You eat the " + item.getDef().getName().toLowerCase() + " ..they're very sour");
			} else if (id == ItemId.DWELLBERRIES.id())
				player.message("You eat the berrys..quite tasty");

			else if (id == ItemId.LIME.id())
				player.message("You eat the lime ..it's quite sour");

			else if (id == ItemId.LIME_SLICES.id() || id == ItemId.LIME_CHUNKS.id())
				player.message("You eat the " + item.getDef().getName().toLowerCase() + "..they're quite sour");

			else if (id == ItemId.ORANGE_SLICES.id())
				player.message("You eat the orange slices ...yum");

			else if (id == ItemId.DICED_ORANGE.id())
				player.message("You eat the orange cubes ...yum");

			else if (id == ItemId.FRESH_PINEAPPLE.id())
				player.message("You eat the pineapple ...yum");

			else if (id == ItemId.PINEAPPLE_CHUNKS.id())
				player.message("You eat the pineapple chunks ..yum");

			else if (id == ItemId.CREAM.id())
				player.message("You eat the cream..you get some on your nose");

			else if (id == ItemId.GNOMEBOWL.id()) {
				message(player, 1200, "You eat the gnome bowl");
				player.message("it's pretty tastless");
				resetGnomeCooking(player);
			} else if (id == ItemId.GNOMECRUNCHIE.id()) {
				player.message("You eat the gnome crunchies");
				resetGnomeCooking(player);
			} else if (id == ItemId.CHEESE_AND_TOMATO_BATTA.id()
				|| id == ItemId.GNOME_WAITER_CHEESE_AND_TOMATO_BATTA.id()) {
				message(player, "You eat the cheese and tomato batta");
				player.message("it's quite tasty");
			} else if (id == ItemId.TOAD_BATTA.id() || id == ItemId.GNOME_WAITER_TOAD_BATTA.id()
				|| id == ItemId.WORM_BATTA.id() || id == ItemId.GNOME_WAITER_WORM_BATTA.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				player.message("it's a bit chewy");
			} else if (id == ItemId.FRUIT_BATTA.id() || id == ItemId.GNOME_WAITER_FRUIT_BATTA.id()
				|| id == ItemId.VEG_BATTA.id() || id == ItemId.GNOME_WAITER_VEG_BATTA.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				player.message("it's tastes pretty good");
			} else if (id == ItemId.CHOC_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_CHOC_CRUNCHIES.id()
				|| id == ItemId.SPICE_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_SPICE_CRUNCHIES.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				player.message("they're very tasty");
			} else if (id == ItemId.WORM_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_WORM_CRUNCHIES.id()
				|| id == ItemId.TOAD_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_TOAD_CRUNCHIES.id()) {
				message(player, "You eat the " + item.getDef().getName().toLowerCase());
				player.message("they're a bit chewy");
			} else if (eatenByParts(item)) {
				String itemName = item.getDef().getName().toLowerCase();
				String message = "";
				String needleSt = "half a";
				String origName;
				if (itemName.contains("pie")) {
					if (itemName.contains(needleSt+" ")) {
						origName = itemName.substring(7); // "half a "
					} else if (itemName.contains(needleSt+"n ")) {
						origName = itemName.substring(8); // "half an "
					} else { // complete pies
						origName = itemName;
					}
					
					message = "You eat half of a" + (startsWithVowel(origName) ? "n " : " ") + origName;
				} else if (itemName.contains("pizza")) {
					message = "You eat half of the pizza";
				} 
				// cakes
				else {
					if (itemName.contains("slice")) {
						message = "You eat the slice of cake";
					} else if (itemName.contains("partial")) {
						message = "You eat some more of the cake";
					} else if (itemName.contains("cake")) {
						message = "You eat part of the cake";
					} else { // shouldn't happen
						message = "You eat the " + itemName;
					}
				}
				player.message(message);
			} else
				player.message("You eat the " + item.getDef().getName().toLowerCase());

			final boolean heals = player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS);
			if (heals) {
				int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + item.eatingHeals();
				if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
					newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
				}
				player.getSkills().setLevel(Skills.HITPOINTS, newHp);
			}
			sleep(325);
			if (heals) {
				player.message("It heals some health");
			}
			player.getInventory().remove(item);

			addFoodResult(player, id);
		}
	}
	
	// cakes, pies and pizzas (except plain pizza) are eaten partially
	private boolean eatenByParts(Item item) {
		String itemName = item.getDef().getName().toLowerCase();
		return itemName.contains("cake") || itemName.contains("pie")
				|| (itemName.contains("pizza") && !itemName.contains("plain pizza"));
	}
	
	private boolean startsWithVowel(String testString) {
		String vowels = "aeiou";
		return vowels.indexOf(Character.toLowerCase(testString.charAt(0))) != -1;
	}

	private void addFoodResult(Player player, int id) {

		if (id == ItemId.MEAT_PIZZA.id())
			player.getInventory().add(new Item(ItemId.HALF_MEAT_PIZZA.id()));

		else if (id == ItemId.ANCHOVIE_PIZZA.id())
			player.getInventory().add(new Item(ItemId.HALF_ANCHOVIE_PIZZA.id()));
		
		else if (id == ItemId.PINEAPPLE_PIZZA.id())
			player.getInventory().add(new Item(ItemId.HALF_PINEAPPLE_PIZZA.id()));

		else if (id == ItemId.CAKE.id())
			player.getInventory().add(new Item(ItemId.PARTIAL_CAKE.id()));

		else if (id == ItemId.PARTIAL_CAKE.id())
			player.getInventory().add(new Item(ItemId.SLICE_OF_CAKE.id()));

		else if (id == ItemId.CHOCOLATE_CAKE.id())
			player.getInventory().add(new Item(ItemId.PARTIAL_CHOCOLATE_CAKE.id()));

		else if (id == ItemId.PARTIAL_CHOCOLATE_CAKE.id())
			player.getInventory().add(new Item(ItemId.CHOCOLATE_SLICE.id()));

		else if (id == ItemId.APPLE_PIE.id())
			player.getInventory().add(new Item(ItemId.HALF_AN_APPLE_PIE.id()));

		else if (id == ItemId.HALF_AN_APPLE_PIE.id())
			player.getInventory().add(new Item(ItemId.PIE_DISH.id()));

		else if (id == ItemId.REDBERRY_PIE.id())
			player.getInventory().add(new Item(ItemId.HALF_A_REDBERRY_PIE.id()));

		else if (id == ItemId.HALF_A_REDBERRY_PIE.id())
			player.getInventory().add(new Item(ItemId.PIE_DISH.id()));

		else if (id == ItemId.MEAT_PIE.id())
			player.getInventory().add(new Item(ItemId.HALF_A_MEAT_PIE.id()));

		else if (id == ItemId.HALF_A_MEAT_PIE.id())
			player.getInventory().add(new Item(ItemId.PIE_DISH.id()));
	}
}
