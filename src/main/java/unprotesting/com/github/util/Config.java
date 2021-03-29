package unprotesting.com.github.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import unprotesting.com.github.Main;

import java.util.Objects;

public final class Config {

    @Getter
    @Setter
    private static boolean webServer, 
                                debugEnabled, 
                                    checksumHeaderBypass, 
                                        sellPriceDifferenceVariationEnabled,
                                            inflationEnabled,
                                                autoSellEnabled,
                                                    calculateGlobalGDP,
                                                        tutorial,
                                                            sendPlayerTopMoversOnJoin,
                                                                dataTransactions,   
                                                                    disableMaxBuysSells,    
                                                                        ignoreAFK;

    @Getter
    @Setter
    private static Integer port, 
                            timePeriod, 
                                menuRows, 
                                    sellPriceVariationTimePeriod, 
                                        sellPriceVariationUpdatePeriod, 
                                            autoSellUpdatePeriod, 
                                                autoSellProfitUpdatePeriod,
                                                    dynamicInflationUpdatePeriod,
                                                        maximumShortTradeLength,
                                                            InterestRateUpdateRate,
                                                                tutorialMessagePeriod,
                                                                    topMoversAmount,
                                                                        updatePricesThreshold;

    @Getter
    @Setter
    private static String serverName, 
                            pricingModel, 
                                basicVolatilityAlgorithim, 
                                    menuTitle, 
                                        noPermission,
                                            apiKey,
                                                email,
                                                    inflationMethod,
                                                        currencySymbol,
                                                            economyShopConfig,
                                                                dataLocation;

    @Getter
    @Setter
    private static Double basicMaxFixedVolatility, 
                            basicMaxVariableVolatility, 
                                basicMinFixedVolatility, 
                                    basicMinVariableVolatility, 
                                        sellPriceDifference, 
                                            sellPriceDifferenceVariationStart, 
                                                dataSelectionM, 
                                                    dataSelectionC,
                                                        dataSelectionZ,
                                                            dynamicInflationValue,
                                                                inflationValue,
                                                                    interestRate,
                                                                        maxDebt,
                                                                           shopConfigGUIShopSellValue,
                                                                                enchantmentLimiter,
                                                                                    durabilityLimiter,
                                                                                        compoundInterestRate;
    
