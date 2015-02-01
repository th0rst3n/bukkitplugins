package me.c0sm0cat.bukkit.BlockSetter;

import java.util.logging.Level;

import me.c0sm0cat.bukkit.BlockSetter.events.DispenseListener;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author th0rst3n
 * 
 *         Base plugin class, only for registering the listener
 *
 */
public class BlockSetterPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		DispenseListener dislis = new DispenseListener(this);
		this.getLogger().log(Level.INFO, "enabled, Registered DispenseListener: " + dislis.isRegistered() + ", Instance " + dislis.toString());
	}

	@Override
	public void onDisable() {
		this.getLogger().log(Level.INFO, "disabled.");
	}

	@Override
	public void onLoad() {
		this.getLogger().log(Level.INFO, "loaded.");
	}

}
