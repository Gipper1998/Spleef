package me.gipper1998.spleef;

import lombok.Getter;
import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.command.CommandManager;
import me.gipper1998.spleef.file.FileManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.sign.SignManager;
import me.gipper1998.spleef.softdepend.PAPIManager;
import me.gipper1998.spleef.softdepend.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spleef extends JavaPlugin {

    public static Spleef main;

    public FileManager arenas;
    public FileManager messages;
    public FileManager config;
    public FileManager playerStats;
    public FileManager signs;


    @Override
    public void onEnable() {
        this.main = this;
        setupFiles();
        ArenaManager.getInstance().loadArenas();
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

    private void setupFiles(){
        this.config = new FileManager(this, "config.yml");
        this.arenas = new FileManager(this, "arenas.yml");
        this.messages = new FileManager(this, "messages.yml");
        this.playerStats = new FileManager(this, "players.yml");
        this.signs = new FileManager(this, "signs.yml");
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
