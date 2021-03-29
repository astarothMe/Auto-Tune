package unprotesting.com.github.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unprotesting.com.github.Main;
import unprotesting.com.github.util.Config;
import unprotesting.com.github.util.InflationEventHandler;
import unprotesting.com.github.util.PriceCalculationHandler;
import unprotesting.com.github.util.TextHandler;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class AutoTuneCommand implements CommandExecutor {

    public static Logger log = Logger.getLogger("Minecraft");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String at, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            if (player.hasPermission("at.help") || player.isOp()) {
                return sendHelp(player);
            } else {
                player.sendMessage(ChatColor.RED
                        + "This command is for admins only! To configure the plugin please use the config!");
                return false;
            }
        }
        if (command.getName().equalsIgnoreCase("at")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (args[0].equalsIgnoreCase("login")) {
                if (player.hasPermission("at.login") || player.isOp()) {
                    String AutoTunePlayerID = UUID.randomUUID().toString();
                    String LoggedIn = Main.playerDataConfig.getString(uuid + ".autotuneID");
                    if (LoggedIn == null) {
                        player.sendMessage(ChatColor.YELLOW + "No Auto-Tune Account found in Config");
                        Main.playerDataConfig.set(uuid + ".autotuneID", AutoTunePlayerID);
                        Main.saveplayerdata();
                        player.sendMessage(ChatColor.YELLOW + "Creating one for you..");
                        player.sendMessage(
                                ChatColor.YELLOW + "Created Auto-Tune Account with Unique ID: " + AutoTunePlayerID);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Already Logged in!");
                        player.sendMessage(ChatColor.YELLOW + "Your unique ID is "
                                + Main.playerDataConfig.getString(uuid + ".autotuneID"));
                    }
                    return true;
                } else if (!(player.hasPermission("at.login")) && !(player.isOp())) {
                    TextHandler.noPermssion(player);
                    return true;
                }

                else {
                    if (player.hasPermission("at.help") || player.isOp()) {
                        return sendHelp(player);
                    } else if (!(player.hasPermission("at.help")) && !(player.isOp())) {
                        TextHandler.noPermssion(player);
                        return true;
                    }
                }

            }
            if (args[0].equalsIgnoreCase("increase")) {
                if (player.hasPermission("at.increase") || player.isOp()) {
                    double value = Double.parseDouble(args[2]);
                    if (!(value == 0 || value == 0.0 || value == 0f || value > 99999999 || args[1] == null
                            || args[2] == null)) {
                        if (args[1].contains("%")) {
                            String item = args[1].replace("%", "").toUpperCase();
                            Double newPrice = InflationEventHandler.increaseItemPrice(item, value,
                                    true);
                            player.sendMessage(ChatColor.YELLOW + "Increased " + item + " price to "
                                    + ChatColor.GREEN + Config.getCurrencySymbol() + newPrice);
                        } else {
                            Double newPrice = InflationEventHandler.increaseItemPrice(args[1].toUpperCase(), value,
                                    true);
                            player.sendMessage(ChatColor.YELLOW + "Increased " + args[1].toUpperCase() + " price to "
                                    + ChatColor.GREEN + Config.getCurrencySymbol() + newPrice);
                        }
                        return true;
                    }
                } else if (!(player.hasPermission("at.increase")) && !(player.isOp())) {
                    TextHandler.noPermssion(player);
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("decrease")) {
                if (player.hasPermission("at.decrease") || player.isOp()) {
                    double value = Double.parseDouble(args[2]);
                    if (!(value == 0 || value == 0.0 || value == 0f || value > 99999999 || args[1] == null
                            || args[2] == null)) {
                        if (args[1].contains("%")) {
                            String item = args[1].replace("%", "").toUpperCase();
                            Double newPrice = InflationEventHandler.decreaseItemPrice(item, value,
                                    true);
                            player.sendMessage(ChatColor.YELLOW + "Decreased " + item + " price to "
                                    + ChatColor.GREEN + Config.getCurrencySymbol() + newPrice);
                        } else {
                            Double newPrice = InflationEventHandler.decreaseItemPrice(args[1].toUpperCase(), value,
                                    true);
                            player.sendMessage(ChatColor.YELLOW + "Decreased " + args[1].toUpperCase() + " price to "
                                    + ChatColor.GREEN + Config.getCurrencySymbol() + newPrice);
                        }
                        return true;
                    }
                } else if (!(player.hasPermission("at.decrease")) && !(player.isOp())) {
                    TextHandler.noPermssion(player);
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (player.hasPermission("at.remove") || player.isOp()) {
                    if (args.length == 2) {
                        String item;
                        try {
                            item = args[1];
                        } catch (ClassCastException ex) {
                            player.sendMessage(ChatColor.RED + "Unknown item format: " + args[1]);
                            return false;
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            return false;
                        }
                        if (Main.map.containsKey(item)) {
                            Main.map.remove(item);
                            player.sendMessage(ChatColor.GREEN + "Removed item: " + item);
                        } else {
                            player.sendMessage(ChatColor.RED + item + " unavailable in data.db map!");
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else if (!(player.hasPermission("at.remove")) && !(player.isOp())) {
                    TextHandler.noPermssion(player);
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (player.hasPermission("at.update") || player.isOp()) {
                    try {
                        PriceCalculationHandler.loadItemPricesAndCalculate();
                        PriceCalculationHandler.loadEnchantmentPricesAndCalculate();
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "Error on price update! Check the console for more info");
                    }
                }
                else if (!(player.hasPermission("at.update")) && !(player.isOp())){
                    TextHandler.noPermssion(player);
                    return true;
                }
            }
            else{
                if (player.hasPermission("at.help") || player.isOp()){
                    player.sendMessage(ChatColor.YELLOW + "\"/at\" command usage:");
                    player.sendMessage(ChatColor.YELLOW + "- /at Login | Login to trading plaform");
                    player.sendMessage(ChatColor.YELLOW + "- /at Register | Register to trading plaform");
                    player.sendMessage(ChatColor.YELLOW + "- /at Increase <Item-Name> <Value | %Value> | Increase Item Price");
                    player.sendMessage(ChatColor.YELLOW + "- /at Decrease <Item-Name> <Value | %Value> | Decrease Item Price");
                    player.sendMessage(ChatColor.YELLOW + "- /at Remove <Item-Name> | Remove an item from the data.db file");
                    player.sendMessage(ChatColor.YELLOW + "- /at Update | Update Item Prices");
                    return true;
                }
                else if (!(player.hasPermission("at.help")) && !(player.isOp())){
                    TextHandler.noPermssion(player);
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean sendHelp(Player player) {
        player.sendMessage(ChatColor.YELLOW + "\"/at\" command usage:");
        player.sendMessage(ChatColor.YELLOW + "- /at login | Login to trading plaform");
        player.sendMessage(ChatColor.YELLOW + "- /at Register | Register to trading plaform");
        player.sendMessage(
                ChatColor.YELLOW + "- /at Increase <Item-Name> <Value | %Value> | Increase Item Price");
        player.sendMessage(
                ChatColor.YELLOW + "- /at Decrease <Item-Name> <Value | %Value> | Decrease Item Price");
        return true;
    }
}

 


