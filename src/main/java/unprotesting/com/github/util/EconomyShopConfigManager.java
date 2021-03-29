package unprotesting.com.github.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EconomyShopConfigManager {

    public static boolean otherEconomyPresent = false;

    static File worth;

    static final PluginManager pm = Bukkit.getServer().getPluginManager();

    public static void checkForOtherEconomy() {
        if (Config.getEconomyShopConfig().toLowerCase().contains(("default"))) {
            Main.log("Using default Auto-Tune 'shops.yml' file");
        } 
        else {
            Main.log("Checking for economy settings for " + (Config.getEconomyShopConfig()).toLowerCase());
            if (Config.getEconomyShopConfig().toLowerCase().contains(("essentials"))) {
                if ((pm.getPlugin("Essentials") == null) && (pm.getPlugin("EssentialsX") == null)) {
                    Bukkit.getLogger().info("}---------------ERROR---------------{");
                    Bukkit.getLogger().info("Essentials must be installed!");
                    Bukkit.getLogger().info("Download it at: https://essentialsx.net/downloads.html");
                    Bukkit.getLogger().info("}---------------ERROR---------------{");
                    pm.disablePlugin(Main.getINSTANCE());
                }
                else {
                    Main.log("Using Essentials shop file for shops");
                    worth = new File("plugins/Essentials/worth.yml");
                    if (!(worth.exists())) {
                        Main.log("worth.yml doesn't exist! Make sure it's present at 'plugins/Essentials/worth.yml'");
                        return;
                    }
                    otherEconomyPresent = true;
                }
            }
            else if (Config.getEconomyShopConfig().toLowerCase().contains(("guishop"))){
                if ((pm.getPlugin("GUIShop") == null) && (pm.getPlugin("guishop") == null)) {
                    Bukkit.getLogger().info("}---------------ERROR---------------{");
                    Bukkit.getLogger().info("GUIShop must be installed!");
                    Bukkit.getLogger().info("Download it at: https://dev.bukkit.org/projects/gui-shop");
                    Bukkit.getLogger().info("}---------------ERROR---------------{");
                    pm.disablePlugin(Main.getINSTANCE());
                }
                else {
                    Main.log("Using GUIShop shop file for shops");
                    worth = new File("plugins/GUIShop/shops.yml");
                    if (!(worth.exists())) {
                        Main.log("shops.yml doesn't exist! Make sure it's present at 'plugins/GUIShop/shops.yml'");
                        return;
                    }
                    otherEconomyPresent = true;
                }
            }
        }
    }

    public static void loadShopsFile(String type) throws IOException {
        if (type.contains("essentials")){
            removeShopsFromShopsFile();
            Main.saveEssentialsFiles();
            new YamlConfiguration();
            FileConfiguration worthConfig;
            worthConfig = Main.saveEssentialsFiles();
            ConfigurationSection configSection = worthConfig.getConfigurationSection("worth");
            Map<String, Object> set = Objects.requireNonNull(configSection).getValues(false);
            for (String str : set.keySet()){
                boolean next = false;
                Double value = 0.0;
                try{
                    value = (Double) set.get(str);
                }
                catch (ClassCastException e){
                    MemorySection memSection = (MemorySection)set.get(str);
                    Set<String> memSectionKeySet = memSection.getKeys(false);
                    for (String str1 : memSectionKeySet){
                        Material[] matSet2 = MaterialUtil.MatchInputAndIDToSet(str, str1);
                        for (Material mat2 : Objects.requireNonNull(matSet2)){
                            Main.log("Set " + mat2.toString() + " in file.");
                            Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).set(mat2.toString() + ".price", (Double.parseDouble(Objects.requireNonNull(memSection.get(str1)).toString())));
                            next = true;
                        }
                    }
                }
                if (!next){
                    Material[] matSet = MaterialUtil.MatchInputToSet(str);
                    if (matSet != null){
                        for (Material mat : matSet){
                            Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).set(mat.toString() + ".price", value);
                            Main.log("Set " + mat.toString() + " in file.");
                        }
                    }
                    else{
                        Main.log("Could not find Material value for: " + str);
                    }
                }
            }
            Main.saveEssentialsFiles();
            Main.getShopConfig().save(Main.getShopf());
        }
        if(type.contains("guishop")){
            removeShopsFromShopsFile();
            new YamlConfiguration();
            FileConfiguration worthConfig;
            worthConfig = Main.saveGUIShopFiles();
            Set<String> sections = worthConfig.getKeys(false);
            for (String section : sections){
                Set<String> sections2 = Objects.requireNonNull(worthConfig.getConfigurationSection(section)).getKeys(false);
                for (String section2 : sections2){
                    Map<String, Object> values = Objects.requireNonNull(Objects.requireNonNull(worthConfig.getConfigurationSection(section)).getConfigurationSection(section2)).getValues(false);
                    String typeS = (String)values.get("type");
                    Main.log(typeS);
                    if (!typeS.equals("SHOP")){
                        continue;
                    }
                    String id = (String)values.get("id");
                    Main.log(id);
                    String sellPriceString = null;
                    String buyPriceString = null;
                    if (values.get("sell-price")!=null){
                        sellPriceString = (values.get("sell-price")).toString();
                    }
                    if (values.get("buy-price")!=null){
                        buyPriceString = (values.get("buy-price")).toString();
                    }
                    double buyWeight = 100/Config.getShopConfigGUIShopSellValue();
                    if (sellPriceString != null && buyPriceString != null){
                        Double buyPrice = Double.parseDouble(buyPriceString);
                        Double sellPrice = Double.parseDouble(sellPriceString);
                        Double newBuyValue = buyPrice*buyWeight;
                        double totalValue = newBuyValue+sellPrice;
                        totalValue = Double.parseDouble(AutoTuneGUIShopUserCommand.df1.format(totalValue/(1+buyWeight)));
                        Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).set(id + ".price", totalValue);
                    }
                    else if(sellPriceString != null){
                        Double sellPrice = (Double.parseDouble(sellPriceString)) + (Double.parseDouble(sellPriceString)*Config.getShopConfigGUIShopSellValue()*0.01);
                        sellPrice = Double.parseDouble(AutoTuneGUIShopUserCommand.df1.format(sellPrice));
                        Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).set(id + ".price", sellPrice);
                    }
                    else if (buyPriceString != null){
                        Double buyPrice = Double.parseDouble(buyPriceString);
                        buyPrice = Double.parseDouble(AutoTuneGUIShopUserCommand.df1.format(buyPrice));
                        Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).set(id + ".price", buyPrice);
                    }
                }
            }
            Main.getShopConfig().save(Main.getShopf());
        }  
    }

    public static void removeShopsFromShopsFile(){
        Set<String> strSet =  Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).getKeys(false);
        for (String str : strSet){
            Main.getShopConfig().set((("shops") + "." + (str)), null);
        }
    }
}
