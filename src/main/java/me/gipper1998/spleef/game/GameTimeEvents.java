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
        if (ConfigManager.getInstance().getBoolean("enable_time_events")) {
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
            if (ConfigManager.getInstance().contains(path + "snowballs")) {
                for (Player p : gm.getPlayersInGame()) {
                    if (ConfigManager.getInstance().contains(path + "random")){
                        if (ConfigManager.getInstance().getBoolean(path + "random")){
                            if (rand.nextBoolean()){
                                p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInstance().getInt((path + "snowballs"))).getIs());
                                p.updateInventory();
                            }
                        }
                        else {
                            p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInstance().getInt((path + "snowballs"))).getIs());
                            p.updateInventory();
                        }
                    }
                    else {
                        p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInstance().getInt((path + "snowballs"))).getIs());
                        p.updateInventory();
                    }
                }
            }
            if (ConfigManager.getInstance().contains(path + "tntfall")) {
                int size = ConfigManager.getInstance().getInt("tntfall");
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
            if (ConfigManager.getInstance().contains(path + "speed")) {
                PotionBuilder potion = new PotionBuilder(PotionEffectType.SPEED, ConfigManager.getInstance().getInt(path + "speed") * 20, ConfigManager.getInstance().getInt(path + "amp"));
                if (ConfigManager.getInstance().contains(path + "random")) {
                    if (ConfigManager.getInstance().getBoolean(path + "random")) {
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
            if (ConfigManager.getInstance().contains(path + "slow")){
                PotionBuilder potion = new PotionBuilder(PotionEffectType.SLOW,ConfigManager.getInstance().getInt(path + "slow") * 20, ConfigManager.getInstance().getInt(path + "amp"));
                if (ConfigManager.getInstance().contains(path + "random")) {
                    if (ConfigManager.getInstance().getBoolean(path + "random")) {
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
            if (ConfigManager.getInstance().contains(path + "jump")){
                PotionBuilder potion = new PotionBuilder(PotionEffectType.JUMP,ConfigManager.getInstance().getInt(path + "jump") * 20, ConfigManager.getInstance().getInt(path + "amp"));
                if (ConfigManager.getInstance().contains(path + "random")) {
                    if (ConfigManager.getInstance().getBoolean(path + "random")) {
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
