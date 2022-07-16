package me.gipper1998.spleef.arena;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.edit.ArenaEditTemplate;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.setup.ArenaSetupTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ArenaManager {

    private static HashMap<Arena, GameManager> activeArenas = new HashMap<>();

    public static void shutGamesDown(){
        if (activeArenas.isEmpty()){
            return;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            gm.removeEverybody();
        }
    }

    public static List<String> getArenaNames(){
        if (activeArenas.isEmpty()){
            return null;
        }
        List<String> arenaNames = new ArrayList<>();
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            Arena am = set.getKey();
            arenaNames.add(am.getName());
        }
        return arenaNames;
    }

    public static GameManager findGame(String name){
        name = name.toUpperCase();
        if (activeArenas.isEmpty()){
            return null;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            Arena am = set.getKey();
            if (am.getName().equalsIgnoreCase(name)){
                return set.getValue();
            }
        }
        return null;
    }

    public static Arena findArena(String name){
        name = name.toUpperCase();
        if (activeArenas.isEmpty()){
            return null;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            Arena am = set.getKey();
            if (am.getName().equalsIgnoreCase(name)){
                return am;
            }
        }
        return null;
    }

    public static boolean deleteArena(String name){
        name = name.toUpperCase();
        if (activeArenas.isEmpty()){
            return false;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            Arena am = set.getKey();
            if (am.getName().equalsIgnoreCase(name)){
                Spleef.main.arenas.getConfig().set("Arenas." + name, null);
                Spleef.main.arenas.saveConfig();
                loadArenas();
                return true;
            }
        }
        return false;
    }

    public static void forceQuitArenas() {
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            gm.removeEverybody();
        }
    }

    public static void createArena(ArenaSetupTemplate temp){
        FileConfiguration arenas = Spleef.main.arenas.getConfig();
        arenas.set("Arenas." + temp.name, temp.name);
        arenas.set("Arenas." + temp.name + ".Minimum", temp.minimum);
        arenas.set("Arenas." + temp.name + ".Maximum", temp.maximum);
        Spleef.main.arenas.saveConfig();
        saveLocation("Arenas." + temp.getName() + ".Arena_Spawn.", temp.arena);
        if (temp.lobby != null) {
            saveLocation("Arenas." + temp.getName() + ".Lobby_Spawn.", temp.lobby);
        }
        saveLocation("Arenas." + temp.getName() + ".Spectate_Spawn.", temp.spectate);
        loadArenas();
    }

    public static void saveEditedArena(ArenaEditTemplate temp, Arena prevArena) {
        FileConfiguration arenas = Spleef.main.arenas.getConfig();
        String newName = temp.getName().toUpperCase();
        String prevName = prevArena.getName().toUpperCase();
        String name = prevName;
        if (!(newName.equalsIgnoreCase(prevName))) {
            deleteArena(prevName);
            name = newName;
            arenas.set("Arenas." + name, name);
            Spleef.main.arenas.saveConfig();
        }
        arenas.set("Arenas." + name + ".Minimum", temp.minimum);
        arenas.set("Arenas." + name + ".Maximum", temp.maximum);
        Spleef.main.arenas.saveConfig();
        saveLocation("Arenas." + name + ".Arena_Spawn.", temp.arena);
        if (temp.lobby != null) {
            saveLocation("Arenas." + name + ".Lobby_Spawn.", temp.lobby);
        }
        saveLocation("Arenas." + name + ".Spectate_Spawn.", temp.spectate);
        loadArenas();
        return;
    }

    public static void loadArenas(){
        FileConfiguration arenas = Spleef.main.arenas.getConfig();
        activeArenas.clear();
        if (arenas.getConfigurationSection("Arenas") != null) {
            for (String name : arenas.getConfigurationSection("Arenas").getKeys(false)) {
                int minimum = arenas.getInt("Arenas." + name + ".Minimum");
                int maximum = arenas.getInt("Arenas." + name + ".Maximum");
                Location arena = loadLocation("Arenas." + name + ".Arena_Spawn.");
                Location lobby = loadLocation("Arenas." + name + ".Lobby_Spawn.");
                Location spectate = loadLocation("Arenas." + name + ".Spectate_Spawn.");
                Arena am = new Arena(minimum, maximum, name, arena, lobby, spectate);
                GameManager gm = new GameManager(am);
                activeArenas.put(am, gm);
                Bukkit.getPluginManager().registerEvents(gm, Spleef.main);
            }
        }
    }

    private static void saveLocation(String path, Location location){
        FileConfiguration arenas = Spleef.main.arenas.getConfig();
        arenas.set(path + "world", location.getWorld().getName());
        arenas.set(path + "x", location.getX());
        arenas.set(path + "y", location.getY());
        arenas.set(path + "z", location.getZ());
        arenas.set(path + "pitch", location.getPitch());
        arenas.set(path + "yaw", location.getYaw());
        Spleef.main.arenas.saveConfig();
    }

    private static Location loadLocation(String path){
        FileConfiguration arenas = Spleef.main.arenas.getConfig();
        String worldName = arenas.getString(path + "world");
        double x = arenas.getDouble(path + "x");
        double y = arenas.getDouble(path + "y");
        double z = arenas.getDouble(path + "z");
        float pitch = (float) arenas.getDouble(path + "pitch");
        float yaw = (float) arenas.getDouble(path + "yaw");
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }

}
