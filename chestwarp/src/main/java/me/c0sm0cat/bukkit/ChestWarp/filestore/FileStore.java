package me.c0sm0cat.bukkit.ChestWarp.filestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public enum FileStore {

	INSTANCE;

	private File dataFile;
	private File dataDir;
	private JavaPlugin plugin;
	private boolean isInitialized = false;

	private FileStore() {

	}

	public void init(JavaPlugin plugin) {
		this.plugin = plugin;
		dataFile = new File(plugin.getDataFolder(), "datastore.cfg");
		dataDir = plugin.getDataFolder();

		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}

		isInitialized = true;
	}

	public void saveData(Set<String> data) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(dataFile));

			for (String next : data) {
				out.println(next);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Error saving ChestWarp data", e);
		}
	}

	public Set<String> loadData() throws IOException {
		if (!dataFile.exists()) {
			return new HashSet<String>();
		}
		Set<String> result = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(dataFile));

		String nextLine = in.readLine();
		while (nextLine != null) {
			result.add(nextLine);
			nextLine = in.readLine();
		}

		in.close();

		return result;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

}
