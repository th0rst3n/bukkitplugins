package me.c0sm0cat.bukkit.ChestWarp.events;

import me.c0sm0cat.bukkit.ChestWarp.data.WarpSign;
import me.c0sm0cat.bukkit.ChestWarp.data.WarpSignRegistry;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SignChangeListener implements Listener {

	private boolean isRegistered = false;

	public SignChangeListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		isRegistered = true;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		WarpSign warpSign = WarpSign.getInstance(event);
		if (warpSign == null) {
			// Utils.logDebug("No WarpSign");
			return;
		} else {
			// Utils.logDebug("WarpSign created");

			String[] lines = event.getLines();
			event.setLine(0, WarpSign.COLOR_CODE + lines[0]);

			if (!WarpSignRegistry.INSTANCE.hasWarpSign(warpSign.getSignLocationString())) {
				WarpSignRegistry.INSTANCE.addWarpSign(warpSign);
				// Utils.logDebug("WarpSign added to registry");
			}
		}
	}
}
