package unprotesting.com.github.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TutorialHandler implements Runnable{
    
    public static final List<String> messages = new ArrayList<>();
    public final ConcurrentHashMap<Player, Integer> playerList = new ConcurrentHashMap<>();


    @Override
    public void run() {
        if (Config.isTutorial()){
            Collection<Player> players = (Collection<Player>)(Bukkit.getOnlinePlayers());
            for (Player player : players){
                playerList.putIfAbsent(player, 0);
                if (!player.hasPermission("at.tutorial") && !player.isOp()){
                    continue;
                }
                if (player.isOnline()){
                    if (playerList.get(player) > messages.size()-1){
                        playerList.put(player, 0);
                    }
                    player.sendMessage(messages.get(playerList.get(player)));
                    playerList.put(player, (playerList.get(player)+1));
                }
            }
        }
    }

    public static void loadMessages(){
        messages.add(ChatColor.YELLOW + "Do /shop to open the shop and start exchanging items.");
        messages.add(ChatColor.YELLOW + "Do /sell to sell items quickly.");
        messages.add(ChatColor.YELLOW + "Control the market! Your purchases rise prices.");
        messages.add(ChatColor.YELLOW + "Control the market! Your sales lower prices.");
        messages.add(ChatColor.YELLOW + "Do /autosell to sell items automatically.");
        messages.add(ChatColor.YELLOW + "Do /buy to purchase custom items.");
        messages.add(ChatColor.YELLOW + "Do /trade to view item prices over time.");
        messages.add(ChatColor.YELLOW + "Do /loan <amount> to loan money. Do /loan to start.");
        messages.add(ChatColor.YELLOW + "Do /gdp to view the server GDP.");
        messages.add(ChatColor.YELLOW + "Inflation means prices for items generally rise. Put your money in assets (items) to make more money");
        messages.add(ChatColor.YELLOW + "Watch the prices of items! They can go up and down.");
        messages.add(ChatColor.YELLOW + "There are big profits to be made when prices change..");
        messages.add(ChatColor.YELLOW + "Loan money to take leverage of the rise in prices.");
        messages.add(ChatColor.YELLOW + "Sell items that have high demand to make the most money selling.");
        messages.add(ChatColor.YELLOW + "Using /autosell allows you to make money quickly and easily");
        messages.add(ChatColor.YELLOW + "Using /loan to loan money allows you to invest to make even more money.");
        messages.add(ChatColor.YELLOW + "Viewing prices with /trade allows you to judge the markets.");
    }
}
