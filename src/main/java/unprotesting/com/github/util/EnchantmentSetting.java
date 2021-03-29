package unprotesting.com.github.util;

import unprotesting.com.github.Main;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentSetting implements Serializable{
    
    private static final long serialVersionUID = 2393067834138849688L;
    public ConcurrentHashMap<Integer, Double[]> buySellData;
    public final String name;
    public double price;
    public final double ratio;

    public EnchantmentSetting(String name){
        this.name = name;
        price = Main.getEnchantmentConfig().getDouble("enchantments." + name + ".price");
        ratio = Main.getEnchantmentConfig().getDouble("enchantments." + name + ".ratio");
        buySellData = new ConcurrentHashMap<>();
        buySellData.put(0, new Double[]{this.price, 0.0, 0.0});
    }
}
