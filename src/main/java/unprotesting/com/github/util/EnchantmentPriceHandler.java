package unprotesting.com.github.util;

import java.io.IOException;

public class EnchantmentPriceHandler implements Runnable {

    @Override
    public void run() {
        try {
            PriceCalculationHandler.loadEnchantmentPricesAndCalculate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
