package unprotesting.com.github.util;

import org.bukkit.inventory.ItemStack;

public class DurabilityAlgorithm {
    
    @Deprecated
    public static double calculateDurability(ItemStack is){
        double durability = is.getDurability();
        double maxDurability = is.getType().getMaxDurability();
        if (durability == 0 ){
            return 100.00;
        }
        double current = maxDurability - durability;
        return (current/maxDurability)*100;
    }

}