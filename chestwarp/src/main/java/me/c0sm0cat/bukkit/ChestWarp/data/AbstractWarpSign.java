package me.c0sm0cat.bukkit.ChestWarp.data;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * @author th0rst3n
 * 
 *         Meta class to implement @see org.bukkit.block.Sign, itself backed by
 *         a given sign implementation. Class is used to extend any given sign
 *         implementation to create WarpSigns. All methods are the ones that
 *         must be implemented by the sign interface and are just a facade to
 *         the methods of the backing sign implementation.
 *
 */
public abstract class AbstractWarpSign implements Sign {

	private final Sign sign;

	public AbstractWarpSign(Sign sign) {
		this.sign = sign;
	}

	public Block getBlock() {
		return sign.getBlock();
	}

	public Chunk getChunk() {
		return sign.getChunk();
	}

	public MaterialData getData() {
		return sign.getData();
	}

	public byte getLightLevel() {
		return sign.getLightLevel();
	}

	public Location getLocation() {
		return sign.getLocation();
	}

	public Location getLocation(Location arg0) {
		return sign.getLocation(arg0);
	}

	public byte getRawData() {
		return sign.getRawData();
	}

	public Material getType() {
		return sign.getType();
	}

	public int getTypeId() {
		return sign.getTypeId();
	}

	public World getWorld() {
		return sign.getWorld();
	}

	public int getX() {
		return sign.getX();
	}

	public int getY() {
		return sign.getY();
	}

	public int getZ() {
		return sign.getZ();
	}

	public void setData(MaterialData arg0) {
		sign.setData(arg0);
	}

	public void setRawData(byte arg0) {
		sign.setRawData(arg0);
	}

	public void setType(Material arg0) {
		sign.setType(arg0);
	}

	public boolean setTypeId(int arg0) {
		return sign.setTypeId(arg0);
	}

	public boolean update() {
		return sign.update();
	}

	public boolean update(boolean arg0) {
		return sign.update(arg0);
	}

	public boolean update(boolean arg0, boolean arg1) {
		return sign.update(arg0, arg1);
	}

	public List<MetadataValue> getMetadata(String arg0) {
		return sign.getMetadata(arg0);
	}

	public boolean hasMetadata(String arg0) {
		return sign.hasMetadata(arg0);
	}

	public void removeMetadata(String arg0, Plugin arg1) {
		sign.removeMetadata(arg0, arg1);
	}

	public void setMetadata(String arg0, MetadataValue arg1) {
		sign.setMetadata(arg0, arg1);
	}

	public String getLine(int arg0) throws IndexOutOfBoundsException {
		return sign.getLine(arg0);
	}

	public String[] getLines() {
		return sign.getLines();
	}

	public void setLine(int arg0, String arg1) throws IndexOutOfBoundsException {
		sign.setLine(arg0, arg1);
	}

}
