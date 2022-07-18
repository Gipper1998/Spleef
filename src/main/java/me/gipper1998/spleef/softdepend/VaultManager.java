package me.gipper1998.spleef.softdepend;

import me.gipper1998.spleef.Spleef;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {

    public static VaultManager vm;

    public static VaultManager getInstance(){
        if (vm == null){
            vm = new VaultManager();
        }
        return vm;
    }
    private Economy economy;

    public boolean registerVault() {
        RegisteredServiceProvider<Economy> rsp = Spleef.main.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public void deposit(Player p, int money){
        economy.depositPlayer(p, money);
    }
    public void withdraw(Player p, int money){ economy.withdrawPlayer(p, money); }
}
