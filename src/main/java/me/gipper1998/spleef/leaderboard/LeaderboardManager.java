package me.gipper1998.spleef.leaderboard;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.PlayerStatManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LeaderboardManager {

    private static HashMap<UUID, Integer> leaderBoard = new HashMap<UUID, Integer>();

    public static String winsPositionPoints(int position){
        sortWins();
        if (leaderBoard.size() == 0 || position == 0 || position > leaderBoard.size()){
            return null;
        }
        return getNumber(position);
    }

    public static String lossesPositionPoints(int position){
        sortLoses();
        if (leaderBoard.size() == 0 || position == 0 || position > leaderBoard.size()){
            return null;
        }
        return getNumber(position);
    }

    public static String winsPositionName(int position){
        sortWins();
        if (leaderBoard.size() == 0 || position == 0 || position > leaderBoard.size()){
            return null;
        }
        return getName(position);
    }

    public static String lossesPositionName(int position){
        sortLoses();
        if (leaderBoard.size() == 0 || position == 0 || position > leaderBoard.size()){
            return null;
        }
        return getName(position);
    }

    private static void sortWins(){
        HashMap<UUID, Integer> temp = new HashMap<>();
        ConfigurationSection playerDataBoard = Spleef.main.playerStats.getConfig().getConfigurationSection("Players");
        if (playerDataBoard == null) {
            return;
        }
        Set<String> keys = playerDataBoard.getKeys(false);
        for (String key : keys) {
            try {
                UUID uuid = UUID.fromString(key);
                temp.put(uuid, PlayerStatManager.getWins(uuid));
            }
            catch (Exception e){
                return;
            }
        }
        leaderBoard = sort(temp);
    }

    private static void sortLoses() {
        HashMap<UUID, Integer> temp = new HashMap<>();
        ConfigurationSection playerDataBoard = Spleef.main.playerStats.getConfig().getConfigurationSection("Players");
        if (playerDataBoard == null) {
            return;
        }
        Set<String> keys = playerDataBoard.getKeys(false);
        for (String key : keys) {
            try {
                UUID uuid = UUID.fromString(key);
                temp.put(uuid, PlayerStatManager.getLosses(uuid));
            } catch (Exception e) {
                return;
            }
        }
        leaderBoard = sort(temp);
    }

    private static HashMap<UUID, Integer> sort(HashMap<UUID, Integer> hm) {
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

    private static String getNumber(int position){
        int size = leaderBoard.size();
        int index = 0;
        for (Map.Entry<UUID, Integer> en : leaderBoard.entrySet()){
            if (position == (size - index)){
                return Integer.toString(en.getValue());
            }
            index++;
        }
        return null;
    }

    private static String getName(int position){
        int size = leaderBoard.size();
        int index = 0;
        for (Map.Entry<UUID, Integer> en : leaderBoard.entrySet()){
            if (position == (size - index)){
                return (Bukkit.getPlayer(en.getKey()).getName());
            }
            index++;
        }
        return null;
    }
}
