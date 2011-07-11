package com.pandemoneus.newChunkNotifier.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import com.pandemoneus.newChunkNotifier.NewChunkNotifier;
import com.pandemoneus.newChunkNotifier.logger.Log;

/**
 * The configuration file for the NewChunkNotifier plugin, uses YML.
 * 
 * @author Pandemoneus
 * 
 */
public final class NCNConfig {

	private NewChunkNotifier plugin;
	private static String pluginName;
	private static String pluginVersion;

	/**
	 * File handling
	 */
	private static String directory = "plugins" + File.separator
			+ NewChunkNotifier.getPluginName() + File.separator;
	private File configFile = new File(directory + File.separator
			+ "config.yml");
	private Configuration bukkitConfig = new Configuration(configFile);

	/**
	 * Default settings
	 */
	private long cooldown = 3000;
	private boolean printLogsToConsole = true;

	/**
	 * Associates this object with a plugin
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public NCNConfig(NewChunkNotifier plugin) {
		this.plugin = plugin;
		pluginName = NewChunkNotifier.getPluginName();
		pluginVersion = NewChunkNotifier.getVersion();
	}

	/**
	 * Loads the configuration used by this plugin.
	 * 
	 * @return true if config loaded without errors
	 */
	public boolean loadConfig() {
		boolean isErrorFree = true;

		new File(directory).mkdir();

		if (configFile.exists()) {
			if (bukkitConfig.getString("Version", "").equals(
					NewChunkNotifier.getVersion())) {
				// config file exists and is up to date
				Log.info(pluginName + " config file found, loading config...");
				loadData();
			} else {
				// config file exists but is outdated
				Log.info(pluginName
						+ " config file outdated, adding old data and creating new values. "
						+ "Make sure you change those!");
				loadData();
				writeDefault();
			}
		} else {
			// config file does not exist
			try {
				Log.info(pluginName
						+ " config file not found, creating new config file...");
				configFile.createNewFile();
				writeDefault();
			} catch (IOException ioe) {
				Log.severe("Could not create the config file for " + pluginName + "!");
				ioe.printStackTrace();
				isErrorFree = false;
			}
		}

		return isErrorFree;
	}

	private void loadData() {
		cooldown = readLong("Notify.Cooldown", "3000");
		printLogsToConsole = bukkitConfig.getBoolean("Notify.PrintToConsole", true);
	}

	private void writeDefault() {
		bukkitConfig
				.setHeader("### Learn more about how this config can be edited and changed to your preference on the forum page. ###");
		write("Version", pluginVersion);
		write("Notify.Cooldown", "" + cooldown);
		write("Notify.PrintToConsole", printLogsToConsole);

		loadData();
	}

	/**
	 * Reads a string representing a long from the config file.
	 * 
	 * Returns '0' when an exception occurs.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            default value
	 * @return the long specified in 'key' from the config file, '0' on errors
	 */
	private long readLong(String key, String def) {
		bukkitConfig.load();

		// Bukkit Config has no getLong(..)-method, so we are using Strings
		String value = bukkitConfig.getString(key, def);

		long tmp = 0;

		try {
			tmp = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			Log.warning("Error parsing a long from the config file. Key=" + key);
			nfe.printStackTrace();
		}

		return tmp;
	}

	private void write(String key, Object o) {
		bukkitConfig.load();
		bukkitConfig.setProperty(key, o);
		bukkitConfig.save();
	}

	/**
	 * Returns the cooldown time between messages.
	 * 
	 * @return the cooldown time between messages
	 */
	public long getCooldown() {
		return cooldown;
	}
	
	/**
	 * Determines whether messages should be print to the server console.
	 * 
	 * @returns true if messages should be printed, otherwise not
	 */
	public boolean getPrintLogsToConsole() {
		return printLogsToConsole;
	}

	/**
	 * Returns a list containing all loaded keys.
	 * 
	 * @return a list containing all loaded keys
	 */
	public String[] printLoadedConfig() {
		bukkitConfig.load();

		String[] tmp = bukkitConfig.getAll().toString().split(",");
		int n = tmp.length;

		tmp[0] = tmp[0].substring(1);
		tmp[n - 1] = tmp[n - 1].substring(0, tmp[n - 1].length() - 1);

		for (String s : tmp) {
			s = s.trim();
		}

		return tmp;
	}

	/**
	 * Returns the config file.
	 * 
	 * @return the config file
	 */
	public File getConfigFile() {
		return configFile;
	}

	/**
	 * Returns the associated plugin.
	 * 
	 * @return the associated plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}
}
