package me.gipper1998.spleef.game;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.utils.ItemBuilder;
import me.gipper1998.spleef.utils.PotionBuilder;
import me.gipper1998.spleef.utils.TNTBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameTimeEvents {

    public static GameTimeEvents gtm;

    private Random rand;

    private List<Integer> events = new ArrayList<>();

    private String SNOWBALL_ITEM = "";

    private String TNT = "";

    public GameTimeEvents(){
        this.rand = new Random();
        this.SNOWBALL_ITEM = MessageManager.getInstance().getString("snowball");
        this.TNT = "TNT_SPLEEF";
    }

    public static GameTimeEvents getInstance(){
        if (gtm == null){
            gtm = new GameTimeEvents();
        }
        return gtm;
    }

    public void loadEvents(){
        if (ConfigManager.getBoolean("enable_time_events")) {
            for (String key : Spleef.main.config.getConfig().getConfigurationSection("time_events").getKeys(false)) {
                try {
                    events.add(Integer.parseInt(key));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkTime(int currentTime, GameManager gm) {
        if (events.contains(currentTime)) {
            String path = "time_events." + currentTime + ".";
            if (ConfigManager.contains(path + "snowballs")) {
                for (Player p : gm.getPlayersInGame()) {
                    if (ConfigManager.contains(path + "random")){
                        if (ConfigManager.getBoolean(path + "random")){
                            if (rand.nextBoolean()){
                                p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt((path + "snowballs"))).getIs());
                                p.updateInventory();
                            }
                        }
                        else {
                            p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt((path + "snowballs"))).getIs());
                            p.updateInventory();
                        }
                    }
                    else {
                        p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt((path + "snowballs"))).getIs());
                        p.updateInventory();
                    }
                }
            }
            if (ConfigManager.contains(path + "tntfall")) {
                int size = ConfigManager.getInt("tntfall");
                if (size > gm.getPlayersInGame().size()) {
                    for (Player p : gm.getPlayersInGame()){
                        TNTBuilder.getInstance().create(p.getLocation(), TNT);
                    }
                }
                else {
                    for (Player p : gm.getPlayersInGame()){
                        if (rand.nextBoolean()) {
                            TNTBuilder.getInstance().create(p.getLocation(), TNT);
                        }
                    }
                }
            }
            if (ConfigManager.contains(path + "speed")) {
                PotionBuilder potion = new PotionBuilder(PotionEffectType.SPEED, ConfigManager.getInt(path + "speed") * 20, ConfigManager.getInt(path + "amp"));
                if (ConfigManager.contains(path + "random")) {
                    if (ConfigManager.getBoolean(path + "random")) {
                        for (Player p : gm.getPlayersInGame()){
                            if (rand.nextBoolean()){
                                potion.addPlayer(p);
                            }
                        }
                    }
                    else {
                        for (Player p : gm.getPlayersInGame()) {
                            if (rand.nextBoolean()) {
                                potion.addPlayer(p);
                            }
                        }
                    }
                }
                else {
                    for (Player p : gm.getPlayersInGame()) {
                        potion.addPlayer(p);
                    }
                }
            }
            if (ConfigManager.contains(path + "slow")){
                PotionBuilder potion = new PotionBuilder(PotionEffectType.SLOW,ConfigManager.getInt(path + "slow") * 20, ConfigManager.getInt(path + "amp"));
                if (ConfigManager.contains(path + "random")) {
                    if (ConfigManager.getBoolean(path + "random")) {
                        for (Player p : gm.getPlayersInGame()){
                            if (rand.nextBoolean()){
                                potion.addPlayer(p);
                            }
                        }
                    }
                    else {
                        for (Player p : gm.getPlayersInGame()) {
                            if (rand.nextBoolean()) {
                                potion.addPlayer(p);
                            }
                        }
                    }
                }
                else {
                    for (Player p : gm.getPlayersInGame()) {
                        potion.addPlayer(p);
                    }
                }
            }
            if (ConfigManager.contains(path + "jump")){
                PotionBuilder potion = new PotionBuilder(PotionEffectType.JUMP,ConfigManager.getInt(path + "jump") * 20, ConfigManager.getInt(path + "amp"));
                if (ConfigManager.contains(path + "random")) {
                    if (ConfigManager.getBoolean(path + "random")) {
                        for (Player p : gm.getPlayersInGame()){
                            if (rand.nextBoolean()){
                                potion.addPlayer(p);
                            }
                        }
                    }
                    else {
                        for (Player p : gm.getPlayersInGame()) {
                            if (rand.nextBoolean()) {
                                potion.addPlayer(p);
                            }
                        }
                    }
                }
                else {
                    for (Player p : gm.getPlayersInGame()) {
                        potion.addPlayer(p);
                    }
                }
            }
        }
    }
}
