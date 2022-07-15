package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Material;

public class ConfigManager {

    public static Material getBlock(String path){
        String block = Spleef.main.config.getConfig().getString(path);
        try {
            return Material.matchMaterial(block.toUpperCase());
        }
        catch (Exception e) {
            return Material.AIR;
        }
    }

    public static int getInt(String path) {
        return Spleef.main.config.getConfig().getInt(path);
    }

    public static boolean getBoolean(String path){
        return Spleef.main.config.getConfig().getBoolean(path);
    }

    public static boolean contains(String path){
        return Spleef.main.config.getConfig().contains(path);
    }
}
