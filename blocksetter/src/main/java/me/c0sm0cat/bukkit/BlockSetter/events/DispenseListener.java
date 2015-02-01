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

/**
 * @author th0rst3n
 * 
 * Listener class for BlockDispenseEvents. Catches events, checks if preconditions for placing a block are given and sets
 * the block the dispenser faces to the dispensed material.
 *
 */
/**
 * @author thorsten
 *
 */
/**
 * @author thorsten
 *
 */
/**
 * @author thorsten
 *
 */
/**
 * @author thorsten
 *
 */
public class DispenseListener implements Listener {

	private static final Material ACTIVATION_MATERIAL = Material.EYE_OF_ENDER;
	private static final boolean SEND_INGAME_DEBUG = false;

	private boolean isRegistered = false;
	private JavaPlugin plugin;

	// self-registering constructor
	public DispenseListener(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		isRegistered = true;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	// main event handler method. Event gets cancelled if all preconditions are
	// given to prevent normal item dispensing
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

	// method for placing the block in front of the dispenser
	private void setBlock(BlockDispenseEvent event) {
		Dispenser dispenser = (Dispenser) event.getBlock().getState();
		org.bukkit.material.Dispenser mDispenser = (org.bukkit.material.Dispenser) dispenser.getData();
		BlockFace facing = mDispenser.getFacing();
		Block facingBlock = event.getBlock().getRelative(facing);
		ItemStack item = event.getItem();

		if (SEND_INGAME_DEBUG) {
			sendDebugToAllPlayers("Dispensed item is a " + item.getType());
		}

		// the activation item may also get dispensed. in that case another
		// ItemStack must be dispensed, as the activation item must stay in the
		// dispenser
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

	// meta method for handling item removing from the dispensers inv
	private void removeItemFromDispenserInventory(ItemStack item, Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();
		removeSingleItemFromInventory(item, inventory);
		dispenser.update(true);
	}

	// method for manually removing the placed item from the dispenser. As the
	// event got cancelled, this must be done manually
	private void removeSingleItemFromInventory(ItemStack item, Inventory inventory) {
		int amount = 0;
		for (ItemStack is : inventory.all(item.getType()).values()) {
			amount = amount + is.getAmount();
		}

		if (amount == 1) {
			inventory.remove(item.getType());
			return;
		}

		// securely removing a single item from a dispenser inv seems to be a
		// hassle, so looping through the inventory is currently the only
		// working way for removing an item
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

	// meta method for searching for placeable items in the dispensers inv
	private ItemStack findPlaceableItemInDispenser(Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();
		ItemStack result = findPlaceableItemInInventory(inventory);
		return result;
	}

	// method for finding a placeable item in the dispenser inv in case the
	// originally dispensed item was the activation item, which has to stay in
	// the dispensers inv
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

	// method for checking preconditions, if a dispensed item can be placed as a
	// block instead of beeing dispensed as an item
	// preconditions are:
	// - block dispensing must be a dispenser (not a dropper)
	// - at least one item of the activation material must be in the dispensers
	// inv
	// - the item dispensed must be placeable as a block
	// - the block the dispenser is facing must be of a replaceable type
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

	// method to avoid dispensing the activation item in case the preconditions
	// were not given
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

	// funny little helper method to annoy players ;)
	private void sendDebugToAllPlayers(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("[BlockSetter - DEBUG] " + message);
		}
	}

	// method to check the material of the block the dispenser is facing at.
	// Only if the block is one of the listed materials the block material will
	// be replaced
	private boolean isReplacableMaterial(Material material) {
		return material == Material.AIR || material == Material.LAVA || material == Material.WATER || material == Material.GRASS || material == Material.LONG_GRASS || material == Material.VINE || material == Material.STATIONARY_LAVA || material == Material.STATIONARY_WATER
				|| material == Material.SNOW || material == Material.DEAD_BUSH || material == Material.FIRE;
	}

	// method for replacing the material of the given block to the material of
	// the given item stack
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