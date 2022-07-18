package me.gipper1998.spleef.arena;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.edit.ArenaEditTemplate;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.setup.ArenaSetupTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    public static ArenaManager am;

    private FileConfiguration arenas;

    public ArenaManager(){
        this.arenas = Spleef.main.arenas.getConfig();
    }

    public static ArenaManager getInstance(){
        if (am == null){
            am = new ArenaManager();
        }
        return am;
    }
    private HashMap<Arena, GameManager> activeArenas = new HashMap<>();

    public void shutGamesDown(){
        if (activeArenas.isEmpty()){
            return;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            gm.removeEverybody();
        }
    }

    public List<String> getArenaNames(){
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

    public GameManager findGame(String name){
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

    public GameManager findPlayerInGame(Player p){
        if (activeArenas.isEmpty()){
            return null;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            if (gm.getTotalPlayers().contains(p)){
                return gm;
            }
        }
        return null;
    }

    public Arena findArena(String name){
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

    public boolean deleteArena(String name){
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

    public void forceQuitArenas() {
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            GameManager gm = set.getValue();
            gm.removeEverybody();
        }
    }

    public void createArena(ArenaSetupTemplate temp){
        arenas.set("Arenas." + temp.getName(), temp.getName());
        arenas.set("Arenas." + temp.getName() + ".Minimum", temp.getMinimum());
        arenas.set("Arenas." + temp.getName() + ".Maximum", temp.getMaximum());
        Spleef.main.arenas.saveConfig();
        saveLocation("Arenas." + temp.getName() + ".Arena_Spawn.", temp.getArena());
        saveLocation("Arenas." + temp.getName() + ".Lobby_Spawn.", temp.getLobby());
        saveLocation("Arenas." + temp.getName() + ".Spectate_Spawn.", temp.getSpectate());
        loadArenas();
    }

    public void saveEditedArena(ArenaEditTemplate temp, Arena prevArena) {
        String newName = temp.getName().toUpperCase();
        String prevName = prevArena.getName().toUpperCase();
        String name = prevName;
        if (!(newName.equalsIgnoreCase(prevName))) {
            deleteArena(prevName);
            name = newName;
            arenas.set("Arenas." + name, name);
            Spleef.main.arenas.saveConfig();
        }
        arenas.set("Arenas." + name + ".Minimum", temp.getMinimum());
        arenas.set("Arenas." + name + ".Maximum", temp.getMaximum());
        Spleef.main.arenas.saveConfig();
        saveLocation("Arenas." + name + ".Arena_Spawn.", temp.getArena());
        saveLocation("Arenas." + name + ".Lobby_Spawn.", temp.getLobby());
        saveLocation("Arenas." + name + ".Spectate_Spawn.", temp.getSpectate());
        loadArenas();
        return;
    }

    public void loadArenas(){
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

    private void saveLocation(String path, Location location){
        arenas.set(path + "world", location.getWorld().getName());
        arenas.set(path + "x", location.getX());
        arenas.set(path + "y", location.getY());
        arenas.set(path + "z", location.getZ());
        arenas.set(path + "pitch", location.getPitch());
        arenas.set(path + "yaw", location.getYaw());
        Spleef.main.arenas.saveConfig();
    }

    private Location loadLocation(String path){
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
