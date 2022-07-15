package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerStatManager {

    public static void addWinPoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = Spleef.main.playerStats.getConfig().getInt(path + "Wins");
            losses = Spleef.main.playerStats.getConfig().getInt(path + "Losses");
        }
        Spleef.main.playerStats.getConfig().set(path + "Wins", wins++);
        Spleef.main.playerStats.getConfig().set(path + "Losses", losses);
        Spleef.main.playerStats.saveConfig();
    }

    public static void addLosePoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = Spleef.main.playerStats.getConfig().getInt(path + "Wins");
            losses = Spleef.main.playerStats.getConfig().getInt(path + "Losses");
        }
        Spleef.main.playerStats.getConfig().set(path + "Wins", wins);
        Spleef.main.playerStats.getConfig().set(path + "Losses", losses++);
        Spleef.main.playerStats.saveConfig();
    }

    public static void setWinPoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int losses = 0;
        if (checkFiles(path)){
            losses = Spleef.main.playerStats.getConfig().getInt(path + "Losses");
        }
        Spleef.main.playerStats.getConfig().set(path + "Wins", score);
        Spleef.main.playerStats.getConfig().set(path + "Losses", losses);
        Spleef.main.playerStats.saveConfig();
    }

    public static void setLosePoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int wins = 0;
        if (checkFiles(path)){
            wins = Spleef.main.playerStats.getConfig().getInt(path + "Wins");
        }
        Spleef.main.playerStats.getConfig().set(path + "Wins", wins);
        Spleef.main.playerStats.getConfig().set(path + "Losses", score);
        Spleef.main.playerStats.saveConfig();
    }

    public static int getWins(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return Spleef.main.playerStats.getConfig().getInt("Players." + uuid + ".Wins");
        }
        return 0;
    }

    public static int getLosses(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return Spleef.main.playerStats.getConfig().getInt("Players." + uuid + ".Losses");
        }
        return 0;
    }

    public static UUID findPlayer(String name){
        if (Spleef.main.playerStats.getConfig().getConfigurationSection("Players") == null) {
            return null;
        }
        for (String key : Spleef.main.playerStats.getConfig().getConfigurationSection("Players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String temp = Bukkit.getOfflinePlayer(uuid).getName();
                if (temp.equalsIgnoreCase(name)) {
                    return uuid;
                }
            }
            catch (Exception e){
                return null;
            }
        }
        return null;
    }

    private static boolean checkFiles(String path){
        return (Spleef.main.playerStats.getConfig().contains(path + "Wins") && Spleef.main.playerStats.getConfig().contains(path + "Losses"));
    }

}
