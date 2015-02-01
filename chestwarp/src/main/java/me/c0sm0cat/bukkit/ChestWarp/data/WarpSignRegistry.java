package me.c0sm0cat.bukkit.ChestWarp.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.c0sm0cat.bukkit.ChestWarp.helper.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;

public enum WarpSignRegistry {

	INSTANCE;

	private Map<String, WarpSign> registry;

	private WarpSignRegistry() {
		registry = new HashMap<String, WarpSign>();
	}

	public boolean hasWarpSign(WarpSign sign) {
		if (sign == null) {
			return false;
		}
		return registry.containsKey(sign.getSignLocationString());
	}

	public boolean hasWarpSign(Location location) {
		if (location == null) {
			return false;
		}
		String locationString = Utils.getLocationString(location);
		return hasWarpSign(locationString);
	}

	public boolean hasWarpSign(String locationString) {
		if (locationString == null) {
			return false;
		}
		return registry.containsKey(locationString);
	}

	public WarpSign getWarpSign(Location location) {
		if (location == null) {
			return null;
		}

		String locationString = Utils.getLocationString(location);
		return getWarpSign(locationString);
	}

	public WarpSign getByWarpBase(Block block) {
		if (block == null) {
			return null;
		}
		return getByWarpBase(block.getLocation());
	}

	public WarpSign getByWarpBase(Location location) {
		if (location == null) {
			return null;
		}
		String locationString = Utils.getLocationString(location);
		return getByWarpBase(locationString);
	}

	public WarpSign getByWarpBase(String locationString) {
		if (locationString == null) {
			return null;
		}

		for (WarpSign next : registry.values()) {
			if (next.getWarpBaseLocationString().equals(locationString)) {
				return next;
			}
		}

		return null;
	}

	public WarpSign getWarpSign(String locationString) {
		if (locationString == null) {
			return null;
		}

		if (!hasWarpSign(locationString)) {
			return null;
		}

		WarpSign result = registry.get(locationString);
		return result;
	}

	public void addWarpSign(WarpSign sign) {
		if (sign == null) {
			return;
		}

		if (hasWarpSign(sign)) {
			return;
		}

		registry.put(sign.getSignLocationString(), sign);
	}

	public void removeWarpSign(WarpSign sign) {
		if (sign == null) {
			return;
		}

		if (!hasWarpSign(sign)) {
			return;
		}

		registry.remove(sign.getSignLocationString());
	}

	public boolean isWarpBase(Block block) {
		if (block.getType() != WarpSign.WARP_BASE_MATERIAL) {
			// Utils.logDebug("No WarpBase: block is of type " +
			// block.getType());
			return false;
		}

		// Utils.logDebug("WarpBase material found");

		String locationString = Utils.getLocationString(block.getLocation());

		for (WarpSign next : registry.values()) {
			if (next.getWarpBaseLocationString().equals(locationString)) {
				// Utils.logDebug("Block is WarpBase");
				return true;
			}
		}
		// Utils.logDebug("Block is no WarpBase");
		return false;
	}

	public void removeWarpSign(Location location) {
		if (location == null) {
			return;
		}
		String locationString = Utils.getLocationString(location);
		removeWarpSign(locationString);
	}

	public void removeWarpSign(String locationString) {
		if (locationString == null) {
			return;
		}

		if (!hasWarpSign(locationString)) {
			return;
		}

		registry.remove(locationString);
	}

	public Set<String> getWarpSignLocations() {
		return registry.keySet();
	}

	public void rebuildRegistryFromLocationStrings(Set<String> locationStrings) {
		if (locationStrings == null) {
			return;
		}
		registry.clear();
		for (String next : locationStrings) {
			WarpSign warpSign = WarpSign.getInstance(next, null);
			if (warpSign != null) {
				registry.put(warpSign.getSignLocationString(), warpSign);
			}
		}
	}

}
