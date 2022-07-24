package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    public static ConfigManager cm;

    private FileConfiguration config;

    public ConfigManager(){
        this.config = Spleef.main.config.getConfig();
    }

    public static ConfigManager getInstance(){
        if (cm == null){
            cm = new ConfigManager();
        }
        return cm;
    }

    public void reloadConfig() {
        Spleef.main.config.reloadConfig();
        config = Spleef.main.config.getConfig();
    }

    public Material getBlock(String path){
        String block = config.getString(path);
        try {
            return Material.matchMaterial(block.toUpperCase());
        }
        catch (Exception e) {
            return Material.AIR;
        }
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path){
        return config.getBoolean(path);
    }

    public boolean contains(String path){
        return config.contains(path);
    }

    public List<String> getStringList(String path){
        return config.getStringList(path);
    }

}
