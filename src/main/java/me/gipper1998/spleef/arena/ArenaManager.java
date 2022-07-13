package me.gipper1998.spleef.arena;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.edit.ArenaEditTemplate;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.setup.ArenaSetupTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ArenaManager {

    private static HashMap<Arena, GameManager> activeArenas = new HashMap<>();

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

    public static void createArena(ArenaSetupTemplate temp){
        Spleef.main.arenas.getConfig().set("Arenas." + temp.name, temp.name);
        Spleef.main.arenas.getConfig().set("Arenas." + temp.name + ".Minimum", temp.minimum);
        Spleef.main.arenas.getConfig().set("Arenas." + temp.name + ".Maximum", temp.maximum);
        Spleef.main.arenas.saveConfig();
        saveLocation("Arenas." + temp.getName() + ".Arena_Spawn.", temp.arena);
        if (temp.lobby != null) {
            saveLocation("Arenas." + temp.getName() + ".Lobby_Spawn.", temp.lobby);
        }
        saveLocation("Arenas." + temp.getName() + ".Spectate_Spawn.", temp.spectate);
        loadArenas();
    }

    public static void saveEditedArena(ArenaEditTemplate temp) {
        temp.name = temp.name.toUpperCase();
        if (deleteArena(temp.getMainArena().getName())){
            Spleef.main.arenas.getConfig().set("Arenas." + temp.name, temp.name);
            Spleef.main.arenas.getConfig().set("Arenas." + temp.name + ".Minimum", temp.minimum);
            Spleef.main.arenas.getConfig().set("Arenas." + temp.name + ".Maximum", temp.maximum);
            Spleef.main.arenas.saveConfig();
            saveLocation("Arenas." + temp.getName() + ".Arena_Spawn.", temp.arena);
            if (temp.lobby != null) {
                saveLocation("Arenas." + temp.getName() + ".Lobby_Spawn.", temp.lobby);
            }
            saveLocation("Arenas." + temp.getName() + ".Spectate_Spawn.", temp.spectate);
            loadArenas();
        }
        return;
    }

    public static void loadArenas(){
        activeArenas.clear();
        ConfigurationSection section = Spleef.main.arenas.getConfig().getConfigurationSection("Arenas");
        if (section == null){
            return;
        }
        Set<String> keys = section.getKeys(false);
        for (String name : keys){
            int minimum = Spleef.main.arenas.getConfig().getInt("Arenas." + name + ".Minimum");
            int maximum = Spleef.main.arenas.getConfig().getInt("Arenas." + name + ".Maximum");
            Location arena = loadLocation("Arenas." + name + ".Arena_Spawn.");
            Location lobby = loadLocation("Arenas." + name + ".Lobby_Spawn.");
            Location spectate = loadLocation("Arenas." + name + ".Spectate_Spawn.");
            Arena am = new Arena(minimum, maximum, name, arena, lobby, spectate);
            GameManager gm = new GameManager(am);
            activeArenas.put(am, gm);
            Bukkit.getPluginManager().registerEvents(gm, Spleef.main);
        }
    }

    private static void saveLocation(String path, Location location){
        Spleef.main.arenas.getConfig().set(path + "world", location.getWorld().getName());
        Spleef.main.arenas.getConfig().set(path + "x", location.getX());
        Spleef.main.arenas.getConfig().set(path + "y", location.getY());
        Spleef.main.arenas.getConfig().set(path + "z", location.getZ());
        Spleef.main.arenas.getConfig().set(path + "pitch", location.getPitch());
        Spleef.main.arenas.getConfig().set(path + "yaw", location.getYaw());
        Spleef.main.arenas.saveConfig();
    }

    private static Location loadLocation(String path){
        String worldName = Spleef.main.arenas.getConfig().getString(path + "world");
        double x = Spleef.main.arenas.getConfig().getDouble(path + "x");
        double y = Spleef.main.arenas.getConfig().getDouble(path + "y");
        double z = Spleef.main.arenas.getConfig().getDouble(path + "z");
        float pitch = (float) Spleef.main.arenas.getConfig().getDouble(path + "pitch");
        float yaw = (float) Spleef.main.arenas.getConfig().getDouble(path + "yaw");
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void forceQuitArenas() {
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            gm.removeEverybody();
        }
    }
}
