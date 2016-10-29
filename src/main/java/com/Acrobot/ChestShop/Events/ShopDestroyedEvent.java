package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a state after shop destruction
 *
 * @author Acrobot
 */
public class ShopDestroyedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Player destroyer;

	private final Sign sign;
	private final Chest chest;

	public ShopDestroyedEvent(Player destroyer, Sign sign, Chest chest) {
		this.destroyer = destroyer;
		this.sign = sign;
		this.chest = chest;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return Shop's destroyer
	 */
	public Player getDestroyer() {
		return destroyer;
	}

	/**
	 * @return Shop's chest
	 */
	public Chest getChest() {
		return chest;
	}

	/**
	 * @return Shop's sign
	 */
	public Sign getSign() {
		return sign;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
