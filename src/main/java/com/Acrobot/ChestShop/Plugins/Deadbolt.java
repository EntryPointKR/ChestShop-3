package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class Deadbolt implements Listener {
	@EventHandler
	public static void onProtectionCheck(ProtectionCheckEvent event) {
		if (event.getResult() == Event.Result.DENY) {
			return;
		}

		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!com.daemitus.deadbolt.Deadbolt.isProtected(block)) {
			return;
		}

		if (!com.daemitus.deadbolt.Deadbolt.isAuthorized(player, block)) {
			event.setResult(Event.Result.DENY);
		}
	}
}
