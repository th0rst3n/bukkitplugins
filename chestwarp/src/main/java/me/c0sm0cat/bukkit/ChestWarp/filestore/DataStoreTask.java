package me.c0sm0cat.bukkit.ChestWarp.filestore;

import java.util.Set;
import java.util.logging.Level;

import me.c0sm0cat.bukkit.ChestWarp.data.WarpSignRegistry;

import org.bukkit.Bukkit;

public class DataStoreTask implements Runnable {

	public void run() {
		Bukkit.getLogger().log(Level.INFO, "Saving ChestWarp data to disk...");

		Set<String> data = WarpSignRegistry.INSTANCE.getWarpSignLocations();

		if (data != null) {
			FileStore.INSTANCE.saveData(data);
		}

		Bukkit.getLogger().log(Level.INFO, "Saving done.");

	}

}
