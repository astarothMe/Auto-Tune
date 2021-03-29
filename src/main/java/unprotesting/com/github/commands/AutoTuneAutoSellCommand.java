package unprotesting.com.github.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unprotesting.com.github.Main;
import unprotesting.com.github.util.Config;
import unprotesting.com.github.util.TextHandler;

import java.util.UUID;

public class AutoTuneAutoSellCommand implements CommandExecutor{


    @Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String shop, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("autosell")) {
			if (!(sender instanceof Player)) {
				Main.sendMessage(sender, "&cPlayers only.");
				return true;
			}
			Player p = (Player) sender;
			if (Config.getMenuRows() == 6) {
				AutoTuneGUIShopUserCommand.SBPanePos = 2;
			}
			if (args.length == 0) {
				if (p.hasPermission("at.autosell") || p.isOp()) {
                    if (!Config.isAutoSellEnabled()){
                        p.sendMessage(ChatColor.RED + "Auto-Selling is Disabled");
                        return true;
                    }
					AutoTuneGUIShopUserCommand.loadGUISECTIONS(p, true);
                    return true;
				} else if (!(p.hasPermission("at.autosell")) && !(p.isOp())) {
					TextHandler.noPermssion(p);
                    return true;
				}
			}
			if (args.length == 1) {
                if (p.hasPermission("at.autosell") || p.isOp()) {
				} else if (!(p.hasPermission("at.autosell")) && !(p.isOp())) {
					TextHandler.noPermssion(p);
                    return true;
				}
				String inputSection;
				try {
					inputSection = args[0];
				} catch (ClassCastException ex) {
					p.sendMessage("Unknown shop format: " + args[0]);
					return false;
				} catch (ArrayIndexOutOfBoundsException ex) {
					return false;
				}
				for (int i = 0; i < Main.sectionedItems.length; i++) {
					if (Main.sectionedItems[i].name.toLowerCase().equals(inputSection)) {
						AutoTuneGUIShopUserCommand.loadGUIMAIN(p, Main.sectionedItems[i], true, true);
						return true;
					}
				}
			} else {
				return false;
			}
		}
		return false;
	}

    public static void changePlayerAutoSellSettings(Player player, String material){
        UUID uuid = player.getUniqueId();
        boolean autosellset;
        Main.playerDataConfig.contains(uuid + ".AutoSell");
		boolean atonoff = Main.playerDataConfig.getBoolean(uuid + ".AutoSell" + "." + material);
        if (!(Main.playerDataConfig.contains(uuid + ".AutoSell" + "." + material))) {
             Main.playerDataConfig.createSection(uuid + ".AutoSell" + "." + material);
        }
        if (!atonoff){
            Main.playerDataConfig.set(uuid + ".AutoSell" + "." + material, true);
        }
        if (atonoff){
            Main.playerDataConfig.set(uuid + ".AutoSell" + "." + material, false);
        }
        Main.saveplayerdata();
    }

    
}