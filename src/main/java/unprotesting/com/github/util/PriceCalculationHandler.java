package unprotesting.com.github.util;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PriceCalculationHandler implements Runnable {

    @Override
    public void run() {
        try {
            loadItemPricesAndCalculate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadItemPriceData();
    }

    public static void loadItemPriceData() {
        Set<String> strSet = Main.map.keySet();
        for (String str : strSet) {
            Main.itemPrices.put(str, new ItemPriceData(str));
        }
    }

    public static void loadItemPricesAndCalculate() throws IOException {
        int playerCount = Main.calculatePlayerCount();
        Main.log("Player count on price-update: " + playerCount);
        Main.setupMaxBuySell();
        if (playerCount >= Config.getUpdatePricesThreshold()){
            Main.log("Loading Item Price Update Algorithm");
            JSONObject obj = new JSONObject();
            JSONArray itemData = new JSONArray();
            for (String str : Main.map.keySet()) {
                ConcurrentHashMap<Integer, Double[]> buySellMap = Main.map.get(str);
                Double price = AutoTuneGUIShopUserCommand.getItemPrice(str, false);
                loadAverageBuyAndSellValue(itemData, str, buySellMap, price);

            }
            obj.put("itemData", itemData);
            obj.put("maxVolatility", Config.getBasicMaxVariableVolatility());
            obj.put("minVolatility", Config.getBasicMinVariableVolatility());
            HttpPostRequestor.updatePricesforItems(obj);
            Date date = Calendar.getInstance().getTime();
            Date newDate = MathHandler.addMinutesToJavaUtilDate(date, Config.getTimePeriod());
            String strDate = Main.dateFormat.format(newDate);
            Main.loadTopMovers();
            Main.log("Done running item price Algorithim, a new check will occur at: " + strDate);
            try {
                Main.debugLog("Saving data to data.csv file");
                CSVHandler.writeCSV();
                CSVHandler.writeShortCSV();
                Main.debugLog("Saved data to data.csv file");
              } catch (IOException e) {
                e.printStackTrace();
              }
        }
    }

    private static void loadAverageBuyAndSellValue(@NotNull JSONArray itemData, String str, ConcurrentHashMap<Integer, Double[]> buySellMap, Double price) {
        Double[] arr = loadAverageBuyAndSellValue(buySellMap, price, str);
        JSONObject priceData = new JSONObject();
        priceData.put("itemName", str);
        priceData.put("price",  price);
        priceData.put("averageBuy", arr[0]);
        priceData.put("averageSell", arr[1]);
        itemData.add(priceData);
    }

    public static void loadEnchantmentPricesAndCalculate() throws IOException {
        int playerCount = Main.calculatePlayerCount();
        if (playerCount >= Config.getUpdatePricesThreshold()){
            JSONObject obj = new JSONObject();
            JSONArray itemData = new JSONArray();
            Main.log("Loading Enchantment Price Update Algorithm");
            Set<String> set = Main.enchMap.keySet();
            for (String str : set) {
                ConfigurationSection config = Objects.requireNonNull(Main.getShopConfig().getConfigurationSection("shops")).getConfigurationSection(str);
                try{
                    boolean locked = Objects.requireNonNull(config).getBoolean("locked");
                    if (locked){
                        continue;
                    }
                }
                catch(NullPointerException ignored){}
                ConcurrentHashMap<Integer, Double[]> buySellMap;
                try{
                    buySellMap = Main.enchMap.get(str).buySellData;
                }
                catch(ClassCastException | NullPointerException ex){
                    continue;
                }
                Double price;
                try{
                    price = buySellMap.get(buySellMap.size()-1)[0];
                }
                catch(NullPointerException ex){
                    price = Main.enchMap.get(str).price;
                    buySellMap.put(0, new Double[]{price, 0.0, 0.0});
                }
                loadAverageBuyAndSellValue(itemData, str, buySellMap, price);

            }
            obj.put("itemData", itemData);
            obj.put("maxVolatility", Config.getBasicMaxVariableVolatility()*5.5);
            obj.put("minVolatility", Config.getBasicMinVariableVolatility()*5.5);
            HttpPostRequestor.updatePricesforEnchantments(obj);
            Date date = Calendar.getInstance().getTime();
            Date newDate = MathHandler.addMinutesToJavaUtilDate(date, Config.getTimePeriod()*2);
            String strDate = Main.dateFormat.format(newDate);
            if (Config.isSendPlayerTopMoversOnJoin()){Main.loadTopMovers();}
            Main.log("Done running enchantment price Algorithim, a new check will occur at: " + strDate);
        }
    }

    public static Double[] loadAverageBuyAndSellValue(ConcurrentHashMap<Integer, Double[]> map, Double price, String name) {
        int tempSize = map.size()-1;
        int x = 0;
        int expvalues = 0;
        double tempbuys;
        double tempsells;
        double buys = 0.0;
        double sells = 0.0;
        while (x < 100000) {
            double y = Config.getDataSelectionM() * (Math.pow(x, Config.getDataSelectionZ()))
                + Config.getDataSelectionC();
            int inty = (int) Math.round(y) - 1;
            if (inty > tempSize - 1) {
                expvalues = inty + 1;
                break;
            }
            Double[] key = map.get((tempSize) - inty);
            if (key == null){
                key = new Double[]{price, 0.0, 0.0};
            }
            if (key[0] == null){
                key[0] = price;
            }
            if (key[1] == null){
                key[1] = 0.0;
            }
            if (key[2] == null){
                key[2] = 0.0;
            }
            tempbuys = key[1];
            tempbuys = tempbuys * key[0];
            if (tempbuys == 0) {
                tempbuys = key[0];
            }
            buys = buys + tempbuys;
            tempsells = key[2];
            tempsells = tempsells * key[0];
            if (tempsells == 0) {
                tempsells = key[0];
            }
            sells = sells + tempsells;
            x++;
        }
        if (Config.isInflationEnabled()){
            buys = buys + buys * 0.01 * Config.getInflationValue();
        }
        double avBuy = buys / (expvalues);
        double avSells = sells / (expvalues);
        if (avBuy > avSells){
            Main.debugLog("AvBuyValue > AvSellValue for " + name);
        }
        if (avBuy < avSells){
            Main.debugLog("AvBuyValue < AvSellValue for " + name);
        }
        if (Objects.equals(avBuy, avSells)){
            Main.debugLog("AvBuyValue = AvSellValue for " + name);
        }
        return new Double[]{avBuy, avSells};
    }
    
}
