package me.gipper1998.spleef.softdepend;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.MessageManager;
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
        if (economy != null || money != 0) {
            economy.depositPlayer(p, money);
            MessageManager.getInstance().sendVaultPlayerMessage("vault_message", p, money);
        }
        else {
            MessageManager.getInstance().sendCustomConsoleMessage("<prefix> &cVault was not detected, you may either install vault, or use the commands section and set the vault amount to 0.");
            return;
        }
    }
}
