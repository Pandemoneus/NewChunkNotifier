package com.pandemoneus.newChunkNotifier.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.pandemoneus.newChunkNotifier.NewChunkNotifier;
import com.pandemoneus.newChunkNotifier.config.NCNConfig;
import com.pandemoneus.newChunkNotifier.logger.Log;

/**
 * Command class. Available commands are:
 * ncn
 * ncn reload
 * ncn info
 * 
 * @author Pandemoneus
 * 
 */
public final class NCNCommands implements CommandExecutor {

	private NewChunkNotifier plugin;
	private static String pluginName;

	/**
	 * Associates this object with a plugin
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public NCNCommands(NewChunkNotifier plugin) {
		this.plugin = plugin;
		pluginName = NewChunkNotifier.getPluginName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (args != null) {
			if (sender instanceof Player) {
				if (plugin.getPermissionsFound()) {
					usePermissionsStructure((Player) sender, cmd, commandLabel,
							args);
				} else {
					useNormalStructure((Player) sender, cmd, commandLabel, args);
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "Sorry, you are not a player!");
			}
		}

		return true;
	}

	private void usePermissionsStructure(Player sender, Command cmd,
			String commandLabel, String[] args) {
		PermissionHandler ph = plugin.getPermissionsHandler();

		if (args.length == 0) {
			// show help
			if (ph.has(sender, "newchunknotifier.help")) {
				showHelp(sender);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You are not authorized to use this command.");
			}
		} else if (args.length == 1) {
			// commands with 0 arguments
			String command = args[0];

			if (command.equalsIgnoreCase("reload")) {
				// reload
				if (ph.has(sender, "newchunknotifier.config.reload")) {
					reloadPlugin(sender);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You are not authorized to use this command.");
				}
			} else if (command.equalsIgnoreCase("info")) {
				// info
				if (ph.has(sender, "newchunknotifier.config.info")) {
					getConfigInfo(sender);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You are not authorized to use this command.");
				}
			}
		}
	}

	private void useNormalStructure(Player sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender.isOp()) {
			if (args.length == 0) {
				// show help
				showHelp(sender);
			} else if (args.length == 1) {
				// commands with 0 arguments
				String command = args[0];

				if (command.equalsIgnoreCase("reload")) {
					// reload
					reloadPlugin(sender);
				} else if (command.equalsIgnoreCase("info")) {
					// info
					getConfigInfo(sender);
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED
					+ "You are not authorized to use this command.");
		}
	}

	private void showHelp(Player sender) {
		sender.sendMessage(ChatColor.YELLOW + "Available commands:");
		sender.sendMessage(ChatColor.GOLD
				+ "/ncn reload - reloads the plugin's config file");
		sender.sendMessage(ChatColor.GOLD
				+ "/ncn info - shows the currently loaded config");
	}

	private void reloadPlugin(Player sender) {
		Log.info("'" + sender.getName()
				+ "' requested reload of " + pluginName);
		sender.sendMessage(ChatColor.GREEN + "Reloading " + pluginName);

		if (plugin.reload()) {
			sender.sendMessage(ChatColor.GREEN + "Success!");
		}
	}

	private void getConfigInfo(Player sender) {
		NCNConfig config = plugin.getConfig();
		sender.sendMessage(ChatColor.YELLOW
				+ "Currently loaded config of " + pluginName + ":");
		sender.sendMessage(ChatColor.YELLOW
				+ "---------------------------------------------");

		if (config.getConfigFile().exists()) {
			for (String s : config.printLoadedConfig()) {
				sender.sendMessage(ChatColor.YELLOW + s);
			}
		} else {
			sender.sendMessage(ChatColor.RED
					+ "None - Config file deleted - please reload");
		}
	}
}
