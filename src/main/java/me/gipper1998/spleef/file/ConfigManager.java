package me.gipper1998.spleef.file;

import org.bukkit.Material;

import java.util.List;
import java.util.Set;

public class ConfigManager {

    private static ConfigManager cm;

    private FileManager config;

    public ConfigManager(){
        this.config = new FileManager("config.yml");
    }

    public static ConfigManager getInstance(){
        if (cm == null){
            cm = new ConfigManager();
        }
        return cm;
    }

    public void reloadConfig() {
        config.reloadConfig();
    }

    public Material getBlock(String path){
        String block = config.getConfig().getString(path);
        try {
            return Material.matchMaterial(block.toUpperCase());
        }
        catch (Exception e) {
            return Material.AIR;
        }
    }

    public int getInt(String path) {
        return config.getConfig().getInt(path);
    }

    public boolean getBoolean(String path){
        return config.getConfig().getBoolean(path);
    }

    public boolean contains(String path){
        return config.getConfig().contains(path);
    }

    public List<String> getStringList(String path){
        return config.getConfig().getStringList(path);
    }

    public String getString(String path) {
        return config.getConfig().getString(path);
    }

    public Set<String> getConfigurationSection(String path){
        return config.getConfig().getConfigurationSection(path).getKeys(false);
    }
}
