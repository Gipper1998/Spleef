package me.gipper1998.spleef;

import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.command.CommandManager;
import me.gipper1998.spleef.file.FileManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.sign.SignManager;
import me.gipper1998.spleef.softdepend.PAPIManager;
import me.gipper1998.spleef.softdepend.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class Spleef extends JavaPlugin {

    public static Spleef main;

    public boolean vaultEnabled = false;

    public FileManager arenas;
    public FileManager messages;
    public FileManager config;
    public FileManager playerStats;
    public FileManager signs;

    public SignManager signManager;

    @Override
    public void onEnable() {
        this.main = this;
        setupFiles();
        ArenaManager.loadArenas();
        registerSoftDependencies();
        getCommand("spleef").setExecutor(new CommandManager());
        this.signManager = new SignManager();
        signManager.startUpdater();
        MessageManager.sendMessage("start_up", null);
    }

    @Override
    public void onDisable() {
        ArenaManager.shutGamesDown();
        MessageManager.sendMessage("shut_down", null);
    }

    private void setupFiles(){
        this.config = new FileManager(this, "config.yml");
        this.arenas = new FileManager(this, "arenas.yml");
        this.messages = new FileManager(this, "messages.yml");
        this.playerStats = new FileManager(this, "players.yml");
        this.signs = new FileManager(this, "signs.yml");
    }

    private void registerSoftDependencies(){
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            if (VaultManager.registerVault()) {
                this.vaultEnabled = true;
                MessageManager.sendMessage("vault_enable", null);
            }
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PAPIManager().register();
            MessageManager.sendMessage("papi_enable", null);
        }

    }

}
