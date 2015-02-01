package me.c0sm0cat.bukkit.ChestWarp.helper;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
	private final static boolean SEND_INGAME_DEBUG = false;

	public static void logDebug(String message) {
		Bukkit.getLogger().log(Level.FINEST, message);
		if (SEND_INGAME_DEBUG) {
			Bukkit.broadcastMessage(message);
		}
	}

	public static String getLocationString(Location loc) {
		if (loc == null) {
			return null;
		}

		StringBuilder result = new StringBuilder();
		result.append(loc.getWorld().getName());
		result.append("#");
		result.append(loc.getBlockX());
		result.append(":");
		result.append(loc.getBlockY());
		result.append(":");
		result.append(loc.getBlockZ());
		return result.toString();
	}

	public static Location getLocationFromString(String key) {
		if (!key.matches("\\w+#-?\\d+:-?\\d+:-?\\d+")) {
			return null;
		}

		String[] sharpSplit = key.split("#");
		if (sharpSplit.length != 2) {
			return null;
		}

		String[] colonSplit = sharpSplit[1].split(":");
		if (colonSplit.length != 3) {
			return null;
		}

		String worldName = sharpSplit[0];
		int x = Integer.valueOf(colonSplit[0]);
		int y = Integer.valueOf(colonSplit[1]);
		int z = Integer.valueOf(colonSplit[2]);

		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return null;
		}

		Location result = new Location(world, x, y, z);

		return result;
	}
}
