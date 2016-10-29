package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.*;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

/**
 * Represents a Vault connector
 *
 * @author Acrobot
 */
public class VaultListener implements Listener {
	private final Economy provider;

	private VaultListener(Economy provider) {
		this.provider = provider;
	}

	/**
	 * Creates a new VaultListener and returns it (if possible)
	 *
	 * @return VaultListener
	 */
	public static VaultListener initializeVault() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return null;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

		if (rsp == null) {
			return null;
		}

		Economy provider = rsp.getProvider();

		if (provider == null) {
			return null;
		} else {
			return new VaultListener(provider);
		}
	}

	@EventHandler
	public static void onCurrencyTransfer(CurrencyTransferEvent event) {
		if (event.hasBeenTransferred()) {
			return;
		}

		CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(event.getAmount(), event.getSender(), event.getWorld());
		ChestShop.callEvent(currencySubtractEvent);

		if (!currencySubtractEvent.isSubtracted()) {
			return;
		}

		CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(currencySubtractEvent.getAmount(), event.getReceiver(), event.getWorld());
		ChestShop.callEvent(currencyAddEvent);
	}

	public boolean transactionCanFail() {
		return provider.getName().equals("Gringotts") || provider.getName().equals("GoldIsMoney") || provider.getName().equals("MultiCurrency");
	}

	@EventHandler
	public void onAmountCheck(CurrencyAmountEvent event) {
		if (!event.getAmount().equals(BigDecimal.ZERO)) {
			return;
		}

		double balance = provider.getBalance(NameManager.getLastSeenName(event.getAccount()), event.getWorld().getName());

		if (balance > Double.MAX_VALUE) {
			balance = Double.MAX_VALUE;
		}

		event.setAmount(balance);
	}

	@EventHandler
	public void onCurrencyCheck(CurrencyCheckEvent event) {
		if (event.hasEnough()) {
			return;
		}

		World world = event.getWorld();

		if (provider.has(NameManager.getLastSeenName(event.getAccount()), world.getName(), event.getDoubleAmount())) {
			event.hasEnough(true);
		}
	}

	@EventHandler
	public void onAccountCheck(AccountCheckEvent event) {
		if (event.hasAccount()) {
			return;
		}

		World world = event.getWorld();

		if (!provider.hasAccount(NameManager.getLastSeenName(event.getAccount()), world.getName())) {
			event.hasAccount(false);
		}
	}

	@EventHandler
	public void onCurrencyFormat(CurrencyFormatEvent event) {
		if (!event.getFormattedAmount().isEmpty()) {
			return;
		}

		String formatted = provider.format(event.getDoubleAmount());

		event.setFormattedAmount(formatted);
	}

	@EventHandler
	public void onCurrencyAdd(CurrencyAddEvent event) {
		if (event.isAdded()) {
			return;
		}

		World world = event.getWorld();

		provider.depositPlayer(NameManager.getLastSeenName(event.getTarget()), world.getName(), event.getDoubleAmount());
	}

	@EventHandler
	public void onCurrencySubtraction(CurrencySubtractEvent event) {
		if (event.isSubtracted()) {
			return;
		}

		World world = event.getWorld();

		provider.withdrawPlayer(NameManager.getLastSeenName(event.getTarget()), world.getName(), event.getDoubleAmount());
	}

	@EventHandler
	public void onCurrencyHoldCheck(CurrencyHoldEvent event) {
		if (event.getAccount() == null || !transactionCanFail()) {
			return;
		}

		if (!provider.hasAccount(NameManager.getLastSeenName(event.getAccount()), event.getWorld().getName())) {
			event.canHold(false);
			return;
		}

		EconomyResponse response = provider.depositPlayer(NameManager.getLastSeenName(event.getAccount()), event.getWorld().getName(), event.getDoubleAmount());

		if (!response.transactionSuccess()) {
			event.canHold(false);
			return;
		}

		provider.withdrawPlayer(NameManager.getLastSeenName(event.getAccount()), event.getWorld().getName(), event.getDoubleAmount());
	}
}
