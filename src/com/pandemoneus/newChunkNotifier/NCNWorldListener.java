package com.pandemoneus.newChunkNotifier;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import com.nijiko.permissions.PermissionHandler;
import com.pandemoneus.newChunkNotifier.logger.Log;

/**
 * WorldListener that checks when new chunks are created.
 * 
 * @author Pandemoneus
 * 
 */
public final class NCNWorldListener extends WorldListener {
	private NewChunkNotifier plugin;
	private long cooldown = 3000;
	private long before = 0;
	private boolean printLogsToConsole = true;

	/**
	 * Associates this object with a plugin.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public NCNWorldListener(NewChunkNotifier plugin) {
		this.plugin = plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.isNewChunk()) {
			
			if (printLogsToConsole) {
				Log.info("New chunk created at " + event.getChunk().getX() + ", "
						+ event.getChunk().getZ() + " in world '" + event.getWorld().getName() + "'");
			}
			
			long after = System.currentTimeMillis();
			
			if (after - before >= cooldown) {
				List<Player> players = event.getWorld().getPlayers();
	
				if (plugin.getPermissionsFound()) {
					PermissionHandler ph = plugin.getPermissionsHandler();
	
					for (Player p : players) {
						if (ph.has(p, "newchunknotifier.notifyme")) {
							p.sendMessage(ChatColor.GOLD
									+ "NOTICE: New chunk created at "
									+ event.getChunk().getX() + ", "
									+ event.getChunk().getZ() + " in world '" + event.getWorld().getName() + "'");
						}
					}
				} else {
					for (Player p : players) {
						if (p.isOp()) {
							p.sendMessage(ChatColor.GOLD
									+ "NOTICE: New chunk created at "
									+ event.getChunk().getX() + ", "
									+ event.getChunk().getZ() + " in world '" + event.getWorld().getName() + "'");
						}
					}
				}
				
				before = System.currentTimeMillis();
			}
		}
	}
	
	/**
	 * Sets the cooldown time (in milliseconds) between messages.
	 * 
	 * @param cooldown the cooldown time in milliseconds
	 */
	public void setCooldown(long cooldown) {
		long tmp = cooldown;
		
		if (tmp < 0) {
			tmp = 0;
		}
		
		this.cooldown = tmp;
	}
	
	/**
	 * Determines whether messages should be print to the server console.
	 * 
	 * @param print when true, prints messages in the console, otherwise not
	 */
	public void setPrintLogsToConsole(boolean print) {
		printLogsToConsole = print;
	}
}
