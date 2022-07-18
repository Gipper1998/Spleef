package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerStatManager {

    public static PlayerStatManager psm;

    private FileConfiguration players;

    public PlayerStatManager(){
        this.players = Spleef.main.playerStats.getConfig();
    }

    public static PlayerStatManager getInstance(){
        if (psm == null){
            psm = new PlayerStatManager();
        }
        return psm;
    }

    //Add point
    public void addWinPoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = players.getInt(path + "Wins");
            losses = players.getInt(path + "Losses");
        }
        players.set(path + "Wins", wins + 1);
        players.set(path + "Losses", losses);
        Spleef.main.playerStats.saveConfig();
    }

    public void addLosePoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = players.getInt(path + "Wins");
            losses = players.getInt(path + "Losses");
        }
        players.set(path + "Wins", wins);
        players.set(path + "Losses", losses + 1);
        Spleef.main.playerStats.saveConfig();
    }

    public void setWinPoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int losses = 0;
        if (checkFiles(path)){
            losses = players.getInt(path + "Losses");
        }
        players.set(path + "Wins", score);
        players.set(path + "Losses", losses);
        Spleef.main.playerStats.saveConfig();
    }

    public void setLosePoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int wins = 0;
        if (checkFiles(path)){
            wins = players.getInt(path + "Wins");
        }
        players.set(path + "Wins", wins);
        players.set(path + "Losses", score);
        Spleef.main.playerStats.saveConfig();
    }

    public int getWins(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return players.getInt("Players." + uuid + ".Wins");
        }
        return 0;
    }

    public int getLosses(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return players.getInt("Players." + uuid + ".Losses");
        }
        return 0;
    }

    public UUID findPlayer(String name){
        if (players.getConfigurationSection("Players") == null) {
            return null;
        }
        for (String key : players.getConfigurationSection("Players").getKeys(false)) {
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

    private boolean checkFiles(String path){
        return (players.contains(path + "Wins") && players.contains(path + "Losses"));
    }

}
