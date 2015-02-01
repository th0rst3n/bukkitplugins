package me.c0sm0cat.bukkit.ChestWarp.events;

import me.c0sm0cat.bukkit.ChestWarp.data.WarpSign;
import me.c0sm0cat.bukkit.ChestWarp.data.WarpSignRegistry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public class PistonListener implements Listener {

	private boolean isRegistered = false;

	public PistonListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		isRegistered = true;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event) {
		if (!checkPreconditions(event)) {
			return;
		}

		WarpSign sign = getWarpSign(event);
		if (sign == null) {
			return;
		}

		Location source = sign.getWarpBlockLocation();
		if (!checkWarpConditions(source)) {
			return;
		}

		// Utils.logDebug("Chest can be warped");
		warpChest(sign.getWarpBlockLocation(), sign.getDestinationLocation());
	}

	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (!checkPreconditions(event)) {
			return;
		}

		WarpSign sign = getWarpSign(event);
		if (sign == null) {
			return;
		}

		Location source = sign.getDestinationLocation();
		if (!checkWarpConditions(source)) {
			return;
		}

		// Utils.logDebug("Chest can be warped back");
		warpChest(sign.getDestinationLocation(), sign.getWarpBlockLocation());
	}

	private WarpSign getWarpSign(BlockPistonEvent event) {
		BlockFace direction = event.getDirection();
		Block block = event.getBlock();
		Block warpBase = block.getRelative(direction, 2);
		WarpSign sign = WarpSignRegistry.INSTANCE.getByWarpBase(warpBase);
		if (sign == null) {
			return null;
		}

		return sign;
	}

	private boolean checkWarpConditions(Location source) {
		if (source.getBlock().getType() != Material.CHEST) {
			// Utils.logDebug("Source block is no chest");
			return false;
		}

		Chest chest = (Chest) source.getBlock().getState();
		if (chest.getInventory().getSize() == 54) {
			// Utils.logDebug("Chest is double chest");
			return false;
		}

		return true;
	}

	private boolean checkPreconditions(BlockPistonEvent event) {
		if (event.isSticky()) {
			// Utils.logDebug("Piston is sticky");
			return false;
		}

		BlockFace direction = event.getDirection();
		Block block = event.getBlock();
		Block checkBlock = block.getRelative(direction);

		if (checkBlock.getType() != Material.AIR && checkBlock.getType() != Material.PISTON_EXTENSION) {
			// Utils.logDebug("Facing block is not air or piston");
			return false;
		}
		checkBlock = block.getRelative(direction, 2);

		if (checkBlock.getType() != WarpSign.WARP_BASE_MATERIAL) {
			// Utils.logDebug("2nd facing block is not " +
			// WarpSign.WARP_BASE_MATERIAL + ": " + checkBlock.getType());
			return false;
		}

		return WarpSignRegistry.INSTANCE.isWarpBase(checkBlock);
	}

	private void warpChest(Location source, Location destination) {
		Chest chest = (Chest) source.getBlock().getState();
		ItemStack[] sourceContents = chest.getInventory().getContents();
		ItemStack[] destinationContents = cloneItemStack(sourceContents);
		chest.getInventory().clear();
		MaterialData materialData = chest.getData();
		chest.update(true);
		source.getBlock().setType(Material.AIR);
		source.getWorld().strikeLightningEffect(source);

		destination.getBlock().setType(Material.CHEST);
		chest = (Chest) destination.getBlock().getState();
		chest.setData(materialData);
		chest.getInventory().setContents(destinationContents);
		chest.update(true);
		destination.getWorld().strikeLightningEffect(destination);
	}

	private ItemStack[] cloneItemStack(ItemStack[] source) {
		ItemStack[] result = new ItemStack[source.length];

		ItemStack next;
		for (int i = 0; i < source.length; i++) {
			if (source[i] == null) {
				continue;
			}
			next = source[i].clone();
			result[i] = next;
		}

		return result;

	}
}
