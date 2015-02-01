package me.c0sm0cat.bukkit.ChestWarp;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import me.c0sm0cat.bukkit.ChestWarp.data.WarpSignRegistry;
import me.c0sm0cat.bukkit.ChestWarp.events.BlockBreakListener;
import me.c0sm0cat.bukkit.ChestWarp.events.PistonListener;
import me.c0sm0cat.bukkit.ChestWarp.events.SignChangeListener;
import me.c0sm0cat.bukkit.ChestWarp.filestore.DataStoreTask;
import me.c0sm0cat.bukkit.ChestWarp.filestore.FileStore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author th0rst3n
 * 
 *         ChestWarp bukkit plugin. Allows to teleport a chest with its
 *         inventory from one place to another, also between worlds
 *
 */
public class ChestWarpPlugin extends JavaPlugin {

	private BukkitTask dataStoreTask;

	@Override
	public void onEnable() {
		if (!FileStore.INSTANCE.isInitialized()) {
			FileStore.INSTANCE.init(this);
		}

		// load all "warp stations" previously set on the server and store them
		// in the registry.
		Set<String> data = null;
		try {
			data = FileStore.INSTANCE.loadData();
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error loading ChestWarp data, disabling plugin", e);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (data != null) {
			WarpSignRegistry.INSTANCE.rebuildRegistryFromLocationStrings(data);
		}

		// 4 listeners are needed (nested in 3 classes)
		BlockBreakListener bblis = new BlockBreakListener(this);
		this.getLogger().log(Level.INFO, "Registered BlockBreakListener: " + bblis.isRegistered() + ", Instance " + bblis.toString());

		SignChangeListener sclis = new SignChangeListener(this);
		this.getLogger().log(Level.INFO, "Registered SignChangeListener: " + sclis.isRegistered() + ", Instance " + sclis.toString());

		PistonListener pilis = new PistonListener(this);
		this.getLogger().log(Level.INFO, "Registered PistonListener: " + pilis.isRegistered() + ", Instance " + pilis.toString());

		dataStoreTask = this.getServer().getScheduler().runTaskTimerAsynchronously(this, new DataStoreTask(), 6000, 6000);
		this.getLogger().log(Level.INFO, "DataStore task started, ID: " + dataStoreTask.getTaskId());

		this.getLogger().log(Level.INFO, "enabled.");
	}

	@Override
	public void onDisable() {

		this.getServer().getScheduler().cancelAllTasks();

		Set<String> data = WarpSignRegistry.INSTANCE.getWarpSignLocations();

		// save warp station data to disc before unloading to prevent data loss
		if (data != null) {
			FileStore.INSTANCE.saveData(data);
		}

		this.getLogger().log(Level.INFO, "disabled.");
	}

	@Override
	public void onLoad() {
		this.getLogger().log(Level.INFO, "loaded.");
	}

}
