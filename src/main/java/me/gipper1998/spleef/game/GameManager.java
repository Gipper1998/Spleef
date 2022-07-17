package me.gipper1998.spleef.game;

import lombok.Getter;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.item.FireworkBuilder;
import me.gipper1998.spleef.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager extends BukkitRunnable implements Listener {

    @Getter
    private Arena arena;

    private int waitTime = 10;
    private int gameTime = 120;
    private int startDelay = 5;
    private int winnerDelay = 5;
    public int currentTime = 0;

    private String EXIT_ITEM = "";
    private String GOLD_SPADE_ITEM = "";
    private String DIAMOND_SPADE_ITEM = "";
    private String SNOWBALL_ITEM = "";
    private String TNT = "";

    private HashMap<Player, GameStoreItems> playersStuff = new HashMap<Player, GameStoreItems>();
    private HashMap<Material, Location> brokenBlocks = new HashMap<>();

    @Getter
    private List<Player> playersInGame = new ArrayList<>();
    @Getter
    private List<Player> spectators = new ArrayList<>();
    @Getter
    private List<Player> totalPlayers = new ArrayList<>();
    private List<Integer> events = new ArrayList<>();

    @Getter
    private Status status = Status.WAIT;

    public GameManager(Arena arena){
        Spleef.main.getServer().getPluginManager().registerEvents(this, Spleef.main);
        this.arena = arena;
        this.waitTime = ConfigManager.getInt("waiting_time");
        this.gameTime = ConfigManager.getInt("total_game_time");
        this.EXIT_ITEM = MessageManager.getInstance().getString("leave_item");
        this.GOLD_SPADE_ITEM = MessageManager.getInstance().getString("gold_shovel");
        this.DIAMOND_SPADE_ITEM = MessageManager.getInstance().getString("diamond_shovel");
        this.SNOWBALL_ITEM = MessageManager.getInstance().getString("snowball");
        this.TNT = "TNT_SPLEEF";
        loadEvents();
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
            case WINNER: {
                winnerShowOff();
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
            if (currentTime == 60 || currentTime == 50 || currentTime == 40 || currentTime == 30 || currentTime == 20 || currentTime == 10){
                for (Player p : playersInGame) {
                    MessageManager.getInstance().sendNumberMessage("starting_game", currentTime, p);
                }
            }
            if (currentTime <= 5){
                for (Player p : playersInGame) {
                    MessageManager.getInstance().sendNumberMessage("starting_game", currentTime, p);
                }
            }
            currentTime--;
        }
        else {
            currentTime = waitTime;
        }
    }

    private void delayStart(){
        if (currentTime <= 0){
            for (Player p : playersInGame){
                giveItems(p);
                p.showPlayer(Spleef.main, p);
                MessageManager.getInstance().sendMessage("game_start", p);
                p.setGameMode(GameMode.SURVIVAL);
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
                for (Player p : totalPlayers){
                    MessageManager.getInstance().sendMessage("arena_no_winner", p);
                }
                removeEverybody();
                resetArena();
                currentTime = waitTime;
                status = Status.WAIT;
                return;
            }
            else {
                for (Player p : playersInGame){
                    if (loseFloor(p)){
                        killPlayer(p);
                    }
                }
                checkTime();
                currentTime--;
            }
        }
        if (playersInGame.size() == 1) {
            currentTime = winnerDelay;
            status = Status.WINNER;
        }
    }

    private void winnerShowOff(){
        Player winner = playersInGame.get(0);
        if (currentTime == winnerDelay) {
            FireworkBuilder fb = new FireworkBuilder(arena.getArena(), 25, "aqua", 2, 5);
            fb.launch();
            for (Player p : totalPlayers) {
                MessageManager.getInstance().sendPlayerNameMessage("player_winner", winner, p);
            }
        }
        if (currentTime <= 0){
            PlayerStatManager.getInstance().addWinPoint(winner.getUniqueId());
            removeEverybody();
            resetArena();
            currentTime = waitTime;
            status = Status.WAIT;
        }
        else {
            currentTime--;
        }
    }

    private void resetArena(){
        for (Map.Entry<Material, Location> set : brokenBlocks.entrySet()) {
            Block b = set.getValue().getBlock();
            b.setType(set.getKey());
        }
    }

    private void giveItems(Player p){
        if (p.hasPermission("Spleef.diamond")) {
            p.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SHOVEL, DIAMOND_SPADE_ITEM).getIs());
        }
        else {
            p.getInventory().setItem(0, new ItemBuilder(Material.GOLDEN_SHOVEL, GOLD_SPADE_ITEM).getIs());
        }
        if (ConfigManager.getBoolean("give_snowballs_on_start")) {
            p.getInventory().setItem(1, new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt("give_snowballs_on_start.amount")).getIs());
        }
    }

    private boolean bothClicks(PlayerInteractEvent event){
        return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean loseFloor(Player p){
        return (p.getLocation().getBlock().getType().name().contains("WATER") || p.getLocation().getBlock().getType().name().contains("LAVA"));
    }

    private void killPlayer(Player p){
        for (Player player : totalPlayers){
            MessageManager.getInstance().sendPlayerNameMessage("player_died", p, player);
        }
        playersInGame.remove(p);
        spectators.add(p);
        p.teleport(arena.getSpectate());
        p.setGameMode(GameMode.SPECTATOR);
        PlayerStatManager.getInstance().addLosePoint(p.getUniqueId());
        for (PotionEffect effect : p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
    }

    private void checkTime() {
        if (events.contains(currentTime)) {
            String path = "time_events." + currentTime + ".";
            if (ConfigManager.contains(path + "snowballs")) {
                for (Player p : playersInGame) {
                    p.getInventory().addItem(new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInt((path + "snowballs"))).getIs());
                    p.updateInventory();
                }
            }
            if (ConfigManager.contains(path + "tntfall")) {
                int size = ConfigManager.getInt("tntfall");
                if (size > playersInGame.size()) {
                    for (Player p : playersInGame){
                        ItemBuilder temp = new ItemBuilder(p, TNT);
                    }
                }
                else {
                    int[] playerTarget = {0};
                    int index = 0;
                    int temp = 0;
                    Random rand = new Random();
                    for (int i = 0; i < size; i++) {
                        do {
                            temp = rand.nextInt(size);
                            for (int j = 0; j < size; j++) {
                                if (temp == playerTarget[j]) {
                                    temp = 0;
                                    break;
                                }
                            }
                        } while (temp == 0);
                        playerTarget[index] = temp;
                        index++;
                    }
                    for (int i = 0; i < size; i++){
                        Player p = playersInGame.get(playerTarget[i]);
                        ItemBuilder t = new ItemBuilder(p, TNT);
                    }
                }
            }
            if (ConfigManager.contains(path + "speed")){
                for (Player p : playersInGame){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,ConfigManager.getInt(path + "speed"), 3));
                }
            }
            if (ConfigManager.contains(path + "slow")){
                for (Player p : playersInGame){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,ConfigManager.getInt(path + "slow"), 3));
                }
            }
            if (ConfigManager.contains(path + "jump")){
                for (Player p : playersInGame){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,ConfigManager.getInt(path + "jump"), 3));
                }
            }
        }
    }

    private void loadEvents(){
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

    public void addPlayer(Player p){
        if (totalPlayers.contains(p)){
            MessageManager.getInstance().sendMessage("player_already_joined", p);
            return;
        }
        if (status == Status.WAIT) {
            if (playersInGame.size() >= arena.getMaximum()) {
                MessageManager.getInstance().sendMessage("arena_full", p);
                return;
            }
            for (Player player : playersInGame) {
                MessageManager.getInstance().sendPlayerNameMessage("player_join", p, player);
            }
            for (PotionEffect effect : p.getActivePotionEffects()){
                p.removePotionEffect(effect.getType());
            }
            MessageManager.getInstance().sendArenaNameMessage("player_success_join", this, p);
            playersInGame.add(p);
            totalPlayers.add(p);
            GameStoreItems gmi = new GameStoreItems(p);
            playersStuff.put(p, gmi);
            p.teleport(arena.getLobby());
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().setItem(8, new ItemBuilder(ConfigManager.getBlock("in_lobby.leave"), EXIT_ITEM).getIs());
            p.updateInventory();
        }
        else {
            MessageManager.getInstance().sendMessage("arena_in-game", p);
        }
    }

    public void removePlayer(Player p) {
        if (totalPlayers.contains(p)) {
            if (status == Status.WAIT) {
                GameStoreItems gmi = playersStuff.get(p);
                playersInGame.remove(p);
                totalPlayers.remove(p);
                for (Player player : playersInGame) {
                    MessageManager.getInstance().sendPlayerNameMessage("player_quit", p, player);
                }
                gmi.giveBackItems();
                playersStuff.remove(p);
                MessageManager.getInstance().sendArenaNameMessage("player_success_quit", this, p);
            } else {
                MessageManager.getInstance().sendArenaNameMessage("player_success_quit", this, p);
                if (spectators.contains(p)) {
                    spectators.remove(p);
                }
                if (spectators.contains(p)) {
                    playersInGame.remove(p);
                }
                totalPlayers.remove(p);
                GameStoreItems gmi = playersStuff.get(p);
                gmi.giveBackItems();
                playersStuff.remove(p);
                PlayerStatManager.getInstance().addLosePoint(p.getUniqueId());
            }
        }
        else {
            MessageManager.getInstance().sendMessage("player_not_in_game", p);
        }
    }

    public void removeEverybody(){
        for (Player p : totalPlayers){
            GameStoreItems gmi = playersStuff.get(p);
            gmi.giveBackItems();
        }
        playersInGame.clear();
        spectators.clear();
        totalPlayers.clear();
        playersStuff.clear();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (status == Status.GAME && event.getEntity().getCustomName().equals(TNT)) {
            if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
                List destroyed = event.blockList();
                Iterator it = destroyed.iterator();
                while (it.hasNext()) {
                    Block block = (Block) it.next();
                    if (!(block.getType() == Material.SNOW_BLOCK)) {
                        brokenBlocks.put(Material.SNOW_BLOCK, block.getLocation());
                        it.remove();
                    }
                }
            }
        }
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
        Block b = event.getBlock();
        if (playersInGame.contains(event.getPlayer()) && b.getType() == Material.SNOW_BLOCK){
            if (status == Status.GAME){
                event.setDropItems(false);
                brokenBlocks.put(b.getType(), b.getLocation());
                b.setType(Material.AIR);
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
                if (event.getEntity().getName().equals(SNOWBALL_ITEM)) {
                    brokenBlocks.put(Material.SNOW_BLOCK, event.getEntity().getLocation());
                    Block b = event.getEntity().getLocation().getBlock();
                    b.setType(Material.AIR);
                }
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
            String command = event.getMessage();
            if (command.equalsIgnoreCase("/spleef leave") && status == Status.WAIT){
                removePlayer(event.getPlayer());
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (playersInGame.contains(event.getPlayer()) || spectators.contains(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }
}
