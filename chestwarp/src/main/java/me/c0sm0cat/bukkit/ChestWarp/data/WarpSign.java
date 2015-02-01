package me.c0sm0cat.bukkit.ChestWarp.data;

import java.util.logging.Level;

import me.c0sm0cat.bukkit.ChestWarp.helper.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

/**
 * @author th0rst3n
 * 
 *         Class representing a warp station for chests. A warp station consists
 *         of a sign with the teleport info for the station, a block the sign
 *         must be attached to and a block above that block where the chest must
 *         be placed.
 * 
 *         All checking and functionality is based around a string
 *         representation of blocks locations in the following pattern:
 *         [worldname]#[x]:[y]:[z]
 *
 */
public class WarpSign extends AbstractWarpSign {

	public final static String WARP_MARKER = "[ChestWarp]";
	public final static ChatColor COLOR_CODE = ChatColor.DARK_RED;
	public final static Material WARP_BASE_MATERIAL = Material.ENDER_STONE;

	private Block warpBase;
	private Block warpBlock;
	private Block signBlock;
	private Block destinationBlock;
	private org.bukkit.material.Sign signState;

	private String signLocation;
	private String warpBaseLocation;
	private String warpBlockLocation;
	private String destinationLocation;

	// private constructor, use one of the getInstance methods instead
	private WarpSign(Sign sign, String[] lines) {
		super(sign);

		this.signBlock = sign.getBlock();
		this.signState = (org.bukkit.material.Sign) signBlock.getState().getData();

		warpBase = signBlock.getRelative(signState.getAttachedFace());
		warpBlock = warpBase.getRelative(BlockFace.UP);

		signLocation = Utils.getLocationString(signBlock.getLocation());
		warpBaseLocation = Utils.getLocationString(warpBase.getLocation());
		warpBlockLocation = Utils.getLocationString(warpBlock.getLocation());

		String destWorld = lines[1];
		if (destWorld == null || destWorld.equals("")) {
			destWorld = signBlock.getWorld().getName();
		}
		World world = Bukkit.getWorld(destWorld);

		Vector destCoords = getCoordinates(lines[2]);

		Location destination = new Location(world, destCoords.getBlockX(), destCoords.getBlockY(), destCoords.getBlockZ());
		this.destinationBlock = destination.getBlock();
		destinationLocation = Utils.getLocationString(destination);
	}

	public Location getSignLocation() {
		return signBlock.getLocation();
	}

	public String getSignLocationString() {
		return signLocation;
	}

	public Location getWarpBaseLocation() {
		return warpBase.getLocation();
	}

	public String getWarpBaseLocationString() {
		return warpBaseLocation;
	}

	public Location getWarpBlockLocation() {
		return warpBlock.getLocation();
	}

	public String getWarpBlockLocationString() {
		return warpBlockLocation;
	}

	public BlockFace getSignFacing() {
		return this.signState.getFacing();
	}

	public Location getDestinationLocation() {
		return destinationBlock.getLocation();
	}

	public String getDestinationLocationString() {
		return destinationLocation;
	}

	public boolean isWarpBlock(Block block) {
		if (block == null) {
			return false;
		}

		String locationString = Utils.getLocationString(block.getLocation());
		return isWarpBlock(locationString);
	}

	public boolean isWarpBlock(Location location) {
		if (location == null) {
			return false;
		}

		String locationString = Utils.getLocationString(location);
		return isWarpBlock(locationString);
	}

	public boolean isWarpBlock(String locationString) {
		if (locationString == null) {
			return false;
		}

		return locationString.equals(signLocation) || locationString.equals(warpBaseLocation) || locationString.equals(warpBlockLocation);
	}

	public static WarpSign getInstance(String locationString, String[] lines) {
		Location location = Utils.getLocationFromString(locationString);
		if (location == null) {
			Bukkit.getLogger().log(Level.INFO, "locationString null");
			return null;
		}

		Block block = location.getBlock();
		if (block.getType() != Material.WALL_SIGN) {
			Bukkit.getLogger().log(Level.INFO, "Sign is not a WALL_SIGN");
			return null;
		}

		Sign sign = (Sign) block.getState();

		if (lines == null) {
			lines = sign.getLines();
		}

		if (lines[0] == null || lines[1] == null || lines[2] == null) {
			Bukkit.getLogger().log(Level.INFO, "One line is null");
			return null;
		}

		if (!lines[0].equalsIgnoreCase(WARP_MARKER) && !lines[0].equalsIgnoreCase(COLOR_CODE + WARP_MARKER)) {
			Bukkit.getLogger().log(Level.INFO, "First line is not " + WARP_MARKER + ": " + lines[0] + ", check result: " + (!lines[0].equalsIgnoreCase(WARP_MARKER)));
			return null;
		}

		Vector signCoords = getCoordinates(lines[2]);
		if (signCoords == null) {
			Bukkit.getLogger().log(Level.INFO, "Line 3 has no coordinates");
			return null;
		}

		org.bukkit.material.Sign signState = (org.bukkit.material.Sign) block.getState().getData();
		Block warpBase = block.getRelative(signState.getAttachedFace());
		if (warpBase.getType() != WARP_BASE_MATERIAL) {
			Bukkit.getLogger().log(Level.INFO, "Facing block is not " + WARP_BASE_MATERIAL);
			return null;
		}

		String destWorld = lines[1];
		if (destWorld == null || destWorld.equals("")) {
			destWorld = block.getWorld().getName();
		}
		World world = Bukkit.getWorld(destWorld);

		if (world == null) {
			Bukkit.getLogger().log(Level.INFO, "Worldname " + lines[1] + " is no valid world");
			return null;
		}

		return new WarpSign(sign, lines);
	}

	public static WarpSign getInstance(Sign sign, String[] lines) {
		if (sign == null) {
			return null;
		}

		return getInstance(sign.getLocation(), lines);
	}

	public static WarpSign getInstance(Location location, String[] lines) {
		if (location == null) {
			return null;
		}

		String locationString = Utils.getLocationString(location);
		return getInstance(locationString, lines);
	}

	public static WarpSign getInstance(SignChangeEvent event) {
		if (event == null) {
			return null;
		}

		return getInstance(event.getBlock().getLocation(), event.getLines());
	}

	private static boolean isCoordString(String line) {
		return line.matches("-?\\d+,-?\\d+,-?\\d+");
	}

	private static Vector getCoordinates(String line) {
		if (!isCoordString(line)) {
			return null;
		}

		String[] numbers = line.split(",");

		int x = Integer.valueOf(numbers[0]);
		int y = Integer.valueOf(numbers[1]);
		int z = Integer.valueOf(numbers[2]);

		if (y < 1) {
			y = 1;
		}
		if (y > 256) {
			y = 256;
		}

		Vector result = new Vector(x, y, z);
		return result;
	}
}
