package unprotesting.com.github.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class Transaction implements Serializable{

    private static final long serialVersionUID = 4371216893978491119L;
    public final Date date;
    public final String player;
    public final String item;
    public final int amount;
    public final String type;
    public final double total_price;

    public Transaction(String player, ItemStack is, String type, double total_price){
        this.date = Date.from(Instant.now());
        this.player = player;
        this.item = is.getType().toString();
        this.amount = is.getAmount();
        this.total_price = total_price;
        this.type = type;
    }

    @Deprecated
    public Transaction(Player player, Enchantment enchantment, String type){
        double price;
        try{
            price = Main.enchMap.get(enchantment.getName()).price;
        }
        catch (NullPointerException ex){
            price = 0.0;
        }
        if (type.equals("Sell")){
            price = price - price*0.01*Config.getSellPriceDifference();
        }
        this.date = Date.from(Instant.now());
        this.player = player.getName();
        this.item = enchantment.getName();
        this.amount = 1;
        this.total_price = price;
        this.type = type;
    }

    public Transaction(Date date, String player, String item, int amount, String type, double total_price){
        this.date = date;
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.type = type;
        this.total_price = total_price;
    }

    @Deprecated
    public String toString() {
        Player player = Bukkit.getPlayer(this.player);
        return ("Date: " + this.date.toGMTString()
        + " | Player: " + Objects.requireNonNull(player).getDisplayName()
        + " | UUID: " + player.getUniqueId()
        + " | Item: " + this.item
        + " | Quantity: " + this.amount
        + " | Total Price Paid: " + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(this.total_price)
        + " | Indiviudal Price: " + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(this.total_price/ this.amount)
        + " | Type: " + this.type
        );
    }

    @Deprecated
    public String toDisplayString() {
        return (ChatColor.YELLOW + "Date: " + ChatColor.GREEN + this.date.toGMTString()
        + ChatColor.YELLOW + " | Item: " + ChatColor.GREEN + this.item
        + ChatColor.YELLOW + " | Quantity: " + ChatColor.GREEN + this.amount
        + ChatColor.YELLOW + " | Total Price Paid: " + ChatColor.GREEN + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(this.total_price)
        + ChatColor.YELLOW + " | Indiviudal Price: " + ChatColor.GREEN + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(this.total_price/ this.amount)
        + ChatColor.YELLOW + " | Type: " + ChatColor.GREEN + this.type
        );
    }

    public void loadIntoMap(){
        if (this.total_price != 0.0){
            int size = Main.getTransactions().size()-1;
            Main.transactions.put(size, this);
        }
    }

    public void loadTransactionArrayIntoMap(Transaction[] arr){
        for (Transaction transaction : arr){
            transaction.loadIntoMap();
        }
    }

}
