package unprotesting.com.github.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import unprotesting.com.github.Main;
import unprotesting.com.github.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AutoTuneBuyCommand implements CommandExecutor {

    public static final List<String> shopTypes = new ArrayList<>();

    @Deprecated
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("buy")){
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (!(player.hasPermission("at.buy") || player.isOp())){
                    TextHandler.noPermssion(player);
                    return true;
                }
                if (args.length == 0){
                    player.sendMessage(ChatColor.YELLOW + "Command Usage: ");
                    player.sendMessage(ChatColor.YELLOW + "/buy: <shop-type> <shop>");
                    for (String str : shopTypes){
                        player.sendMessage(ChatColor.YELLOW + "Available shop: '" + str + "'");
                        return true;
                    }
                }
                if (args.length == 1){
                    if (args[0].contains("enchantments")){
                        for (String str : Main.enchMap.keySet()){
                            EnchantmentSetting setting;
                            try{
                            setting = Main.enchMap.get(str);
                            }
                            catch (ClassCastException | NullPointerException ex){
                                continue;
                            }
                            player.sendMessage(ChatColor.WHITE + "Enchantment: " + ChatColor.AQUA + str + ChatColor.YELLOW +
                            " | Price : "+ Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(setting.price) + " | Item Multiplier: " + setting.ratio + "x");
                        }
                        return true;
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Shop " + args[0] + " not found!");
                        return false;
                    }
                }
                if (args.length == 2){
                    if (args[0].contains("enchantments")){
                        EnchantmentSetting setting = Main.enchMap.get((args[1].toUpperCase()));
                        ItemStack is = player.getInventory().getItemInMainHand();
                        if (Main.getEconomy().getBalance(player) < setting.price){
                            player.sendMessage(ChatColor.RED + "Cannot enchant item: " + is.getType().toString() + " with enchantment " + setting.name);
                            return true;
                        }
                        boolean enchantExists = false;
                        Map<Enchantment, Integer> map = is.getEnchantments();
                        Enchantment ench = Enchantment.getByName(setting.name);
                        if (EnchantmentAlgorithm.checkForEnchantConflicts(is, ench)){
                            player.sendMessage(ChatColor.RED + "Cannot enchant item: " + is.getType().toString() + " with enchantment " + setting.name);
                            return true;
                        }
                        if (map.get(ench) != null){
                            enchantExists = true;
                        }
                        double price;
                        try{
                            price = setting.price + AutoTuneGUIShopUserCommand.getItemPrice(is.getType().toString(), false)*setting.ratio;
                        }
                        catch(NullPointerException e){
                            price = setting.price;
                        }
                        if (Main.getEconomy().getBalance(player) < price){
                            player.sendMessage(ChatColor.RED + "Cannot enchant item: " + is.getType().toString() + " with enchantment " + setting.name);
                            return true;
                        }
                        try{
                            if (!enchantExists){
                                is.addEnchantment(Objects.requireNonNull(ench), 1);
                                Transaction transaction = new Transaction(player, ench, "Buy");
                                transaction.loadIntoMap();
                            }
                            else{
                                int level = is.getEnchantmentLevel(Objects.requireNonNull(ench));
                                is.addEnchantment(ench, level+1);
                                Transaction transaction = new Transaction(player, ench, "Buy");
                                transaction.loadIntoMap();
                            }
                        }
                        catch(IllegalArgumentException ex){
                            player.sendMessage(ChatColor.RED + "Cannot enchant item: " + is.getType().toString() + " with enchantment " + setting.name);
                            ex.printStackTrace();
                            return true;
                        }
                        Main.getEconomy().withdrawPlayer(player, Double.parseDouble(AutoTuneGUIShopUserCommand.df1.format(price)));
                        player.sendMessage(ChatColor.GOLD + "Purchased " + setting.name + " for "
                         + ChatColor.GREEN + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(price));
                        ConcurrentHashMap<Integer, Double[]> buySellMap = setting.buySellData;
                        Double[] arr = buySellMap.get(buySellMap.size()-1);
                        if (arr == null){
                            arr = new Double[]{setting.price, 0.0, 0.0};
                        }
                        if (arr[1] == null){
                            arr[1] = 0.0;
                        }
                        if (arr[2] == null){
                            arr[2] = 0.0;
                        }
                        arr[1] = arr[1]+1;
                        buySellMap.put(buySellMap.size()-1, arr);
                        setting.buySellData = buySellMap;
                        Main.enchMap.put(setting.name, setting);
                        return true;
                    }
                    else{
                        player.sendMessage("Shop " + args[0] + "not found!");
                        return false;
                    }
                }
                else{
                    player.sendMessage(ChatColor.YELLOW + "Command Usage: ");
                    player.sendMessage(ChatColor.YELLOW + "/buy: <shop-type> <shop>");
                    for (String str : shopTypes){
                        player.sendMessage(ChatColor.YELLOW + "Available shop: '" + str + "'");
                        return true;
                    }
                    return false;
                }
            }
            else{
                Main.sendMessage(sender, "&cPlayers only.");
                return true;
            }
        }
        return false;
    }
    
}
