package unprotesting.com.github.commands;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import unprotesting.com.github.Main;
import unprotesting.com.github.util.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoTuneGUIShopUserCommand implements CommandExecutor {

	public static final DecimalFormat df1 = new DecimalFormat("###########0.00");
	public static final DecimalFormat df2 = new DecimalFormat("###,###,###,##0.00");
	public static DecimalFormat df3 = new DecimalFormat("###,###,###,##0.00000");
	public static DecimalFormat df4 = new DecimalFormat("###########0.0000");
	public static final DecimalFormat df5 = new DecimalFormat("###########0");
	public static final DecimalFormat df6 = new DecimalFormat("###,###,###,##0.00######");

	public static Integer SBPanePos = 1;

	public static final Integer[] amounts = { 1, 2, 4, 8, 16, 32, 64 };

	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String shop, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("shop")) {
			if (!(sender instanceof Player)) {
				Main.sendMessage(sender, "&cPlayers only.");
				return true;
			}
			Player p = (Player) sender;
			if (Config.getMenuRows() == 6) {
				SBPanePos = 2;
			}
			if (args.length == 0) {
				if (p.hasPermission("at.shop") || p.isOp()) {
					loadGUISECTIONS(p, false);
				} else if (!(p.hasPermission("at.shop")) && !(p.isOp())) {
					TextHandler.noPermssion(p);
				}
				return true;
			}
			if (args.length == 1) {
				if (p.hasPermission("at.shop") || p.isOp()) {
				} else if (!(p.hasPermission("at.shop")) && !(p.isOp())) {
					TextHandler.noPermssion(p);
					return true;
				}
				String inputSection;
				try {
					inputSection = args[0];
				} catch (ClassCastException ex) {
					p.sendMessage("Unknown shop format: " + args[0]);
					return false;
				} catch (ArrayIndexOutOfBoundsException ex) {
					return false;
				}
				for (int i = 0; i < Main.sectionedItems.length; i++) {
					if (Main.sectionedItems[i].name.toLowerCase().equals(inputSection)) {
						loadGUIMAIN(p, Main.sectionedItems[i], true, false);
						return true;
					}
				}
			} else {
				return false;
			}
		}
		return false;
	}

	public static void loadGUISECTIONS(Player player, boolean autosell) {
		int lines = (int) Math.floor(((Main.sectionedItems.length) / 7) + 1);
		if (lines > 4) {
			lines = 4;
		}
		Gui front = new Gui((lines + 2), Config.getMenuTitle());
		OutlinePane pane = new OutlinePane(1, 1, 7, lines);
		for (int i = 0; i < Main.sectionedItems.length; i++) {
			ItemStack is;
			try{
				is = new ItemStack((Main.sectionedItems[i].image));
			}
			catch(IllegalArgumentException ex){
				Main.log("Section: " + Main.sectionedItems[i].name + " has a null or invalid material-name");
				continue;
			}
			ItemMeta im = is.getItemMeta();
			Objects.requireNonNull(im).setDisplayName(ChatColor.GOLD + Main.sectionedItems[i].name);
			if (!autosell){
				im.setLore(Collections.singletonList(
						ChatColor.WHITE + "Click to enter the " + (Main.sectionedItems[i].name.toLowerCase()) + " shop!"));
			}
			if (autosell){
				im.setLore(Collections.singletonList(
						ChatColor.WHITE + "Click to enter the " + (Main.sectionedItems[i].name.toLowerCase()) + " auto-selling configuration!"));
			}
			is.setItemMeta(im);
			final Section inputSection = Main.sectionedItems[i];
			GuiItem gItem = new GuiItem(is, event -> {
				if (event.getClick() == ClickType.LEFT) {
					event.setCancelled(true);
					player.getOpenInventory().close();
					loadGUIMAIN(player, inputSection, false, autosell);
				} else if (event.getClick() != ClickType.LEFT) {
					event.setCancelled(true);
					player.setItemOnCursor(null);
				}
			});
			pane.addItem(gItem);
			front.addPane(pane);
		}
		front.update();
		front.show(player);
	}

	public static void loadGUIMAIN(Player player, Section sec, boolean twoArgs, boolean autosell) {
		int itemAmount = sec.items.size();
		int lines = (int) Math.floor(((itemAmount - 1) / 7) + 1);
		int itemNo = 0;
		if (lines > 4) {
			lines = 4;
		}
		Gui main = new Gui((lines + 2), Config.getMenuTitle());
		int paneSize = (lines + 1) * 7;
		int paneAmount = (int) Math.ceil((itemAmount+7) / paneSize) + 1;
		PaginatedPane pPane = new PaginatedPane(0, 0, 9, (lines + 2));
		OutlinePane[] shopPanes = new OutlinePane[paneAmount];
		main.addPane(pPane);
		StaticPane[] panes = loadPagePanes(pPane, (lines + 1), main);
		panes[0].setVisible(false);
		panes[1].setVisible(true);
		if (pPane.getPages() == 1) {
			panes[1].setVisible(false);
		}
		main.addPane(panes[0]);
		main.addPane(panes[1]);
		if (!twoArgs || sec.showBackButton) {
			main.addPane(loadMainMenuBackPane(pPane, autosell));
		}
		main.update();
		main.show(player);
	}

	public static void loadGUITRADING(Player player, String itemName, Section sec, boolean autosell) {
		Gui main = new Gui(4, Config.getMenuTitle());
		OutlinePane front = new OutlinePane(1, 1, 7, 2);
		if (!autosell) {
			loadTradingItems(itemName, sec, front);
		}
		main.addPane(front);
		main.addPane(loadReturnButton(sec, autosell));
		main.update();
		main.show(player);
	}

	public static OutlinePane loadTradingItems(String itemName, Section sec, OutlinePane front) {
		for (int i = 0; i < 14; i++) {
			final int finalI = i;
			ItemStack iStack;
			GuiItem gItem;
			if (i < 7) {
				iStack = loadTradingItem(itemName, amounts[i], true, sec);
				int maxStackSize = iStack.getMaxStackSize();
				int maxBuys = sec.itemMaxBuySell.get(itemName)[0];
				if (maxStackSize < amounts[i] || maxBuys < amounts[i]){
					Material mat = Material.RED_STAINED_GLASS_PANE;
					ItemStack itemPane = new ItemStack(mat);
					ItemMeta itemPaneMeta = itemPane.getItemMeta();
					Objects.requireNonNull(itemPaneMeta).setDisplayName(ChatColor.MAGIC + "_");
					itemPane.setItemMeta(itemPaneMeta);
					GuiItem gItemPane = new GuiItem(itemPane, event -> event.setCancelled(true));
					front.addItem(gItemPane);
					continue;
				}
				gItem = new GuiItem(iStack, event -> {
					Player player = (Player) event.getWhoClicked();
					if (event.getClick() == ClickType.LEFT) {
						event.setCancelled(true);
						ConcurrentHashMap<String, Integer> maxBuyMapRec = Main.maxBuyMap.get(player.getUniqueId());
						int currentMax = maxBuyMapRec.get(itemName);
						Integer[] max = sec.itemMaxBuySell.get(itemName);
						Double price = getItemPrice(itemName, false);
						if (max[0] < (currentMax + amounts[finalI]) && !Config.isDisableMaxBuysSells()) {
							player.sendMessage(ChatColor.BOLD + "Cant Purchase " + amounts[finalI]
									+ "x of " + itemName);
							int difference = (currentMax + amounts[finalI]) - max[0];
							if (difference != 0 && !(currentMax >= max[0])) {
								ItemStack is = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)),
										(amounts[finalI] - difference));
								checkForEnchantAndApply(is, sec);
								if (((amounts[finalI] - difference) * price) < Main.getEconomy().getBalance(player)) {
									HashMap<Integer, ItemStack> unableItems = player.getInventory().addItem(is);
									if (unableItems.size() > 0) {
										player.sendMessage(ChatColor.BOLD + "Cant Purchase "
												+ amounts[finalI] + "x of " + itemName);
									} else {
										sendPlayerShopMessageAndUpdateGDP((amounts[finalI] - difference), player,
												itemName, false);
										Main.maxBuyMap.put(player.getUniqueId(), maxBuyMapRec);
									}
									player.sendMessage(ChatColor.RED + "Max Buys Reached! - " + max[0] + "/" + max[0]);
								} else {
									player.sendMessage(ChatColor.BOLD + "Cant Purchase "
											+ amounts[finalI] + "x of " + itemName);
								}
							}
						} else {
							try {
								ItemStack is = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)), amounts[finalI]);
								checkForEnchantAndApply(is, sec);
								if ((price * amounts[finalI]) < Main.getEconomy().getBalance(player)) {
									HashMap<Integer, ItemStack> unableItems = player.getInventory().addItem(is);
									if (unableItems.size() > 0) {
										player.sendMessage(ChatColor.BOLD + "Cant Purchase "
												+ amounts[finalI] + "x of " + itemName);
									} else {
										sendPlayerShopMessageAndUpdateGDP(amounts[finalI], player, itemName, false);
									}
								} else {
									player.sendMessage(ChatColor.BOLD + "Cant Purchase "
											+ amounts[finalI] + "x of " + itemName);
								}
							} catch (IllegalArgumentException ignored) {
							}
						}
					} else {
						event.setCancelled(true);
					}
				});
			} else {
				iStack = loadTradingItem(itemName, amounts[i - 7], false, sec);
				int maxStackSize = iStack.getMaxStackSize();
				int maxSells = sec.itemMaxBuySell.get(itemName)[1];
				if (maxStackSize < amounts[i - 7] || maxSells < amounts[i - 7]){
					Material mat = Material.RED_STAINED_GLASS_PANE;
					ItemStack itemPane = new ItemStack(mat);
					ItemMeta itemPaneMeta = itemPane.getItemMeta();
					Objects.requireNonNull(itemPaneMeta).setDisplayName(ChatColor.MAGIC + "_");
					itemPane.setItemMeta(itemPaneMeta);
					GuiItem gItemPane = new GuiItem(itemPane, event -> event.setCancelled(true));
					front.addItem(gItemPane);
					continue;
				}
				gItem = new GuiItem(iStack, event -> {
					Player player = (Player) event.getWhoClicked();
					if (event.getClick() == ClickType.LEFT) {
						event.setCancelled(true);
						ConcurrentHashMap<String, Integer> maxSellMapRec = Main.maxSellMap.get(player.getUniqueId());
						int currentMax = maxSellMapRec.get(itemName);
						Integer[] max = sec.itemMaxBuySell.get(itemName);
						ItemStack test = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)));
						checkForEnchantAndApply(test, sec);
						EnchantmentAlgorithm.updateEnchantSellData(test, player);
						if (!player.getInventory().containsAtLeast(test, amounts[finalI - 7])) {
							player.sendMessage(ChatColor.BOLD + "Cant Sell " + amounts[finalI - 7]
									+ "x of " + itemName);
						}
						else if (max[1] < (currentMax + amounts[finalI - 7]) && !Config.isDisableMaxBuysSells()) {
							player.sendMessage(ChatColor.BOLD + "Cant Sell " + amounts[finalI - 7]
									+ "x of " + itemName);
							int difference = (currentMax + amounts[finalI - 7]) - max[1];
							if (difference != 0 && !(currentMax >= max[1])) {
								removeItems(player, (finalI - 7), itemName, sec, difference);
								Main.maxSellMap.put(player.getUniqueId(), maxSellMapRec);
							}
							player.sendMessage(ChatColor.RED + "Max Sells Reached! - " + max[1] + "/" + max[1]);
						} 
						else {
							removeItems(player, (finalI - 7), itemName, sec, 0);
						}
					} else {
						event.setCancelled(true);
					}
				});
			}
			front.addItem(gItem);
		}
		return front;
	}

	public static void removeItems(Player player, int finalI, String itemName, Section sec, int difference) {
		try {
			ItemStack iStack = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)), (amounts[finalI]) - difference);
			checkForEnchantAndApply(iStack, sec);
			HashMap<Integer, ItemStack> takenItems = player.getInventory().removeItem(iStack);
			if (takenItems.size() > 0) {
				player.sendMessage(ChatColor.BOLD + "Cant sell " + (amounts[finalI] - difference)
						+ "x of " + itemName);
			} else {
				sendPlayerShopMessageAndUpdateGDP((amounts[finalI] - difference), player, itemName, true);
			}
		} catch (IllegalArgumentException ignored) {
		}
	}

	public static ItemStack loadShopItem(String itemName, Section sec, Player player, boolean autosell) {
		ItemStack iStack = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)));
		ItemMeta iMeta = iStack.getItemMeta();
		Integer[] maxBuySellForItem = sec.itemMaxBuySell.get(itemName);
		if (!autosell) {
			Objects.requireNonNull(iMeta).setDisplayName(ChatColor.GOLD + itemName);
			if (!Config.isDisableMaxBuysSells()){
				iMeta.setLore(Arrays.asList((ChatColor.GRAY + "Click to purchase/sell"), (loadPriceDisplay(itemName)),
					(ChatColor.WHITE + "Max Buys: " + maxBuySellForItem[0] + " per " + Config.getTimePeriod() + "min"),
					(ChatColor.WHITE + "Max Sells: " + maxBuySellForItem[1] + " per " + Config.getTimePeriod()
							+ "min")));
			}
			else{
				iMeta.setLore(Arrays.asList((ChatColor.GRAY + "Click to purchase/sell"), (loadPriceDisplay(itemName))));
			}
		}
		if (autosell) {
			boolean atonoff = Main.playerDataConfig.getBoolean(player.getUniqueId() + ".AutoSell" + "." + itemName);
			if (!atonoff) {
				Objects.requireNonNull(iMeta).setDisplayName(ChatColor.AQUA + itemName + ChatColor.RED + " - Auto Sell Disabled");
				iMeta.setLore(
						Arrays.asList((ChatColor.GRAY + "Click to turn on auto-sell"), (loadPriceDisplay(itemName))));
			} else {
				Objects.requireNonNull(iMeta).setDisplayName(ChatColor.AQUA + itemName + ChatColor.GREEN + " - Auto Sell Enabled");
				iMeta.setLore(
						Arrays.asList((ChatColor.GRAY + "Click to turn off auto-sell"), (loadPriceDisplay(itemName))));
			}
		}
		iStack.setItemMeta(iMeta);
		checkForEnchantAndApply(iStack, sec);
		return iStack;
	}

	public static String loadPriceDisplay(String item) {
		double currentPrice = getItemPrice(item, false);
		float timePeriod = (float) Config.getTimePeriod();
		float timePeriodsInADay = 1 / (timePeriod / 1440);
		List<Double> newMap;
		try{
		newMap = Main.getItemPrices().get(item).prices;
		}
		catch(NullPointerException ex){
			return (ChatColor.WHITE + Config.getCurrencySymbol() + df2.format(currentPrice) + ChatColor.DARK_GRAY
					+ " - " + ChatColor.GRAY + "%0.0");
		}
		if (newMap.size() <= timePeriodsInADay) {
			return (ChatColor.WHITE + Config.getCurrencySymbol() + df2.format(currentPrice) + ChatColor.DARK_GRAY
					+ " - " + ChatColor.GRAY + "%0.0");
		}
		int oneDayOldTP = (int) Math.floor(newMap.size() - timePeriodsInADay);
		double oneDayOldPrice = newMap.get(oneDayOldTP);
		if (oneDayOldPrice > currentPrice) {
			double percent = 100 * ((currentPrice / oneDayOldPrice) - 1);
			return (ChatColor.WHITE + Config.getCurrencySymbol() + df2.format(currentPrice) + ChatColor.DARK_GRAY
					+ " - " + ChatColor.RED + "%-" + df2.format(Math.abs(percent)));
		} else if (oneDayOldPrice < currentPrice) {
			double percent = 100 * (1 - (oneDayOldPrice / currentPrice));
			return (ChatColor.WHITE + Config.getCurrencySymbol() + df2.format(currentPrice) + ChatColor.DARK_GRAY
					+ " - " + ChatColor.GREEN + "%+" + df2.format(Math.abs(percent)));
		} else {
			return (ChatColor.WHITE + Config.getCurrencySymbol() + df2.format(currentPrice) + ChatColor.DARK_GRAY
					+ " - " + ChatColor.GRAY + "%0.0");
		}
	}

	public static ItemStack loadTradingItem(String itemName, int number, boolean buy, Section sec) {
		ItemStack iStack = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemName)), number);
		ItemMeta iMeta = iStack.getItemMeta();
		Objects.requireNonNull(iMeta).setDisplayName(ChatColor.GOLD + itemName);
		if (buy) {
			iMeta.setLore(Arrays.asList((ChatColor.WHITE + "$" + df2.format(getItemPrice(itemName, false) * number)),
					(ChatColor.GRAY + "Purchase " + number + "x ")));
			iStack.setItemMeta(iMeta);
		}
		if (!buy) {
			iMeta.setLore(Arrays.asList((ChatColor.WHITE + "$" + df2.format(getItemPrice(itemName, true) * number)),
					(ChatColor.GRAY + "Sell " + number + "x ")));
			iStack.setItemMeta(iMeta);
		}
		checkForEnchantAndApply(iStack, sec);
		return iStack;
	}

	@Deprecated
	public static ItemStack checkForEnchantAndApply(ItemStack is, Section sec) {
		for (String enchantedItem : sec.enchantedItems) {
			if (is.getType().toString().contains(enchantedItem)) {
				ConfigurationSection config = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops"))
						.getConfigurationSection((is.getType().toString()));
				String enchantmentName = Objects.requireNonNull(config).getString("enchantment", "none");
				ItemMeta meta = is.getItemMeta();
				Enchantment ench = Enchantment.getByName(enchantmentName);
				int level = config.getInt("enchantment-level", 0);
				if (level == 0) {
					return is;
				} else if (Objects.requireNonNull(enchantmentName).contains("none")) {
					Main.debugLog("Enchantment " + enchantmentName + " is null");
					return is;
				} else if (ench == null) {
					Main.debugLog("Enchantment " + enchantmentName + " is null");
					return is;
				}
				try {
					Objects.requireNonNull(meta).addEnchant(ench, level, true);
					is.setItemMeta(meta);
				} catch (IllegalArgumentException ex) {
					Main.debugLog("IllegalArgumentException at shop " + is.getType().toString() + " enchantment "
							+ enchantmentName + " is illegal");
					ex.printStackTrace();
					return is;
				}
				return is;
			}
		}
		return is;
	}

	public boolean hasAvaliableSlot(Player player) {
		InventoryView invview = player.getOpenInventory();
		Inventory inv = invview.getBottomInventory();
		boolean check = true;
		int a = inv.firstEmpty();
		return a != -1;
	}

	public double getSellPriceDifference(String item) {
		double output = Config.getSellPriceDifference();
		try {
			ConfigurationSection config = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops"))
					.getConfigurationSection((item));
			output = Objects.requireNonNull(config).getDouble("sell-difference", output);
			return output;
		} catch (NullPointerException ex) {
			output = Config.getSellPriceDifference();
		}
		return output;
	}

	public static Double getItemPrice(String item, boolean sell) {
		Double output;
		if (!sell) {
			try{
				output = Main.getItemPrices().get(item).price;
				return output;
			}
			catch(NullPointerException e){
				ConcurrentHashMap<Integer, Double[]> map = Main.map.get(item);
				try{
					output = map.get(map.size()-1)[0];
					return output;
				}
				catch(NullPointerException e2){
					return null;
				}
				
			}
		} else {
			try{
				output = Main.getItemPrices().get(item).sellPrice;
				return output;
			}
			catch(NullPointerException e){
				ConcurrentHashMap<Integer, Double[]> map = Main.map.get(item);
				try{
					output = map.get(map.size()-1)[0];
					output = output - output*0.01*getSellDifference(item);
					return output;
				}
				catch(NullPointerException e2){
					return null;
				}
			}
		}
	}

	@Deprecated
	public static Double getItemPrice(ItemStack item, boolean sell){
		Double output;
		try{
			output = getItemPrice(item.getType().toString(), sell);
		}
		catch (NullPointerException ex){
			return null;
		}
		return output;
	}

	public static StaticPane loadReturnButton(Section sec, boolean autosell) {
		StaticPane output = new StaticPane(0, 0, 1, 1);
		ItemStack iStack = new ItemStack(Material.ARROW);
		ItemMeta iMeta = iStack.getItemMeta();
		Objects.requireNonNull(iMeta).setDisplayName(ChatColor.DARK_PURPLE + sec.name);
		iMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to go to " + ChatColor.WHITE + sec.name));
		iStack.setItemMeta(iMeta);
		GuiItem gItem = new GuiItem(iStack, event -> {
			Player player = (Player) event.getWhoClicked();
			player.getOpenInventory().close();
			loadGUIMAIN(player, sec, false, autosell);
		});
		output.addItem(gItem, 0, 0);
		return output;
	}

	public static StaticPane[] loadPagePanes(PaginatedPane pPane, int lines, Gui main) {
		StaticPane output = new StaticPane(0, (lines), 1, 1);
		ItemStack iStack = new ItemStack(Material.ARROW);
		ItemMeta iMeta = iStack.getItemMeta();
		Objects.requireNonNull(iMeta).setDisplayName(ChatColor.DARK_PURPLE + "Back");
		iMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Page " + ChatColor.WHITE + (pPane.getPage() + 1)));
		iStack.setItemMeta(iMeta);
		StaticPane forward = loadForwardPane(pPane, lines, main, output);
		GuiItem gItem = new GuiItem(iStack, event -> {
			event.setCancelled(true);
			int page = pPane.getPage();
			pPane.setPage(page - 1);
			forward.setVisible(true);
			if (pPane.getPage() == 0) {
				output.setVisible(false);
				forward.setVisible(true);
			}
			main.update();
		});
		output.addItem(gItem, 0, 0);
		return new StaticPane[]{ output, forward };
	}

	public static StaticPane loadForwardPane(PaginatedPane pPane, int lines, Gui main, StaticPane backPane) {
		StaticPane output = new StaticPane(8, (lines), 1, 1);
		ItemStack iStack = new ItemStack(Material.ARROW);
		ItemMeta iMeta = iStack.getItemMeta();
		Objects.requireNonNull(iMeta).setDisplayName(ChatColor.DARK_PURPLE + "Next");
		iMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Page " + ChatColor.WHITE + (pPane.getPage() + 2)));
		iStack.setItemMeta(iMeta);
		GuiItem gItem = new GuiItem(iStack, event -> {
			event.setCancelled(true);
			int page = pPane.getPage();
			int pages = pPane.getPages();
			pPane.setPage(page + 1);
			if (pPane.getPage() > (pages - 2)) {
				output.setVisible(false);
			}
			if (pPane.getPage() > page) {
				backPane.setVisible(true);
			}
			main.update();
		});
		output.addItem(gItem, 0, 0);
		return output;
	}

	public static StaticPane loadMainMenuBackPane(PaginatedPane pPane, boolean autosell) {
		StaticPane output = new StaticPane(0, 0, 1, 1);
		ItemStack iStack = new ItemStack(Material.ARROW);
		ItemMeta iMeta = iStack.getItemMeta();
		Objects.requireNonNull(iMeta).setDisplayName(ChatColor.DARK_PURPLE + "Main Menu");
		iMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to go to " + ChatColor.WHITE + "Main Menu"));
		iStack.setItemMeta(iMeta);
		GuiItem gItem = new GuiItem(iStack, event ->{
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			player.getOpenInventory().close();
			loadGUISECTIONS(player, autosell);
		});
		output.addItem(gItem, 0, 0);
		return output;
	}

	public static void sendPlayerShopMessageAndUpdateGDP(int amount, Player player, String matClickedString, boolean sell){
		ItemStack itemstack = new ItemStack(Objects.requireNonNull(Material.getMaterial(matClickedString)), amount);
		if (!sell){
			ConcurrentHashMap<String, Integer> cMap = Main.maxBuyMap.get(player.getUniqueId());
			cMap.put(matClickedString, (cMap.get(matClickedString)+amount));
			Main.maxBuyMap.put(player.getUniqueId(), cMap);
			ConcurrentHashMap<Integer, Double[]> inputMap = Main.map.get(matClickedString);
			Double[] arr = inputMap.get(inputMap.size()-1);
			Double[] outputArr = {arr[0], (arr[1]+amount), arr[2]};
			Main.tempdatadata.put("GDP", (Main.tempdatadata.get("GDP")+(arr[0]*amount)));
			Transaction transaction = new Transaction(player.getName(), itemstack, "Buy", arr[0]);
			inputMap.put((inputMap.size()-1), outputArr);
			Main.map.put(matClickedString, inputMap);
			Main.getEconomy().withdrawPlayer(player, (arr[0]*amount));
			transaction.loadIntoMap();
			player.sendMessage(ChatColor.GOLD + "Purchased " + amount + "x " + matClickedString + " for " + ChatColor.GREEN + Config.getCurrencySymbol() + df2.format(arr[0]*amount));
			
		}
		else {
			ConcurrentHashMap<String, Integer> cMap = Main.maxSellMap.get(player.getUniqueId());
			cMap.put(matClickedString, (cMap.get(matClickedString)+amount));
			Main.maxSellMap.put(player.getUniqueId(), cMap);
			ConcurrentHashMap<Integer, Double[]> inputMap = Main.map.get(matClickedString);
			Double[] arr = inputMap.get(inputMap.size()-1);
			Double[] outputArr = {arr[0], arr[1], (arr[2]+amount)};
			Double price = AutoTuneGUIShopUserCommand.getItemPrice(matClickedString, true);
			Main.tempdatadata.put("GDP", (Main.tempdatadata.get("GDP")+(price*amount)));
			Transaction transaction = new Transaction(player.getName(), itemstack, "Sell", price);
			inputMap.put((inputMap.size()-1), outputArr);
			Main.map.put(matClickedString, inputMap);
			Main.getEconomy().depositPlayer(player, (price*amount));
			transaction.loadIntoMap();
			player.sendMessage(ChatColor.GOLD + "Sold " + amount + "x " + matClickedString + " for " + ChatColor.GREEN + Config.getCurrencySymbol() + df2.format(price*amount));
		}
	}

	public static double getSellDifference(String item){
		ConfigurationSection config = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).getConfigurationSection((item));
            Double sellpricedif = Config.getSellPriceDifference();
            try{
                sellpricedif = Objects.requireNonNull(config).getDouble("sell-difference", sellpricedif);
            }
            catch(NullPointerException ex){
                sellpricedif = Config.getSellPriceDifference();
			}
		return sellpricedif;
	}
}