package me.gipper1998.spleef.game;

import lombok.Getter;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.softdepend.VaultManager;
import me.gipper1998.spleef.utils.FireworkBuilder;
import me.gipper1998.spleef.utils.ItemBuilder;
import me.gipper1998.spleef.utils.PotionBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
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

    @Getter
    private int currentTime = 0;

    @Getter
    private Status status = Status.WAIT;

    private Player winner = null;

    private String EXIT_ITEM = "";
    private String GOLD_SPADE_ITEM = "";
    private String DIAMOND_SPADE_ITEM = "";
    private String SNOWBALL_ITEM = "";
    private String TNT = "";

    private Random rand;

    private HashMap<Player, GameStoreItems> playersStuff = new HashMap<Player, GameStoreItems>();

    private List<Location> blocksBroken = new ArrayList<>();

    @Getter
    private List<Player> playersInGame = new ArrayList<>();
    @Getter
    private List<Player> spectators = new ArrayList<>();
    @Getter
    private List<Player> totalPlayers = new ArrayList<>();

    private PotionBuilder invisiblePotion;


    public GameManager(Arena arena){
        Spleef.main.getServer().getPluginManager().registerEvents(this, Spleef.main);
        this.arena = arena;
        this.waitTime = ConfigManager.getInstance().getInt("waiting_time");
        this.gameTime = ConfigManager.getInstance().getInt("total_game_time");
        this.EXIT_ITEM = MessageManager.getInstance().getString("leave_item");
        this.GOLD_SPADE_ITEM = MessageManager.getInstance().getString("gold_shovel");
        this.DIAMOND_SPADE_ITEM = MessageManager.getInstance().getString("diamond_shovel");
        this.SNOWBALL_ITEM = MessageManager.getInstance().getString("snowball");
        this.TNT = "TNT_SPLEEF";
        this.rand = new Random();
        this.invisiblePotion = new PotionBuilder(PotionEffectType.INVISIBILITY, startDelay * 20, 5);
        GameTimeEvents.getInstance().loadEvents();
        this.runTaskTimer(Spleef.main, 20L, 20L);
    }

    @Override
    public void run(){
        if (ConfigManager.getInstance().getBoolean("exp_time_enable")){
            for (Player p : totalPlayers){
                p.setLevel(currentTime);
            }
        }
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
                    invisiblePotion.addPlayer(p);
                }
                currentTime = startDelay;
                status = Status.DELAYSTART;
                return;
            }
            if (currentTime % 10 == 0 || currentTime <= 5){
                for (Player p : playersInGame) {
                    p.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    MessageManager.getInstance().sendNumberMessage("starting_game", currentTime, p);
                }
            }
            currentTime--;
        }
        else {
            for (Player p : playersInGame) {
                MessageManager.getInstance().sendMessage("arena_not_enough_players", p);
            }
            currentTime = waitTime;
        }
    }

    private void delayStart(){
        if (currentTime <= 0){
            for (Player p : playersInGame){
                giveItems(p);
                p.showPlayer(Spleef.main, p);
                p.playSound(p.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
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
        if (playersInGame.size() == 1) {
            winner = playersInGame.get(0);
            currentTime = winnerDelay;
            status = Status.WINNER;
            return;
        }
        GameTimeEvents.getInstance().checkTime(currentTime, this);
        currentTime--;
    }

    private void winnerShowOff(){
        FireworkBuilder fb = new FireworkBuilder(arena.getArena(), 20, "aqua", 2, 5);
        if (currentTime == winnerDelay) {
            for (Player p : totalPlayers) {
                MessageManager.getInstance().sendPlayerNameMessage("player_winner", winner, p);
            }
        }
        if (currentTime <= winnerDelay && currentTime > 2){
            fb.launch();
        }
        if (currentTime <= 0){
            PlayerStatManager.getInstance().addWinPoint(winner.getUniqueId());
            winnerRewards();
            winner = null;
            removeEverybody();
            resetArena();
            currentTime = waitTime;
            status = Status.WAIT;
            return;
        }
        currentTime--;
    }

    private void resetArena(){
        blocksBroken.forEach(location -> location.getBlock().setType(Material.SNOW_BLOCK));
        blocksBroken.clear();
    }

    private void giveItems(Player p){
        if (p.hasPermission("Spleef.diamond")) {
            p.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SHOVEL, DIAMOND_SPADE_ITEM).getIs());
        }
        else {
            p.getInventory().setItem(0, new ItemBuilder(Material.GOLDEN_SHOVEL, GOLD_SPADE_ITEM).getIs());
        }
        if (ConfigManager.getInstance().getBoolean("give_snowballs_on_start.enable")) {
            p.getInventory().setItem(1, new ItemBuilder(Material.SNOWBALL, SNOWBALL_ITEM, ConfigManager.getInstance().getInt("give_snowballs_on_start.amount")).getIs());
        }
    }

    private boolean bothClicks(PlayerInteractEvent event){
        return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    private void winnerRewards(){
        VaultManager.getInstance().deposit(winner, ConfigManager.getInstance().getInt("vault"));
        List<String> rewardCommands = ConfigManager.getInstance().getStringList("commands");
        ConsoleCommandSender console = Spleef.main.getServer().getConsoleSender();
        Iterator var9 = rewardCommands.iterator();
        if (rewardCommands.size() != 0) {
            while (var9.hasNext()) {
                String command = (String) var9.next();
                command = command.replace("<player>", winner.getName());
                ServerCommandEvent commandEvent = new ServerCommandEvent(console, command);
                Spleef.main.getServer().getPluginManager().callEvent(commandEvent);
                Spleef.main.getServer().getScheduler().callSyncMethod(Spleef.main, () -> {
                    return Spleef.main.getServer().dispatchCommand(commandEvent.getSender(), commandEvent.getCommand());
                });
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
            p.getInventory().setItem(8, new ItemBuilder(ConfigManager.getInstance().getBlock("in_lobby.leave"), EXIT_ITEM).getIs());
            p.updateInventory();
        }
        else {
            MessageManager.getInstance().sendMessage("arena_in-game", p);
        }
    }

    public void removePlayer(Player p) {
        GameStoreItems gmi = playersStuff.get(p);
        gmi.giveBackItems();
        playersStuff.remove(p);
        if (totalPlayers.contains(p)) {
            if (status == Status.WAIT) {
                playersInGame.remove(p);
                totalPlayers.remove(p);
                for (Player player : playersInGame) {
                    MessageManager.getInstance().sendPlayerNameMessage("player_quit", p, player);
                }
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
                PlayerStatManager.getInstance().addLosePoint(p.getUniqueId());
                if (playersInGame.size() == 1) {
                    winner = playersInGame.get(0);
                    currentTime = winnerDelay;
                    status = Status.WINNER;
                    return;
                }
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
                    Block b = (Block) it.next();
                    if (!(b.getType() == Material.SNOW_BLOCK)) {
                        blocksBroken.add(b.getLocation());
                        b.setType(Material.AIR);
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
                blocksBroken.add(b.getLocation());
                b.setType(Material.AIR);
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileHitEvent event){
        Player p = (Player) event.getEntity().getShooter();
        if (playersInGame.contains(p) && status == Status.GAME){
            if (event.getHitBlock() != null && event.getHitBlock().getType() == Material.SNOW_BLOCK){
                blocksBroken.add(event.getHitBlock().getLocation());
                event.getHitBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void foodLevel(FoodLevelChangeEvent event){
        if (event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if (playersInGame.contains(p)){
                event.setCancelled(true);
            }
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
        if (event.getWhoClicked() instanceof Player){
            Player p = (Player) event.getWhoClicked();
            if (playersInGame.contains(p)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerFly(PlayerToggleFlightEvent event){
        if (playersInGame.contains(event.getPlayer())){
            event.setCancelled(true);
        }
        if (spectators.contains(event.getPlayer())){
            double distance = event.getPlayer().getEyeLocation().distance(arena.getSpectate());
            if (distance > 100){
                event.getPlayer().teleport(arena.getSpectate());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        if (playersInGame.contains(event.getPlayer()) || spectators.contains(event.getPlayer())){
            String command = event.getMessage();
            if (!(command.equalsIgnoreCase("/spleef leave"))){
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

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event){
        if (playersInGame.contains(event.getPlayer()) && status == Status.GAME){
            Player p = event.getPlayer();
            if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.LAVA){
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
                if (playersInGame.size() == 1) {
                    winner = playersInGame.get(0);
                    currentTime = winnerDelay;
                    status = Status.WINNER;
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if (playersInGame.contains(p)){
                event.setCancelled(true);
            }
        }
    }
}
