package com.pandemoneus.newChunkNotifier;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.pandemoneus.newChunkNotifier.commands.NCNCommands;
import com.pandemoneus.newChunkNotifier.config.NCNConfig;
import com.pandemoneus.newChunkNotifier.logger.Log;

/**
 * NewChunkNotifier plugin.
 * 
 * Sends a message to all players with the permissions node
 * 'newchunknotifier.notifyme' or OPs when a new chunk is created.
 * 
 * @author Pandemoneus
 * 
 */
public class NewChunkNotifier extends JavaPlugin {
	/**
	 * Plugin related stuff
	 */
	private final NCNCommands cmdExecutor = new NCNCommands(this);
	private NCNConfig config = new NCNConfig(this);
	private final NCNWorldListener worldListener = new NCNWorldListener(this);
	private PermissionHandler permissionsHandler;
	private boolean permissionsFound = false;

	private static final String VERSION = "1.01";
	private static final String PLUGIN_NAME = "NewChunkNotifier";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisable() {
		Log.info(PLUGIN_NAME + " disabled");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable() {
		Log.info(PLUGIN_NAME + " v" + VERSION + " enabled");

		getCommand("newchunknotifier").setExecutor(cmdExecutor);
		getCommand("ncn").setExecutor(cmdExecutor);
		
		setupPermissions();
		config.loadConfig();
		worldListener.setCooldown(config.getCooldown());
		worldListener.setPrintLogsToConsole(config.getPrintLogsToConsole());

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Priority.Lowest,
				this);
	}

	/**
	 * Returns the version of the plugin.
	 * 
	 * @return the version of the plugin
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * Returns the name of the plugin.
	 * 
	 * @return the name of the plugin
	 */
	public static String getPluginName() {
		return PLUGIN_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getPluginName();
	}

	/**
	 * Returns whether the permissions plugin could be found.
	 * 
	 * @return true if permissions plugin could be found, otherwise false
	 */
	public boolean getPermissionsFound() {
		return permissionsFound;
	}

	/**
	 * Returns the permissionsHandler of this plugin if it exists.
	 * 
	 * @return the permissionsHandler of this plugin if it exists, otherwise
	 *         null
	 */
	public PermissionHandler getPermissionsHandler() {
		PermissionHandler ph = null;

		if (getPermissionsFound()) {
			ph = permissionsHandler;
		}

		return ph;
	}

	private void setupPermissions() {
		if (permissionsHandler != null) {
			return;
		}

		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin(
				"Permissions");

		if (permissionsPlugin == null) {
			Log.warning("Permissions not detected, using normal command structure.");
			return;
		}

		permissionsFound = true;
		permissionsHandler = ((Permissions) permissionsPlugin).getHandler();
	}
	
	/**
	 * Method that handles what gets reloaded
	 * 
	 * @return true if everything loaded properly, otherwise false
	 */
	public boolean reload() {
		boolean success = config.loadConfig();
		worldListener.setCooldown(config.getCooldown());
		worldListener.setPrintLogsToConsole(config.getPrintLogsToConsole());
		
		return success;
	}
	
	/**
	 * Returns the plugin's config object.
	 * 
	 * @return the plugin's config object
	 */
	public NCNConfig getConfig() {
		return config;
	}
}
