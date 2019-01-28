package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Event implements CommandListener {
	private static final String[] towns = {"varrock", "falador", "draynor", "portsarim", "karamja", "alkharid",
		"lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby",
		"ardougne", "yanille", "lostcity", "gnome", "shilovillage", "tutorial", "modroom"};

	private static final Point[] townLocations = {Point.location(122, 509), Point.location(304, 542),
		Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693),
		Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498),
		Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663),
		Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518),
		Point.location(703, 527), Point.location(400, 850), Point.location(217, 740), Point.location(75, 1641)};

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isEvent();
	}

	/**
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("teleport") || cmd.equalsIgnoreCase("tp") || cmd.equalsIgnoreCase("town") || cmd.equalsIgnoreCase("goto") || cmd.equalsIgnoreCase("tpto") || cmd.equalsIgnoreCase("teleportto")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [town/player] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [town/player] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] OR");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
				return;
			}

			Player p = null;
			boolean isTown = false;
			String town = "";
			int x = -1;
			int y = -1;
			Point originalLocation;
			Point teleportTo;

			if(args.length == 1) {
				p = player;
				town = args[0];
				isTown = true;
			}
			else if(args.length == 2) {
				try {
					x = Integer.parseInt(args[0]);
					isTown = false;

					try {
						y = Integer.parseInt(args[1]);
						p = player;
					}
					catch(NumberFormatException ex) {
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y]");
						return;
					}
				}
				catch(NumberFormatException ex) {
					p = world.getPlayer(DataConversions.usernameToHash(args[0]));
					town = args[1];
					isTown = true;
				}
			}
			else if(args.length >= 3) {
				p = world.getPlayer(DataConversions.usernameToHash(args[0]));
				try {
					x = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
					return;
				}
				try {
					y = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
					return;
				}
				isTown = false;
			}

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not teleport a staff member of equal or greater rank.");
				return;
			}

			if(player.isJailed() && p.getUsernameHash() == player.getUsernameHash() && !player.isAdmin()) {
				player.message(messagePrefix + "You can not teleport while you are jailed.");
				return;
			}

			originalLocation = p.getLocation();

			if (isTown) {
				int townIndex = -1;
				for (int i = 0; i < towns.length; i++) {
					if (town.equalsIgnoreCase(towns[i])) {
						townIndex = i;
						break;
					}
				}

				// townFound will == -1 when not found
				if(townIndex == -1) {
					// townIndex to find a town, look for a player instead...
					Player tpTo = world.getPlayer(DataConversions.usernameToHash(town));

					if (tpTo == null) {
						player.message(messagePrefix + "Invalid target");
						return;
					}

					teleportTo = tpTo.getLocation();
				} else {
					teleportTo = townLocations[townIndex];
				}
			}
			else {
				if(!world.withinWorld(x, y)) {
					player.message(messagePrefix + "Invalid coordinates");
					return;
				}

				teleportTo = new Point(x,y);
			}

			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			p.teleport(teleportTo.getX(), teleportTo.getY(), true);
			player.message(messagePrefix + "You have teleported " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been teleported to " + p.getLocation() + " from " + originalLocation);
		}
		else if (cmd.equalsIgnoreCase("blink")) {
			player.setAttribute("blink", !player.getAttribute("blink", false));
			player.message(messagePrefix + "Your blink status is now " + player.getAttribute("blink", false));
			GameLogging.addQuery(new StaffLog(player, 10, "Blink was set - " + player.getAttribute("blink", false)));
		}
		else if (cmd.equalsIgnoreCase("invisible") || cmd.equalsIgnoreCase("invis")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p != null) {
				p.toggleInvisible();
				String invisibleText = p.isInvisible() ? "invisible" : "visible";
				player.message(messagePrefix + p.getUsername() + " is now " + invisibleText);
				p.message(messagePrefix + "A staff member has made you " + invisibleText);
				GameLogging.addQuery(new StaffLog(player, 14, player.getUsername() + " has made " + p.getUsername() + " " + invisibleText));
			} else {
				player.message(messagePrefix + "Invalid name or player is not online");
			}
		}
		else if (cmd.equalsIgnoreCase("invulnerable") || cmd.equalsIgnoreCase("invul")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p != null) {
				p.toggleInvulnerable();
				String invulnerableText = p.isInvulnerable() ? "invulnerable" : "vulnerable";
				player.message(messagePrefix + p.getUsername() + " is now " + invulnerableText);
				p.message(messagePrefix + "A staff member has made you " + invulnerableText);
				GameLogging.addQuery(new StaffLog(player, 22, player.getUsername() + " has made " + p.getUsername() + " " + invulnerableText));
			} else {
				player.message(messagePrefix + "Invalid name or player is not online");
			}
		}
		else if (cmd.equals("check")) {
			Player target = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(target == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			String username = target.getUsername();
			String currentIp = null;
			if (target == null) {
				player.message(
					messagePrefix + "No online character found named '" + username + "'.. checking database..");
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase()
						.prepareStatement("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `username`=?");
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					if (!result.next()) {
						player.message(messagePrefix + "Error character not found in MySQL");
						return;
					}
					currentIp = result.getString("login_ip");
					result.close();
					player.message(messagePrefix + "Found character '" + username + "' with IP: " + currentIp
						+ ", fetching other characters..");
				} catch (SQLException e) {
					e.printStackTrace();
					player.message(messagePrefix + "A MySQL error has occured! " + e.getMessage());
					return;
				}
			} else {
				currentIp = target.getCurrentIP();
			}

			if (currentIp == null) {
				player.message(messagePrefix + "An unknown error has occured!");
				return;
			}

			try {
				PreparedStatement statement = DatabaseConnection.getDatabase()
					.prepareStatement("SELECT `username` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `login_ip` LIKE ?");
				statement.setString(1, currentIp);
				ResultSet result = statement.executeQuery();

				List<String> names = new ArrayList<>();
				while (result.next()) {
					names.add(result.getString("username"));
				}
				StringBuilder builder = new StringBuilder("@red@").append(username.toUpperCase())
					.append(" @whi@currently has ").append(names.size() > 0 ? "@gre@" : "@red@")
					.append(names.size()).append(" @whi@registered characters.");

				if (names.size() > 0) {
					builder.append(" % % They are: ");
				}
				for (int i = 0; i < names.size(); i++) {

					builder.append("@yel@")
						.append((World.getWorld().getPlayer(DataConversions.usernameToHash(names.get(i))) != null
							? "@gre@" : "@red@") + names.get(i));

					if (i != names.size() - 1) {
						builder.append("@whi@, ");
					}
				}

				GameLogging.addQuery(new StaffLog(player, 18, target));
				ActionSender.sendBox(player, builder.toString(), names.size() > 10);
				result.close();
			} catch (SQLException e) {
				player.message(messagePrefix + "A MySQL error has occured! " + e.getMessage());
			}
		}
		else if(cmd.equalsIgnoreCase("seers") || cmd.equalsIgnoreCase("toggleseers") || cmd.equalsIgnoreCase("partyhall") || cmd.equalsIgnoreCase("togglepartyhall")) {
			int time;
			if(args.length >= 1) {
				try {
					time = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (time_in_minutes)");
					return;
				}
			} else {
				time = 60;
			}

			if(!player.getLocation().isInSeersPartyHall()) {
				player.message(messagePrefix + "This command can only be run within the vicinity of the seers party hall");
				return;
			}

			boolean upstairs = player.getLocation().isInSeersPartyHallUpstairs();
			Point objectLoc =  upstairs ? new Point(495,1411) : new Point(495,467);
			final GameObject existingObject = player.getViewArea().getGameObject(objectLoc);

			if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() != 18 && existingObject.getID() != 17)) {
				player.message(messagePrefix + "Could not enable seers party hall " + (upstairs ? "upstairs" : "downstairs") + " object exists: " + existingObject.getGameObjectDef().getName());
			}
			else if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() == 18 || existingObject.getID() == 17)) {
				World.getWorld().unregisterGameObject(existingObject);
				player.message(messagePrefix + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been disabled.");
			} else {
				GameObject newObject = new GameObject(objectLoc, 18, 0, 0);
				World.getWorld().registerGameObject(newObject);
				Server.getServer().getEventHandler().add(new SingleEvent(null, time * 60000) {
					@Override
					public void action() {
						World.getWorld().unregisterGameObject(newObject);
					}
				});
				player.message(messagePrefix + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been enabled.");
			}
		}
		else if (cmd.equalsIgnoreCase("stopevent")) {
			World.EVENT_X = -1;
			World.EVENT_Y = -1;
			World.EVENT = false;
			World.EVENT_COMBAT_MIN = -1;
			World.EVENT_COMBAT_MAX = -1;
			player.message(messagePrefix + "Event disabled");
			GameLogging.addQuery(new StaffLog(player, 8, "Stopped an ongoing event"));
		}
		else if (cmd.equalsIgnoreCase("setevent") || cmd.equalsIgnoreCase("startevent")) {
			if (args.length < 4) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int x = -1;
			try {
				x = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int y = -1;
			try {
				y = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int cbMin = -1;
			try {
				cbMin = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int cbMax = -1;
			try {
				cbMax = Integer.parseInt(args[3]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			World.EVENT_X = x;
			World.EVENT_Y = y;
			World.EVENT = true;
			World.EVENT_COMBAT_MIN = cbMin;
			World.EVENT_COMBAT_MAX = cbMax;
			player.message(messagePrefix + "Event enabled: " + x + ", " + y + ", Combat level range: " + World.EVENT_COMBAT_MIN + " - "
				+ World.EVENT_COMBAT_MAX + "");
			GameLogging.addQuery(new StaffLog(player, 9, "Created event at: (" + x + ", " + y + ") cb-min: " + World.EVENT_COMBAT_MIN + " cb-max: " + World.EVENT_COMBAT_MAX + ""));
		}
		else if (cmd.equalsIgnoreCase("setgroup") || cmd.equalsIgnoreCase("setrank") || cmd.equalsIgnoreCase("group") || cmd.equalsIgnoreCase("rank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] OR to set a group");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [group_id/group_name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (args.length == 1) {
				player.message(messagePrefix + p.getStaffName() + "@whi@ has group " + Group.getStaffPrefix(p.getGroupID()) + Group.GROUP_NAMES.get(p.getGroupID()) + (player.isDev() ? " (" + p.getGroupID() + ")" : ""));
			} else if (args.length >= 2){
				if (!player.isAdmin()) {
					player.message(messagePrefix + "You do not have permission to modify users' group.");
					return;
				}

				int newGroup = -1;
				int oldGroup = p.getGroupID();
				String newGroupName;
				String oldGroupName = Group.GROUP_NAMES.get(oldGroup);

				try {
					newGroup = Integer.parseInt(args[1]);
					newGroupName = Group.GROUP_NAMES.get(newGroup);
				} catch (NumberFormatException e) {
					newGroupName = "";
					for (int i = 1; i < args.length; i++)
						newGroupName += args[i] + " ";
					newGroupName = newGroupName.trim();

					for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
						if (newGroupName.equalsIgnoreCase(entry.getValue())) {
							newGroup = entry.getKey();
							newGroupName = entry.getValue();
							break;
						}
					}
				}

				if (Group.GROUP_NAMES.get(newGroup) == null) {
					player.message(messagePrefix + "Invalid group_id or group_name");
					return;
				}

				if (player.getGroupID() >= newGroup || player.getGroupID() >= p.getGroupID()) {
					player.message(messagePrefix + "You can't to set " + p.getStaffName() + "@whi@ to group " + Group.getStaffPrefix(newGroup) + newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));
					return;
				}

				p.setGroupID(newGroup);
				p.message(messagePrefix + player.getStaffName() + "@whi@ has set your group to " + Group.getStaffPrefix(newGroup) + newGroupName + (p.isDev() ? " (" + newGroup + ")" : ""));
				player.message(messagePrefix + "Set " + p.getStaffName() + "@whi@ to group " + Group.getStaffPrefix(newGroup) + newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));

				GameLogging.addQuery(new StaffLog(player, 23, player.getUsername() + " has changed " + p.getUsername() + "'s group to " + newGroupName + " from " + oldGroupName));
			}
		}
		else if((cmd.equalsIgnoreCase("bank") || cmd.equalsIgnoreCase("quickbank")) && !player.isAdmin() && player.getUsernameHash() == DataConversions.usernameToHash("shar")) {
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		}
	}
}
