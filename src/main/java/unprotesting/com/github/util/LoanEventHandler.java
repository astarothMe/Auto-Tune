package unprotesting.com.github.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import unprotesting.com.github.commands.AutoTuneGUIShopUserCommand;
import unprotesting.com.github.Main;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class LoanEventHandler implements Runnable{

    @Override
    public void run() {
        for (UUID uuid : Main.loanMap.keySet()){
            ArrayList<Loan> map = Main.loanMap.get(uuid);
            int k = Objects.requireNonNull(map).size();
            for (int i = 0; i < k; i++){
                Loan loan = map.get(i);
                map.remove(i);
                loan = updateLoan(loan);
                map.add(i, loan);
            }
            Main.loanMap.put(uuid, map);
        }
    } 
    
    Loan updateLoan(Loan loan){
        double value = loan.value;
        Instant now = Instant.now();
        Duration seconds = Duration.between(loan.instant, now);
        double update_rate = loan.interest_rate_update;
        double updates = seconds.getSeconds()/update_rate;
        if (loan.compound_enabled = true){
            for (int i = 0; i < updates; i++){
                value = value + value*loan.interest_rate;
            }
            loan.current_value = value;
            return loan;
        }
        else{
            double interest = value*loan.interest_rate*updates;
            loan.current_value = value + interest;
            return loan;
        }
    }

    public static double getPlayerLoanValue(Player player){
        UUID uuid = player.getUniqueId();
        ArrayList<Loan> map = Main.loanMap.get(uuid);
        if (map == null){
            map = new ArrayList<>();
            Main.loanMap.put(uuid, map);
            return 0.0;
        }
        double output = 0.0;
        for (Loan loan : map){
            output += loan.current_value;
        }
        return output;
    }

    public static void payLoan(Player player, int loanNo){
        UUID uuid = player.getUniqueId();
        ArrayList<Loan> map = Main.loanMap.get(uuid);
        Loan loan = Objects.requireNonNull(map).get(loanNo);
        try{
            map.remove(loanNo);
        }
        catch(IndexOutOfBoundsException ex){
            player.sendMessage(ChatColor.RED + "This loan does not exist!");
            return;
        }
        Main.loanMap.put(uuid, map);
        Main.getEconomy().withdrawPlayer(player, loan.current_value);
        player.sendMessage(ChatColor.YELLOW + "Withdrew " + ChatColor.GREEN + Config.getCurrencySymbol() + AutoTuneGUIShopUserCommand.df2.format(loan.current_value));
    }
}
    
