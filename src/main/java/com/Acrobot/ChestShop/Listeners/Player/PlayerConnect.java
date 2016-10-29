package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.UUIDs.PlayerDTO;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * @author Acrobot
 */
public class PlayerConnect implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onPlayerConnect(final PlayerJoinEvent event) {
		final PlayerDTO playerDTO = new PlayerDTO(event.getPlayer());

		Bukkit.getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
			@Override
			public void run() {
				String playerName = NameUtil.stripUsername(playerDTO.getName());
				UUID uuid = NameManager.getUUID(playerName);

				if (uuid != null && !playerDTO.getUniqueId().equals(uuid)) {
					Bukkit.getScheduler().runTask(ChestShop.getPlugin(), new Runnable() {
						@Override
						public void run() {
							Bukkit.getPlayer(playerDTO.getUniqueId()).kickPlayer("[ChestShop]" +
									"Unfortunately, this username was already used by " +
									"another player.");

						}
					});
				}

				NameManager.storeUsername(playerDTO);
			}
		});
	}
}
