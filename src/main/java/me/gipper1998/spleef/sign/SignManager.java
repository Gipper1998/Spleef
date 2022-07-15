package me.gipper1998.spleef.sign;
// Special thanks to Ajneb97 for this sign Implementation
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class SignManager implements Listener {

    private int taskID = 0;

    public SignManager(){
        Spleef.main.getServer().getPluginManager().registerEvents(this, Spleef.main);
    }

    public void startUpdater() {
        if (taskID != 0) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
        updater();
    }

    private void updater(){
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Spleef.main, new Runnable() {
            @Override
            public void run() {
                updateSigns();
            }
        },0L, 20L);
    }

    private void updateSigns() {
        FileConfiguration signs = Spleef.main.signs.getConfig();
        if (signs.contains("Signs")) {
            for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)) {
                Arena arena = ArenaManager.findArena(arenaName);
                GameManager gm = ArenaManager.findGame(arenaName);
                if (arena != null) {
                    List<String> signLists = new ArrayList<>();
                    if (signs.contains("Signs." + arenaName)) {
                        signLists = signs.getStringList("Signs." + arenaName);
                    }
                    for (int i = 0; i < signLists.size(); i++) {
                        String[] location = signLists.get(i).split(";");
                        int x = Integer.valueOf(location[0]);
                        int y = Integer.valueOf(location[1]);
                        int z = Integer.valueOf(location[2]);
                        World world = Bukkit.getWorld(location[3]);
                        if (world != null) {
                            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                                continue;
                            }
                            Block block = world.getBlockAt(x, y, z);
                            if (block.getType().name().contains("SIGN")) {
                                Sign sign = (Sign) block.getState();
                                String status = getSignStatus(gm);
                                List<String> signListMessages = MessageManager.getStringList("main_sign");
                                for (int line = 0; line < signListMessages.size(); line++){
                                    String currentLine = signListMessages.get(line);
                                    currentLine = currentLine.replaceAll("<arenaname>", gm.getArena().getName());
                                    currentLine = currentLine.replaceAll("<status>", status);
                                    currentLine = currentLine.replaceAll("<in_game>", Integer.toString(gm.getTotalPlayers().size()));
                                    currentLine = currentLine.replaceAll("<maximum>", Integer.toString(gm.getArena().getMaximum()));
                                    sign.setLine(line, currentLine);
                                }
                                sign.update();
                            }
                        }
                    }

                }
            }
        }
    }

    private String getSignStatus(GameManager gameManager){
        if (gameManager.getStatus() == Status.GAME || gameManager.getStatus() == Status.DELAYSTART){
            return MessageManager.getString("sign_status.in-game");
        }
        else if (gameManager.getStatus() == Status.WINNER || gameManager.getStatus() == Status.RESTARTING){
            return MessageManager.getString("sign_status.reset");
        }
        else {
            return MessageManager.getString("sign_status.wait");
        }
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent event){
        FileConfiguration signs = Spleef.main.signs.getConfig();
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
            if (event.getLine(0).equals("[Spleef]")){
                if (event.getLine(1) != null && ArenaManager.findArena(event.getLine(1)) != null){
                    String key = event.getLine(1).toUpperCase();
                    GameManager gm = ArenaManager.findGame(event.getLine(1));
                    String status = getSignStatus(gm);
                    List<String> signListMessages = MessageManager.getStringList("main_sign");
                    for (int line = 0; line < signListMessages.size(); line++){
                        String currentLine = signListMessages.get(line);
                        currentLine = currentLine.replaceAll("<arenaname>", gm.getArena().getName());
                        currentLine = currentLine.replaceAll("<status>", status);
                        currentLine = currentLine.replaceAll("<in_game>", Integer.toString(gm.getTotalPlayers().size()));
                        currentLine = currentLine.replaceAll("<maximum>", Integer.toString(gm.getArena().getMaximum()));
                        event.setLine(line, currentLine);
                    }
                    List<String> listedSigns = new ArrayList<>();
                    if (signs.contains("Signs." + key)){
                        listedSigns = signs.getStringList("Signs." + gm.getArena().getName());
                    }
                    listedSigns.add(event.getBlock().getX()+";"+event.getBlock().getY()+";"+event.getBlock().getZ()+";"+event.getBlock().getWorld().getName());
                    signs.set("Signs." + key, listedSigns);
                    MessageManager.sendMessage("sign_creation", event.getPlayer());
                    Spleef.main.signs.saveConfig();
                }
            }
        }
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event){
        FileConfiguration signs = Spleef.main.signs.getConfig();
        Block block = event.getBlock();
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
            if (event.getPlayer().isSneaking()){
                if (block.getType().name().contains("SIGN")) {
                    if (signs.contains("Signs")){
                        for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)){
                            List<String> listedSigns = new ArrayList<>();
                            if (signs.contains("Signs." + arenaName)){
                                listedSigns = signs.getStringList("Signs." + arenaName);
                            }
                            for (int i = 0; i < listedSigns.size(); i++){
                                String[] location = listedSigns.get(i).split(";");
                                int x = Integer.valueOf(location[0]);
                                int y = Integer.valueOf(location[1]);
                                int z = Integer.valueOf(location[2]);
                                World world = Bukkit.getWorld(location[3]);
                                if (world != null){
                                    if (block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
                                        listedSigns.remove(i);
                                        signs.set("Signs." + arenaName, listedSigns);
                                        Spleef.main.signs.saveConfig();
                                        MessageManager.sendMessage("sign_deletion", event.getPlayer());
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                event.setCancelled(true);
            }
        }
        else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();
        FileConfiguration signs = Spleef.main.signs.getConfig();
        if (block != null && block.getType().name().contains("SIGN") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (signs.contains("Signs")){
                for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)){
                    if (signs.contains("Signs." + arenaName)){
                        Arena arena = ArenaManager.findArena(arenaName);
                        if (arena != null) {
                            GameManager gm = ArenaManager.findGame(arenaName);
                            List<String> listedSigns = new ArrayList<>();
                            if (signs.contains("Signs." + arenaName)) {
                                listedSigns = signs.getStringList("Signs." + arenaName);
                            }
                            for (int i = 0; i < listedSigns.size(); i++) {
                                String[] location = listedSigns.get(i).split(";");
                                int x = Integer.valueOf(location[0]);
                                int y = Integer.valueOf(location[1]);
                                int z = Integer.valueOf(location[2]);
                                World world = Bukkit.getWorld(location[3]);
                                if (world != null) {
                                    if (block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
                                        if (arena != null) {
                                            gm.addPlayer(p);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}