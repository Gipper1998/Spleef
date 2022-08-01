package me.gipper1998.spleef;

import me.gipper1998.spleef.file.ArenaManager;
import me.gipper1998.spleef.command.CommandManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.sign.SignManager;
import me.gipper1998.spleef.softdepend.PAPIManager;
import me.gipper1998.spleef.softdepend.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spleef extends JavaPlugin {

    public static Spleef main;

    @Override
    public void onEnable() {
        this.main = this;
        ArenaManager.getInstance().loadArenas();
        ConfigManager.getInstance().reloadConfig();
        PlayerStatManager.getInstance().reloadStats();
        registerSoftDependencies();
        getCommand("spleef").setExecutor(new CommandManager());
        SignManager.getInstance().startUpdater();
        MessageManager.getInstance().sendConsoleMessage("start_up");
    }

    @Override
    public void onDisable() {
        ArenaManager.getInstance().shutGamesDown();
        MessageManager.getInstance().sendConsoleMessage("shut_down");
    }
    private void registerSoftDependencies(){
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            if (VaultManager.getInstance().registerVault()) {
                MessageManager.getInstance().sendConsoleMessage("vault_enable");
            }
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PAPIManager().register();
            MessageManager.getInstance().sendConsoleMessage("papi_enable");
        }

    }

}
