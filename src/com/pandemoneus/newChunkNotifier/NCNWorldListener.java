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

	/**
	 * Associates this object with a plugin
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
			List<Player> players = event.getWorld().getPlayers();

			Log.info("New chunk created at " + event.getChunk().getX() + ", "
					+ event.getChunk().getZ());

			if (plugin.getPermissionsFound()) {
				PermissionHandler ph = plugin.getPermissionsHandler();

				for (Player p : players) {
					if (ph.has(p, "newchunknotifier.notifyme")) {
						p.sendMessage(ChatColor.GOLD
								+ "NOTICE: New chunk created at "
								+ event.getChunk().getX() + ", "
								+ event.getChunk().getZ());
					}
				}
			} else {
				for (Player p : players) {
					if (p.isOp()) {
						p.sendMessage(ChatColor.GOLD
								+ "NOTICE: New chunk created at "
								+ event.getChunk().getX() + ", "
								+ event.getChunk().getZ());
					}
				}
			}
		}
	}
}
