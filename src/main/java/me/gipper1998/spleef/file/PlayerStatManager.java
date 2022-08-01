package me.gipper1998.spleef.file;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerStatManager {

    private static PlayerStatManager psm;

    private FileManager players;

    public PlayerStatManager(){
        this.players = new FileManager("players.yml");
    }

    public static PlayerStatManager getInstance(){
        if (psm == null){
            psm = new PlayerStatManager();
        }
        return psm;
    }

    public void reloadStats(){
        players.reloadConfig();
    }

    public void addWinPoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = players.getConfig().getInt(path + "Wins");
            losses = players.getConfig().getInt(path + "Losses");
        }
        players.getConfig().set(path + "Wins", wins + 1);
        players.getConfig().set(path + "Losses", losses);
        players.saveConfig();
    }

    public void addLosePoint(UUID uuid){
        String path = "Players." + uuid + ".";
        int wins = 0;
        int losses = 0;
        if (checkFiles(path)){
            wins = players.getConfig().getInt(path + "Wins");
            losses = players.getConfig().getInt(path + "Losses");
        }
        players.getConfig().set(path + "Wins", wins);
        players.getConfig().set(path + "Losses", losses + 1);
        players.saveConfig();
    }

    public void setWinPoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int losses = 0;
        if (checkFiles(path)){
            losses = players.getConfig().getInt(path + "Losses");
        }
        players.getConfig().set(path + "Wins", score);
        players.getConfig().set(path + "Losses", losses);
        players.saveConfig();
    }

    public void setLosePoint(UUID uuid, int score){
        String path = "Players." + uuid + ".";
        int wins = 0;
        if (checkFiles(path)){
            wins = players.getConfig().getInt(path + "Wins");
        }
        players.getConfig().set(path + "Wins", wins);
        players.getConfig().set(path + "Losses", score);
        players.saveConfig();
    }

    public int getWins(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return players.getConfig().getInt("Players." + uuid + ".Wins");
        }
        return 0;
    }

    public int getLosses(UUID uuid){
        String path = "Players." + uuid + ".";
        if (checkFiles(path)) {
            return players.getConfig().getInt("Players." + uuid + ".Losses");
        }
        return 0;
    }

    public UUID findPlayer(String name){
        if (players.getConfig().getConfigurationSection("Players") == null) {
            return null;
        }
        for (String key : players.getConfig().getConfigurationSection("Players").getKeys(false)) {
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

    public List<String> getPlayerNames(){
        List<String> temp = new ArrayList<>();
        if (players.getConfig().getConfigurationSection("Players") != null){
            for (String key : players.getConfig().getConfigurationSection("Players").getKeys(false)){
                try {
                    UUID uuid = UUID.fromString(key);
                    temp.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
                catch (Exception e){
                    return null;
                }
            }
        }
        return temp;
    }

    public HashMap<UUID, Integer> getData(){
        HashMap<UUID, Integer> temp = new HashMap<>();
        if (players.getConfig().getConfigurationSection("Players") != null){
            for (String key : players.getConfig().getConfigurationSection("Players").getKeys(false)){
                try {
                    UUID uuid = UUID.fromString(key);
                    temp.put(uuid, PlayerStatManager.getInstance().getWins(uuid));
                }
                catch (Exception e){
                    return null;
                }
            }
        }
        return temp;
    }

    private boolean checkFiles(String path){
        return (players.getConfig().contains(path + "Wins") && players.getConfig().contains(path + "Losses"));
    }

}
