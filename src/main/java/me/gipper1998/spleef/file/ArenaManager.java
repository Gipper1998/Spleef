package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.game.Status;
import me.gipper1998.spleef.setup.ArenaSetupTemplate;
import me.gipper1998.spleef.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    public static ArenaManager am;

    private HashMap<Arena, GameManager> activeArenas = new HashMap<>();

    private FileManager arenas;

    public ArenaManager(){
        this.arenas = new FileManager("arenas.yml");
    }

    public static ArenaManager getInstance(){
        if (am == null){
            am = new ArenaManager();
        }
        return am;
    }

    public void reloadArenas(){
        shutGamesDown();
        arenas.reloadConfig();
        loadArenas();
    }
    public void shutGamesDown(){
        if (activeArenas.isEmpty()){
            return;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            set.getValue().removeEverybody();
            set.getValue().setStatus(Status.STOP);
        }
    }

    public List<String> getArenaNames(){
        if (activeArenas.isEmpty()){
            return null;
        }
        List<String> arenaNames = new ArrayList<>();
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            arenaNames.add(set.getKey().getName());
        }
        return arenaNames;
    }

    public GameManager findGame(String name){
        name = name.toUpperCase();
        if (activeArenas.isEmpty()){
            return null;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            if (set.getKey().getName().equalsIgnoreCase(name)){
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
            if (set.getValue().getTotalPlayers().contains(p)){
                return set.getValue();
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
            if (set.getKey().getName().equalsIgnoreCase(name)){
                return set.getKey();
            }
        }
        return null;
    }

    public boolean disableArena(Player p, Arena arena){
        if (activeArenas.isEmpty()){
            return false;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            if (set.getKey().equals(arena)) {
                if (set.getValue().getStatus() == Status.WAIT) {
                    set.getValue().removeEverybody();
                    set.getValue().setStatus(Status.STOP);
                    MessageManager.getInstance().sendMessage("arena_disabled", p);
                    return true;
                }
            }
        }
        MessageManager.getInstance().sendMessage("arena_already_disabled", p);
        return false;
    }

    public boolean enableArena(Player p, Arena arena){
        if (activeArenas.isEmpty()){
            return false;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            if (set.getKey().equals(arena)){
                if (set.getValue().getStatus() == Status.STOP) {
                    set.getValue().setStatus(Status.WAIT);
                    MessageManager.getInstance().sendMessage("arena_enabled", p);
                    return true;
                }
            }
        }
        MessageManager.getInstance().sendMessage("arena_already_enabled", p);
        return false;
    }

    public boolean deleteArena(String name){
        name = name.toUpperCase();
        if (activeArenas.isEmpty()){
            return false;
        }
        for (Map.Entry<Arena, GameManager> set : activeArenas.entrySet()){
            if (set.getKey().getName().equalsIgnoreCase(name)){
                arenas.getConfig().set("Arenas." + name, null);
                SignManager.getInstance().deleteSigns(set.getValue());
                arenas.saveConfig();
                loadArenas();
                return true;
            }
        }
        return false;
    }

    public void createArena(ArenaSetupTemplate temp){
        arenas.getConfig().set("Arenas." + temp.getName(), temp.getName());
        arenas.getConfig().set("Arenas." + temp.getName() + ".Minimum", temp.getMinimum());
        arenas.getConfig().set("Arenas." + temp.getName() + ".Maximum", temp.getMaximum());
        arenas.saveConfig();
        saveLocation("Arenas." + temp.getName() + ".Arena_Spawn.", temp.getArena());
        saveLocation("Arenas." + temp.getName() + ".Lobby_Spawn.", temp.getLobby());
        saveLocation("Arenas." + temp.getName() + ".Spectate_Spawn.", temp.getSpectate());
        loadArenas();
    }

    public void loadArenas(){
        activeArenas.clear();
        if (arenas.getConfig().getConfigurationSection("Arenas") != null) {
            for (String name : arenas.getConfig().getConfigurationSection("Arenas").getKeys(false)) {
                int minimum = arenas.getConfig().getInt("Arenas." + name + ".Minimum");
                int maximum = arenas.getConfig().getInt("Arenas." + name + ".Maximum");
                Location arena = loadLocation("Arenas." + name + ".Arena_Spawn.");
                Location lobby = loadLocation("Arenas." + name + ".Lobby_Spawn.");
                Location spectate = loadLocation("Arenas." + name + ".Spectate_Spawn.");
                Arena am = new Arena(name, arena, lobby, spectate, minimum, maximum);
                GameManager gm = new GameManager(am);
                activeArenas.put(am, gm);
                gm.setStatus(Status.WAIT);
                Spleef.main.getServer().getPluginManager().registerEvents(gm, Spleef.main);
            }
        }
    }

    private void saveLocation(String path, Location location){
        arenas.getConfig().set(path + "world", location.getWorld().getName());
        arenas.getConfig().set(path + "x", location.getX());
        arenas.getConfig().set(path + "y", location.getY());
        arenas.getConfig().set(path + "z", location.getZ());
        arenas.getConfig().set(path + "pitch", location.getPitch());
        arenas.getConfig().set(path + "yaw", location.getYaw());
        arenas.saveConfig();
    }

    private Location loadLocation(String path){
        String worldName = arenas.getConfig().getString(path + "world");
        double x = arenas.getConfig().getDouble(path + "x");
        double y = arenas.getConfig().getDouble(path + "y");
        double z = arenas.getConfig().getDouble(path + "z");
        float pitch = (float) arenas.getConfig().getDouble(path + "pitch");
        float yaw = (float) arenas.getConfig().getDouble(path + "yaw");
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }

}
