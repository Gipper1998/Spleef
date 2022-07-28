package me.gipper1998.spleef.leaderboard;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.PlayerStatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class LeaderboardManager {

    private static LeaderboardManager lm;

    private HashMap<UUID, Integer> winsLeaderboard = new HashMap<>();
    private HashMap<UUID, Integer> lossesLeaderboard = new HashMap<>();
    private FileConfiguration players;

    public LeaderboardManager(){
        this.players = Spleef.main.playerStats.getConfig();
    }

    public static LeaderboardManager getInstance(){
        if (lm == null){
            lm = new LeaderboardManager();
        }
        return lm;
    }

    public List<String> getPlayerNames(){
        List<String> temp = new ArrayList<>();
        if (players.getConfigurationSection("Players") != null){
            for (String key : players.getConfigurationSection("Players").getKeys(false)){
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

    public String winsPositionPoints(int position){
        sortWins();
        if (winsLeaderboard.size() == 0 || position == 0 || position > winsLeaderboard.size()){
            return "";
        }
        int index = 0;
        for (Map.Entry<UUID, Integer> entry : winsLeaderboard.entrySet()){
            if (position == (winsLeaderboard.size() - index)){
                return Integer.toString(entry.getValue());
            }
            index++;
        }
        return "";
    }

    public String lossesPositionPoints(int position){
        sortLoses();
        if (lossesLeaderboard.size() == 0 || position == 0 || position > lossesLeaderboard.size()){
            return "";
        }
        int index = 0;
        for (Map.Entry<UUID, Integer> entry : lossesLeaderboard.entrySet()){
            if (position == (lossesLeaderboard.size() - index)){
                return Integer.toString(entry.getValue());
            }
            index++;
        }
        return "";
    }

    public String winsPositionName(int position){
        sortWins();
        if (winsLeaderboard.size() == 0 || position == 0 || position > winsLeaderboard.size()){
            return "";
        }
        int index = 0;
        for (Map.Entry<UUID, Integer> entry : winsLeaderboard.entrySet()){
            if (position == (winsLeaderboard.size() - index)){
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                return player.getName();
            }
            index++;
        }
        return "";
    }

    public String lossesPositionName(int position){
        sortLoses();
        if (lossesLeaderboard.size() == 0 || position == 0 || position > lossesLeaderboard.size()){
            return "";
        }
        int index = 0;
        for (Map.Entry<UUID, Integer> entry : lossesLeaderboard.entrySet()){
            if (position == (lossesLeaderboard.size() - index)){
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                return player.getName();
            }
            index++;
        }
        return "";
    }

    private void sortWins(){
        HashMap<UUID, Integer> temp = new HashMap<>();
        if (players.getConfigurationSection("Players") != null){
            for (String key : players.getConfigurationSection("Players").getKeys(false)){
                try {
                    UUID uuid = UUID.fromString(key);
                    temp.put(uuid, PlayerStatManager.getInstance().getWins(uuid));
                }
                catch (Exception e){
                    return;
                }
            }
        }
        winsLeaderboard = sort(temp);
    }

    private void sortLoses() {
        HashMap<UUID, Integer> temp = new HashMap<>();
        if (players.getConfigurationSection("Players") != null){
            for (String key : players.getConfigurationSection("Players").getKeys(false)){
                try {
                    UUID uuid = UUID.fromString(key);
                    temp.put(uuid, PlayerStatManager.getInstance().getLosses(uuid));
                }
                catch (Exception e){
                    return;
                }
            }
        }
        lossesLeaderboard = sort(temp);
    }

    private HashMap<UUID, Integer> sort(HashMap<UUID, Integer> hm) {
        List<Map.Entry<UUID, Integer> > list = new LinkedList<Map.Entry<UUID, Integer> >(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<UUID, Integer> >() {
            public int compare(Map.Entry<UUID, Integer> o1, Map.Entry<UUID, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<UUID, Integer> temp = new LinkedHashMap<UUID, Integer>();
        for (Map.Entry<UUID, Integer> entry : list) {
            temp.put(entry.getKey(), entry.getValue());
        }
        return temp;
    }

}
