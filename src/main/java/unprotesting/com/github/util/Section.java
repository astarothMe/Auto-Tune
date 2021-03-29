package unprotesting.com.github.util;

import org.bukkit.Material;
import unprotesting.com.github.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Section {
    
    public final ArrayList<String> items;
    public final String name;
    public Material image = Material.matchMaterial("GRASS_BLOCK");
    public boolean showBackButton = true;
    public final ConcurrentHashMap<String, Integer[]> itemMaxBuySell = new ConcurrentHashMap<>();
    public final List<String> enchantedItems = new ArrayList<>();

    public Section(String name){
        this.name = name;
        items = new ArrayList<>();
        for (String section : Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("sections")).getKeys(false)){
            if (section.equals(name)){
                try{
                    showBackButton = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("sections." + section)).getBoolean("back-menu-button-enabled");
                }
                catch (NullPointerException ex){
                    showBackButton = true;
                }
                image = Material.matchMaterial(Objects.requireNonNull(Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("sections." + section)).getString("block")));
                for (String shop : Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).getKeys(false)){
                    String shopSection = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + shop)).getString("section");
                    String enchantment = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + shop)).getString("enchantment", "none");
                    try{
                        if (Objects.requireNonNull(shopSection).equals(section)){
                            items.add(shop);
                            if (!Objects.requireNonNull(enchantment).contains("none")){
                                enchantedItems.add(shop);
                            }
                        }
                    }
                    catch(NullPointerException ex){
                        Main.log("Shop " + shop + " doesn't have a section, please input one to continue");
                    }
                }
                for (String item : items){
                    int maxBuy = 100000;
                    int maxSell;
                    int test = 0;
                    try{
                        maxBuy = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + item)).getInt("max-buy");
                    }
                    catch(NullPointerException ignored){
                    }
                    catch(NumberFormatException ex){
                        try{
                            maxBuy = (int) Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + item)).getDouble("max-buy");
                        }
                        catch (NumberFormatException ex2){
                            Main.log("Can't format " + item + " for max-buy");
                        }
                    }
                    try{
                        maxSell = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + item)).getInt("max-sell");
                    }
                    catch(NullPointerException ex){
                        maxSell = 100000;
                    }
                    catch(NumberFormatException ex){
                        try{
                            maxSell = (int) Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops." + item)).getDouble("max-sell");
                        }
                        catch (NumberFormatException ex2){
                            maxSell = 100000;
                            Main.log("Can't format " + item + " for max-sell");
                        }
                    }
                    Integer[] outputIntegerArray = {maxBuy, maxSell};
                    itemMaxBuySell.put(item, outputIntegerArray);
                }
            }
        }
    }
}