    public static void loadDefaults() {
    Config.setSellPriceDifferenceVariationEnabled(Main.getMainConfig().getBoolean("sell-price-difference-variation-enabled", true));
    Config.setSendPlayerTopMoversOnJoin(Main.getMainConfig().getBoolean("send-player-top-movers-on-join", true));
    Config.setWebServer(Main.getMainConfig().getBoolean("web-server-enabled", true));
    Config.setTutorial(Main.getMainConfig().getBoolean("tutorial", true));
    Config.setIgnoreAFK(Main.getMainConfig().getBoolean("ignore-afk", true));
    Config.setDataTransactions(Main.getMainConfig().getBoolean("data-transactions", false));
    Config.setDisableMaxBuysSells(Main.getMainConfig().getBoolean("disable-max-buys-sells", false));
    Config.setCalculateGlobalGDP(Main.getMainConfig().getBoolean("calculate-global-GDP", true));
    Config.setAutoSellEnabled(Main.getMainConfig().getBoolean("auto-sell-enabled", true));
    Config.setInflationEnabled(Main.getMainConfig().getBoolean("inflation-enabled", true));
    Config.setChecksumHeaderBypass(Main.getMainConfig().getBoolean("checksum-header-bypass", false));
    Config.setDebugEnabled(Main.getMainConfig().getBoolean("debug-enabled", false));
    Config.setAutoSellProfitUpdatePeriod(Main.getMainConfig().getInt("auto-sell-profit-update-period", 1200));
    Config.setPort(Main.getMainConfig().getInt("port", 8321));
    Config.setUpdatePricesThreshold(Main.getMainConfig().getInt("update-prices-threshold", 1));
    Config.setTopMoversAmount(Main.getMainConfig().getInt("top-movers-amount", 5));
    Config.setMaximumShortTradeLength(Main.getMainConfig().getInt("maximum-short-trade-length", 100));
    Config.setAutoSellUpdatePeriod(Main.getMainConfig().getInt("auto-sell-update-period", 10));
    Config.setTimePeriod(Main.getMainConfig().getInt("time-period", 10));
    Config.setMenuRows(Main.getMainConfig().getInt("menu-rows", 3));
    Config.setTutorialMessagePeriod(Main.getMainConfig().getInt("tutorial-message-period", 300));
    Config.setInterestRateUpdateRate(Main.getMainConfig().getInt("interest-rate-update-period", 1200));
    Config.setDynamicInflationUpdatePeriod(Main.getMainConfig().getInt("dynamic-inflation-update-period", 5000));
    Config.setSellPriceVariationTimePeriod(Main.getMainConfig().getInt("sell-price-variation-time-period", 10800));
    Config.setSellPriceVariationUpdatePeriod(Main.getMainConfig().getInt("sell-price-variation-update-period", 30));
    Config.setServerName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("server-name", "Survival Server - (Change this in Config)"))));
    Config.setCurrencySymbol(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("currency-symbol", "$"))));
    Config.setMenuTitle(
    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("menu-title", "Auto-Tune Shop"))));
    Config.setPricingModel(
    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("pricing-model", "Exponential"))));
    Config.setApiKey(Main.getMainConfig().getString("api-key", "xyz"));
    Config.setDataLocation(Main.getMainConfig().getString("data-location", ""));
    Config.setInflationMethod(Main.getMainConfig().getString("inflation-method", "Mixed"));
    Config.setEmail(Main.getMainConfig().getString("email", "xyz@gmail.com"));
    Config.setBasicVolatilityAlgorithim(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("Volatility-Algorithim", "Variable"))));
    Config.setNoPermission(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getMainConfig().getString("no-permission", "You do not have permission to perform this command"))));
    Config.setEconomyShopConfig(Main.getMainConfig().getString("economy-shop-config", "default"));
    Config.setBasicMaxFixedVolatility(Main.getMainConfig().getDouble("Fixed-Max-Volatility", 2.00));
    Config.setBasicMaxVariableVolatility(Main.getMainConfig().getDouble("max-volatility", 0.5));
    Config.setBasicMinFixedVolatility(Main.getMainConfig().getDouble("Fixed-Min-Volatility", 0.05));
    Config.setBasicMinVariableVolatility(Main.getMainConfig().getDouble("min-volatility", 0.025));
    Config.setDataSelectionM(Main.getMainConfig().getDouble("data-selection-m", 0.05));
    Config.setDataSelectionC(Main.getMainConfig().getDouble("data-selection-c", 1.05));
    Config.setDynamicInflationValue(Main.getMainConfig().getDouble("dynamic-inflation-value", 0.0025));
    Config.setInflationValue(Main.getMainConfig().getDouble("static-inflation-value", 0.1));
    Config.setDataSelectionZ(Main.getMainConfig().getDouble("data-selection-z", 1.6));
    Config.setSellPriceDifference(Main.getMainConfig().getDouble("sell-price-difference", 2.5));
    Config.setEnchantmentLimiter(Main.getMainConfig().getDouble("enchantment-limiter", 7.5));
    Config.setDurabilityLimiter(Main.getMainConfig().getDouble("durability-limiter", 5.0));
    Config.setInterestRate(Main.getMainConfig().getDouble("interest-rate", 0.005));
    Config.setCompoundInterestRate(Main.getMainConfig().getDouble("compound-interest-rate", 0.0025));
    Config.setMaxDebt(Main.getMainConfig().getDouble("max-debt-value", -100.00));
    Config.setSellPriceDifferenceVariationStart(Main.getMainConfig().getDouble("sell-price-difference-variation-start", 25.0));
    Config.setShopConfigGUIShopSellValue(Main.getMainConfig().getDouble("shop-config-guishop-sell-value", 20.00));
    if (getTimePeriod() < 3){
        Main.debugLog("Time-Period Setting reverting to 3 to reduce memory usage. If you would like lower time periods open a ticket in the offical discord.");
        Config.setTimePeriod(3);
    }
  }

}