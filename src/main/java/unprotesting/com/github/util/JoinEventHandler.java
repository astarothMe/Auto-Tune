package unprotesting.com.github.util;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JoinEventHandler implements Listener {

    public static Logger log = Logger.getLogger("Minecraft");
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        sendTopMoversMessages(p);
        UUID uuid = p.getUniqueId();
        String name = p.getName();
        Main.playerDataConfig.set(uuid + ".name", name);
        Main.saveplayerdata();
        if (!Config.isDisableMaxBuysSells()){
            ConcurrentHashMap<String, Integer> cMap = Main.loadMaxStrings(Main.map);
            if (!Main.maxBuyMap.containsKey(((OfflinePlayer) p).getUniqueId())){
                Main.maxBuyMap.put(((OfflinePlayer) p).getUniqueId(), cMap);
            }  
            if (!Main.maxSellMap.containsKey(((OfflinePlayer) p).getUniqueId())){
                Main.maxSellMap.put(((OfflinePlayer) p).getUniqueId(), cMap);
            }     
        }
    }

    public void sendTopMoversMessages(Player player){
        if (Config.isSendPlayerTopMoversOnJoin()){
            player.sendMessage(ChatColor.GREEN + "***** | Top Buyers Today: | ******");
            for (TopMover mover : Main.topBuyers){
                player.sendMessage(ChatColor.GOLD + mover.name + ": " +  ChatColor.GREEN + " %+" + AutoTuneGUIShopUserCommand.df2.format(mover.percentage_change));
            }
            player.sendMessage(ChatColor.RED + "***** | Top Sellers Today: | ******");
            for (TopMover mover : Main.topSellers){
                player.sendMessage(ChatColor.GOLD + mover.name + ": " +  ChatColor.RED + " %" + AutoTuneGUIShopUserCommand.df2.format(mover.percentage_change));
            }
        }
    }

}
    

  