package com.openrsc.server.model.world.region;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GameObjectType;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class Region {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The RegionManager this Region belongs to
	 */
	private final RegionManager regionManager;
	/**
	 * A list of players in this region.
	 */
	final private Multimap<Point, Player> players = Multimaps.synchronizedMultimap(HashMultimap.create());

	/**
	 * A list of NPCs in this region.
	 */
	final private Multimap<Point, Npc> npcs = Multimaps.synchronizedMultimap(HashMultimap.create());

	/**
	 * A list of objects in this region.
	 */
	final private Multimap<Point, GameObject> objects = Multimaps.synchronizedMultimap(HashMultimap.create());

	/**
	 * A list of objects in this region.
	 */
	final private Multimap<Point, GroundItem> items = Multimaps.synchronizedMultimap(HashMultimap.create());

	/**
	 * A list of tiles in this region.
	 */
	private volatile TileValue[][] tiles;

	/**
	 * The constant tile value used for this region.
	 */
	private volatile TileValue tile;

	/**
	 * The X index of this region
	 */
	private final int regionX;

	/**
	 * The Y index of this region
	 */
	private final int regionY;

	/**
	 * This constructor is used to create a blank region
	 *
	 * @param regionManager
	 * @param regionX
	 * @param regionY
	 */
	public Region(final RegionManager regionManager, final int regionX, final int regionY) {
		this.regionManager = regionManager;
		this.regionX = regionX;
		this.regionY = regionY;

		this.tiles = new TileValue[Constants.REGION_SIZE][Constants.REGION_SIZE];
		this.tile = null;

		for (int i = 0; i < Constants.REGION_SIZE; i++) {
			for (int j = 0; j < Constants.REGION_SIZE; j++) {
				tiles[i][j] = new TileValue();
			}
		}
	}

	public void unload() {
		players.clear();
		npcs.clear();
		objects.clear();
		items.clear();
		tiles = null;
		tile = null;
	}

	/**
	 * Gets the list of players.
	 *
	 * @return The list of players.
	 */
	protected Collection<Player> getPlayers() {
		return players.values();
	}

	/**
	 * Gets the list of NPCs.
	 *
	 * @return The list of NPCs.
	 */
	protected Collection<Npc> getNpcs() {
		return npcs.values();
	}

	/**
	 * Gets the list of objects.
	 *
	 * @return The list of objects.
	 */
	public Collection<GameObject> getGameObjects() {
		return objects.values();
	}

	protected Collection<GroundItem> getGroundItems() {
		return items.values();
	}

	public void removeEntity(final Entity entity) {
		if (entity.isPlayer()) {
			players.remove(entity.getLocation(), entity);
		} else if (entity.isNpc()) {
			npcs.remove(entity.getLocation(), entity);
		} else if (entity instanceof GameObject) {
			objects.remove(entity.getLocation(), entity);
		} else if (entity instanceof GroundItem) {
			items.remove(entity.getLocation(), entity);
		}
	}

	public void addEntity(final Entity entity) {
		if (entity.isRemoved()) {
			return;
		}
		switch (entity.getEntityType()) {
			case PLAYER:
				players.put(entity.getLocation(), (Player) entity);
				break;
			case NPC:
				npcs.put(entity.getLocation(), (Npc) entity);
				break;
			case GAME_OBJECT:
				objects.put(entity.getLocation(), (GameObject) entity);
				break;
			case GROUND_ITEM:
				items.put(entity.getLocation(), (GroundItem) entity);
				break;
		}
	}

	private String stringifyEntities(String title, Multimap<Point, ? extends Entity> multimap) {
		StringBuilder sb = new StringBuilder(2000);
		sb.append(title).append(":\n");
		for (Entity entity : multimap.values()) {
			sb.append("\t").append(entity).append("\n");
		}
		return sb.toString();
	}

	public String toString() {
		return toString(true, true, true, true);
	}

	public String toString(final boolean debugPlayers, final boolean debugNpcs, final boolean debugItems, final boolean debugObjects) {
		final StringBuilder sb = new StringBuilder(2000);
		if (debugPlayers) {
			sb.append(stringifyEntities("Players", players)).append("\n");
		}
		if (debugNpcs) {
			sb.append(stringifyEntities("Npcs", npcs)).append("\n");
		}
		if (debugItems) {
			sb.append(stringifyEntities("Items", players)).append("\n");
		}
		if (debugObjects) {
			sb.append(stringifyEntities("Objects", objects)).append("\n");
		}
		return sb.toString();
	}

	private GameObject getGameObject(Point location, Entity observer, GameObjectType type, Integer direction) {
		return objects.get(location)
				.stream()
				.filter(obj -> type == null || obj.getGameObjectType() == type)
				.filter(obj -> observer == null || !obj.isInvisibleTo(observer))
				.filter(obj -> direction == null || obj.getDirection() == direction)
				.findFirst()
				.orElse(null);
	}

	public GameObject getGameObject(Point location) {
		return getGameObject(location, null, null, null);
	}

	public GameObject getGameObject(Point location, Entity entity) {
		return getGameObject(location, entity, GameObjectType.GAME_OBJECT, null);
	}

	public GameObject getWallGameObject(Point location, int direction) {
		return getGameObject(location, null, GameObjectType.WALL_OBJECT, direction);
	}

	public GameObject getWallGameObject(Point location, Entity entity) {
		return getGameObject(location, entity, GameObjectType.WALL_OBJECT, null);
	}

	public Npc getNpc(Point location, Entity observer) {
		return npcs.get(location)
				.stream()
				.filter(npc -> observer == null || !npc.isInvisibleTo(observer))
				.findFirst()
				.orElse(null);
	}

	public Player getPlayer(int x, int y, Entity observer, boolean includeSelf) {
		return players.get(new Point(x, y))
				.stream()
				.filter(player -> observer == null || !player.isInvisibleTo(observer))
				.filter(player -> observer == null || includeSelf || player.equals(observer))
				.findFirst()
				.orElse(null);
	}

	public Player getPlayer(int x, int y, Entity observer) {
		return getPlayer(x, y, observer, true);
	}

	public GroundItem getItem(final int id, final Point location, final Entity e) {
		final int x = location.getX();
		final int y = location.getY();
		for (final GroundItem i : getGroundItems()) {
			if (i.getID() == id && i.getX() == x && i.getY() == y && (e == null || !i.isInvisibleTo(e))) {
				return i;
			}
		}
		return null;
	}

	public TileValue getTileValue(final int regionX, final int regionY) {
		return tile != null ? tile : tiles[regionX][regionY];
	}

	public TileValue getTileValue(final Point regionPoint) {
		return getTileValue(regionPoint.getX(), regionPoint.getY());
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public int getRegionX() {
		return regionX;
	}

	public int getRegionY() {
		return regionY;
	}

	public void checkRegionValues() {
		boolean allTilesEqual = true;
		final TileValue firstTile = tiles[0][0];
		for (int i = 0; i < Constants.REGION_SIZE && allTilesEqual; i++) {
			for (int j = 0; j < Constants.REGION_SIZE && allTilesEqual; j++) {
				allTilesEqual = allTilesEqual && firstTile.equals(tiles[i][j]);
			}
		}

		if (allTilesEqual) {
			tile = tiles[0][0];
			tiles = null;
		}
	}
}
