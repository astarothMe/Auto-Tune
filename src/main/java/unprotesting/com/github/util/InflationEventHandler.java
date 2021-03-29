package unprotesting.com.github.util;

import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.util.concurrent.ConcurrentHashMap;

public class InflationEventHandler implements Runnable {
    @Override
    public void run() {
        int playerCount = Main.calculatePlayerCount();
        if (playerCount >= Config.getUpdatePricesThreshold()){
            for (String str : Main.map.keySet()){
                increaseItemPrice(str, Config.getDynamicInflationValue(), true);
            }
        }
        if (Config.isSendPlayerTopMoversOnJoin()){Main.loadTopMovers();}
        PriceCalculationHandler.loadItemPriceData();
        Main.debugLog("Dynamic Inflation Value: " + Config.getDynamicInflationValue());
    }

    public static Double increaseItemPrice(String item, Double value, boolean percentage){
        Double price  = AutoTuneGUIShopUserCommand.getItemPrice(item, false);
        double newPrice = 0.0;
        if (percentage){
        newPrice = price+price*0.01*value;
        }
        if (!percentage){
            newPrice = price+value;
        }
        ConcurrentHashMap<Integer, Double[]> outputMap = Main.map.get(item);
        int size = outputMap.size();
        Double[] arr = {newPrice, outputMap.get((size-1))[1], outputMap.get((size-1))[2]};
        outputMap.put(size-1, arr);
        Main.map.put(item, outputMap);
        Main.debugLog("Increased item price of: " + item + " from " + price + " to " + newPrice);
        return newPrice;
    }

    public static Double decreaseItemPrice(String item, Double value, boolean percentage){
        Double price  = AutoTuneGUIShopUserCommand.getItemPrice(item, false);
        double newPrice = 0.0;
        if (percentage){
        newPrice = price-price*0.01*value;
        }
        if (!percentage){
            newPrice = price-value;
        }
        ConcurrentHashMap<Integer, Double[]> outputMap = Main.map.get(item);
        int size = outputMap.size();
        Double[] arr = {newPrice, outputMap.get((size-1))[1], outputMap.get((size-1))[2]};
        outputMap.put(size-1, arr);
        Main.map.put(item, outputMap);
        Main.debugLog("Increased item price of: " + item + " from " + price + " to " + newPrice);
        return newPrice;
    }
    
}