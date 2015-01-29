package me.c0sm0cat.bukkit.BlockSetter.events;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public class DispenseListener implements Listener {

	private static final Material ACTIVATION_MATERIAL = Material.EYE_OF_ENDER;
	private static final boolean SEND_INGAME_DEBUG = false;

	private boolean isRegistered = false;
	private JavaPlugin plugin;

	public DispenseListener(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		isRegistered = true;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	@EventHandler
	public void onDispense(BlockDispenseEvent event) {
		if (checkPreconditions(event)) {
			event.setCancelled(true);
			plugin.getLogger().log(Level.FINEST, "All preconditions set, cancelling event");

			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("All preconditions set, cancelling event");
			}

			this.setBlock(event);
		}
	}

	private void setBlock(BlockDispenseEvent event) {
		Dispenser dispenser = (Dispenser) event.getBlock().getState();
		org.bukkit.material.Dispenser mDispenser = (org.bukkit.material.Dispenser) dispenser.getData();
		BlockFace facing = mDispenser.getFacing();
		Block facingBlock = event.getBlock().getRelative(facing);
		ItemStack item = event.getItem();

		if (SEND_INGAME_DEBUG) {
			sendDebugToAllPlayers("Dispensed item is a " + item.getType());
		}

		if (item.getType() == ACTIVATION_MATERIAL) {
			item = findPlaceableItemInDispenser(dispenser);
			if (item == null) {
				plugin.getLogger().log(Level.WARNING, "Preconditions were ok, but found no placebale item in dispenser inventory");
				return;
			}

			plugin.getLogger().log(Level.FINEST, "Dispensed item was " + ACTIVATION_MATERIAL + ", item changed to " + item.getType());
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Dispensed item was " + ACTIVATION_MATERIAL + ", item changed to " + item.getType());
			}
		}

		setBlockMaterialFromItem(facingBlock, item);
		removeItemFromDispenserInventory(item, dispenser);
	}

	private void removeItemFromDispenserInventory(ItemStack item, Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();
		removeSingleItemFromInventory(item, inventory);
		dispenser.update(true);
	}

	private void removeSingleItemFromInventory(ItemStack item, Inventory inventory) {
		int amount = 0;
		for (ItemStack is : inventory.all(item.getType()).values()) {
			amount = amount + is.getAmount();
		}

		if (amount == 1) {
			inventory.remove(item.getType());
			return;
		}

		ItemStack[] invContents = inventory.getContents();
		for (int i = 0; i < invContents.length; i++) {
			ItemStack nextItem = invContents[i];
			if (nextItem != null && nextItem.getType() == item.getType() && nextItem.getData().getData() == item.getData().getData()) {
				if (nextItem.getAmount() > 1) {
					nextItem.setAmount(nextItem.getAmount() - 1);
				} else {
					nextItem.setAmount(0);
				}
				invContents[i] = nextItem;
				inventory.setContents(invContents);
				break;
			}
		}
	}

	private ItemStack findPlaceableItemInDispenser(Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();
		ItemStack result = findPlaceableItemInInventory(inventory);
		return result;
	}

	private ItemStack findPlaceableItemInInventory(Inventory inventory) {
		ItemStack[] invContents = inventory.getContents();
		for (int i = 0; i < invContents.length; i++) {
			ItemStack nextItem = invContents[i];
			if (nextItem != null && nextItem.getType().isBlock()) {
				return nextItem;
			}
		}

		return null;
	}

	private boolean checkPreconditions(BlockDispenseEvent event) {
		if (!(event.getBlock().getState() instanceof Dispenser)) {
			plugin.getLogger().log(Level.FINEST, "Block dispensing is not a dispenser");
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Block dispensing is not a dispenser");
			}
			return false;
		}

		Dispenser dispenser = (Dispenser) event.getBlock().getState();
		Inventory inventory = dispenser.getInventory();

		if (!inventory.contains(ACTIVATION_MATERIAL)) {
			plugin.getLogger().log(Level.FINEST, "Dispenser does not hold an " + ACTIVATION_MATERIAL);
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Dispenser does not hold an " + ACTIVATION_MATERIAL);
			}
			return false;
		}

		ItemStack item = event.getItem();
		if (!item.getType().isBlock() && item.getType() != ACTIVATION_MATERIAL) {
			plugin.getLogger().log(Level.FINEST, "Dispensed item is no placeable block or not an " + ACTIVATION_MATERIAL);
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Dispensed item is no plcceable block or not an " + ACTIVATION_MATERIAL);
			}
			return false;
		}

		org.bukkit.material.Dispenser mDispenser = (org.bukkit.material.Dispenser) dispenser.getData();
		BlockFace facing = mDispenser.getFacing();
		Block facingBlock = event.getBlock().getRelative(facing);
		if (!isReplacableMaterial(facingBlock.getType())) {
			plugin.getLogger().log(Level.FINEST, "Facing neighbour block is not replacable. Facing is " + facing + ", neighbour block is " + facingBlock.getType());
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Facing neighbour block is not replacable. Facing is " + facing + ", neighbour block is " + facingBlock.getType());
			}
			avoidDispensingActivationItem(event);
			return false;
		}

		return true;
	}

	private void avoidDispensingActivationItem(BlockDispenseEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() == ACTIVATION_MATERIAL) {
			event.setCancelled(true);
			plugin.getLogger().log(Level.FINEST, "Dispensed item is activation item, cancelling event");
			if (SEND_INGAME_DEBUG) {
				sendDebugToAllPlayers("Dispensed item is activation item, cancelling event");
			}
		}
	}

	private void sendDebugToAllPlayers(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("[BlockSetter - DEBUG] " + message);
		}
	}

	private boolean isReplacableMaterial(Material material) {
		return material == Material.AIR || material == Material.LAVA || material == Material.WATER || material == Material.GRASS || material == Material.LONG_GRASS || material == Material.VINE || material == Material.STATIONARY_LAVA || material == Material.STATIONARY_WATER
				|| material == Material.SNOW || material == Material.DEAD_BUSH || material == Material.FIRE;
	}

	private void setBlockMaterialFromItem(Block block, ItemStack item) {
		MaterialData materialData = item.getData();
		Material material = item.getType();
		block.setType(material);
		BlockState blockState = block.getState();
		blockState.setData(materialData);
		blockState.update(true);

		plugin.getLogger().log(Level.FINEST, "Facing block set to " + item.getType());
		if (SEND_INGAME_DEBUG) {
			sendDebugToAllPlayers("Facing block set to " + item.getType());
		}
	}
}