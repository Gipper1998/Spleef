package me.gipper1998.spleef.game;

import lombok.Getter;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager extends BukkitRunnable implements Listener {

    private Arena arena;
    private int waitTime = 10;
    private int gameTime = 120;
    private int resetDelay = 5;
    private int startDelay = 5;
    private int winnerDelay = 5;
    public int currentTime = 0;

    private String EXIT_ITEM = "";
    private String GOLD_SPADE_ITEM = "";
    private String DIAMOND_SPADE_ITEM = "";
    private String SNOWBALL_ITEM = "";

    private HashMap<Player, GameStoreItems> playersStuff = new HashMap<Player, GameStoreItems>();
    private HashMap<Material, Location> brokenBlocks = new HashMap<>();

    @Getter
    private List<Player> playersInGame = new ArrayList<>();

    @Getter
    private List<Player> spectators = new ArrayList<>();

    @Getter
    private Status status = Status.WAIT;

    public GameManager(Arena arena){
        Spleef.main.getServer().getPluginManager().registerEvents(this, Spleef.main);
        this.arena = arena;
        this.waitTime = ConfigManager.getInt("waiting_time");
        this.gameTime = ConfigManager.getInt("total_game_time");
        this.EXIT_ITEM = MessageManager.getString("leave_item");
        this.GOLD_SPADE_ITEM = MessageManager.getString("gold_shovel");
        this.DIAMOND_SPADE_ITEM = MessageManager.getString("diamond_shovel");
        this.SNOWBALL_ITEM = MessageManager.getString("snowball");
        this.runTaskTimer(Spleef.main, 20L, 20L);
    }

    @Override
    public void run(){
        switch (status){
            case WAIT: {
                waitTask();
                break;
            }
            case DELAYSTART: {
                delayStart();
                break;
            }
            case GAME: {
                playGame();
                break;
            }
            case RESTARTING: {
                resetArena();
                break;
            }
        }
    }

    private void waitTask(){
        if (playersInGame.size() >= arena.getMinimum()){
            if (currentTime <= 0){
                for (Player p : playersInGame){
                    p.teleport(arena.getArena());
                    p.hidePlayer(Spleef.main, p);
                }
                currentTime = startDelay;
                status = Status.DELAYSTART;
            }
            else {
                currentTime--;
            }
        }
        else {
            currentTime = waitTime;
        }
    }

    private void delayStart(){
        if (currentTime >= 0){
            for (Player p : playersInGame){
                giveItems(p);
                p.showPlayer(Spleef.main, p);
            }
            currentTime = gameTime;
            status = Status.GAME;
            return;
        }
        else {
            currentTime--;
        }
    }

    private void playGame(){
        if (playersInGame.size() > 1){
            if (currentTime <= 0){
                for (Player p : playersInGame){
                  GameStoreItems gmi = playersStuff.get(p);
                  gmi.giveBackItems();
                }
                playersInGame.clear();
                currentTime = resetDelay;
                status = Status.RESTARTING;
                return;
            }
            else {
                for (Player p : playersInGame){
                    if (loseFloor(p)){
                        killPlayer(p);
                    }
                }
                currentTime--;
            }
        }
        else {
            Player winner = playersInGame.get(0);
            String name = winner.getName();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Spleef.main, new Runnable() {
                @Override
                public void run() {
                    PlayerStatManager.addWinPoint(winner.getUniqueId());
                    status = Status.RESTARTING;
                    removeEverybody();
                }
            }, 20L * winnerDelay);
        }
    }

    private void resetArena(){
        if (resetDelay <= 0) {
            for (Map.Entry<Material, Location> set : brokenBlocks.entrySet()) {
                set.getValue().getBlock().setType(set.getKey());
            }
            currentTime = waitTime;
            status = Status.WAIT;
            return;
        }
        else {
            currentTime--;
        }
    }

    private void giveItems(Player p){
        if (p.hasPermission("Spleef.diamond")) {
            p.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SHOVEL, DIAMOND_SPADE_ITEM).getIs());
        }
        else {
            p.getInventory().setItem(0, new ItemBuilder(Material.GOLDEN_SHOVEL, GOLD_SPADE_ITEM).getIs());
        }
        if (ConfigManager.getBoolean("give_snowballs_on_begin")) {
            p.getInventory().setItem(1, new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt("give_snowballs_on_begin")).getIs());
        }
    }

    private boolean bothClicks(PlayerInteractEvent event){
        return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean loseFloor(Player p){
        return (p.getLocation().getBlock().getType().name().contains("WATER") || p.getLocation().getBlock().getType().name().contains("LAVA"));
    }

    private void killPlayer(Player p){
        playersInGame.remove(p);
        spectators.add(p);
        p.teleport(arena.getSpectate());
        p.setGameMode(GameMode.SPECTATOR);
        PlayerStatManager.addLosePoint(p.getUniqueId());
    }

    public boolean addPlayer(Player p){
        if (status == Status.WAIT) {
            if (playersInGame.size() >= arena.getMaximum()) {
                return false;
            }
            for (Player player : playersInGame) {
                MessageManager.getString("player_join", p.getName(), player);
            }
            playersInGame.add(p);
            GameStoreItems gmi = new GameStoreItems(p);
            playersStuff.put(p, gmi);
            p.getInventory().setItem(8, new ItemBuilder(ConfigManager.getBlock("in_lobby.leave"), EXIT_ITEM).getIs());
            p.updateInventory();
            p.teleport(arena.getLobby());
            return true;
        }
        return false;
    }

    public void removePlayer(Player p) {
        playersInGame.remove(p);
        for (Player player : playersInGame) {
            MessageManager.getString("player_quit", p.getName(), player);
        }
        GameStoreItems gmi = playersStuff.get(p);
        gmi.giveBackItems();
        MessageManager.getString("player_success_quit", arena.getName(), p);
        playersStuff.remove(p);
        if (status == Status.GAME){
            PlayerStatManager.addLosePoint(p.getUniqueId());
        }
    }

    public void removeEverybody(){
        List<Player> totalPlayers = new ArrayList<>();
        for (Player p : playersInGame){
            totalPlayers.add(p);
        }
        for (Player p : spectators){
            totalPlayers.add(p);
        }
        playersInGame.clear();
        spectators.clear();
        for (Player p : totalPlayers){
            GameStoreItems gmi = playersStuff.get(p);
            gmi.giveBackItems();
        }
        playersStuff.clear();
    }

    @EventHandler
    public void onPlayerInteractExit(PlayerInteractEvent event){
        if (playersInGame.contains(event.getPlayer())) {
            if (bothClicks(event) && event.getMaterial() == Material.BARRIER) {
                removePlayer(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (playersInGame.contains(event.getPlayer()) && event.getBlock().getType() == Material.SNOW_BLOCK){
            if (status == Status.GAME){
                event.setDropItems(false);
                brokenBlocks.put(Material.SNOW_BLOCK, event.getBlock().getLocation());
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        if (playersInGame.contains(event.getEntity().getShooter() instanceof Player) && status == Status.GAME){
            if (event.getEntity().getLocation().getBlock().getType() == Material.SNOW_BLOCK){
                brokenBlocks.put(Material.SNOW_BLOCK, event.getEntity().getLocation());
                Block b = event.getEntity().getLocation().getBlock();
                b.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void foodLevel(FoodLevelChangeEvent event){
        if (playersInGame.contains(event.getEntity() instanceof Player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        if (playersInGame.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event){
        if (playersInGame.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent event){
        if (playersInGame.contains(event.getWhoClicked() instanceof Player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFly(PlayerToggleFlightEvent event){
        if (playersInGame.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        if (playersInGame.contains(event.getPlayer()) || spectators.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (playersInGame.contains(event.getPlayer()) || spectators.contains(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }
}
