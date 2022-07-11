package me.gipper1998.spleef.softdepend;

import me.gipper1998.spleef.Spleef;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {
    private static Economy economy;

    public static boolean registerVault() {
        RegisteredServiceProvider<Economy> rsp = Spleef.main.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public static void deposit(Player player, int money){
        economy.depositPlayer(player, money); }
}
