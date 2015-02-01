package me.c0sm0cat.bukkit.ChestWarp.events;

import me.c0sm0cat.bukkit.ChestWarp.data.WarpSignRegistry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakListener implements Listener {

	private boolean isRegistered = false;

	public BlockBreakListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		isRegistered = true;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// Utils.logDebug(event.getBlock().getType() + "");

		if (event.getBlock().getType() != Material.WALL_SIGN) {
			// Utils.logDebug("No wall sign");
			return;
		}

		removeWarpSignFromRegistry(event.getBlock());
	}

	@EventHandler
	public void signDetachCheck(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN) {
			org.bukkit.material.Sign signState = (org.bukkit.material.Sign) block.getState().getData();
			Block attachedBlock = block.getRelative(signState.getAttachedFace());
			if (attachedBlock.getType() == Material.AIR) {
				// Utils.logDebug("Sign popped off");
				removeWarpSignFromRegistry(event.getBlock());
			}
		}
	}

	private void removeWarpSignFromRegistry(Block block) {
		if (!WarpSignRegistry.INSTANCE.hasWarpSign(block.getLocation())) {
			// Utils.logDebug("Sign not found in registry");
			return;
		}

		WarpSignRegistry.INSTANCE.removeWarpSign(block.getLocation());
		// Utils.logDebug("Sign removed from registry");
	}
}
