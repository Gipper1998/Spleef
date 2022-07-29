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

    private static SignManager sm;
    private int taskID = 0;
    private FileConfiguration signs;

    public SignManager(){
        Spleef.main.getServer().getPluginManager().registerEvents(this, Spleef.main);
        this.signs = Spleef.main.signs.getConfig();
    }

    public static SignManager getInstance(){
        if (sm == null){
            sm = new SignManager();
        }
        return sm;
    }

    public void reloadSigns(){
        Spleef.main.signs.reloadConfig();
        signs = Spleef.main.signs.getConfig();
        startUpdater();
    }

    public void startUpdater(){
        if (taskID != 0) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = 0;
            startUpdater();
        }
        else {
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Spleef.main, new Runnable() {
                @Override
                public void run() {
                    if (signs.contains("Signs")) {
                        for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)) {
                            Arena arena = ArenaManager.getInstance().findArena(arenaName);
                            GameManager gm = ArenaManager.getInstance().findGame(arenaName);
                            if (arena != null) {
                                List<String> signLists = new ArrayList<>();
                                if (signs.contains("Signs." + arenaName)) {
                                    signLists = signs.getStringList("Signs." + arenaName);
                                }
                                for (int i = 0; i < signLists.size(); i++) {
                                    String[] location = signLists.get(i).split(";");
                                    String type = location[0];
                                    int x = Integer.valueOf(location[1]);
                                    int y = Integer.valueOf(location[2]);
                                    int z = Integer.valueOf(location[3]);
                                    World world = Bukkit.getWorld(location[4]);
                                    if (world != null) {
                                        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                                            continue;
                                        }
                                        Block block = world.getBlockAt(x, y, z);
                                        if (block.getType().name().contains("SIGN")) {
                                            Sign sign = (Sign) block.getState();
                                            updateSignType(sign, type, gm);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }, 0L, 20L);
        }
    }

    private String getSignStatus(GameManager gameManager){
        if (gameManager.getStatus() == Status.GAME || gameManager.getStatus() == Status.DELAYSTART){
            return MessageManager.getInstance().getString("sign_status.in-game");
        }
        else if (gameManager.getStatus() == Status.WINNER){
            return MessageManager.getInstance().getString("sign_status.reset");
        }
        else if (gameManager.getStatus() == Status.STOP){
            return MessageManager.getInstance().getString("sign_status.disabled");
        }
        else {
            return MessageManager.getInstance().getString("sign_status.wait");
        }
    }

    private void updateSignType(Sign sign, String type, GameManager gm) {
        List<String> signListMessages = new ArrayList<>();
        String status = getSignStatus(gm);
        if (type.equalsIgnoreCase("join")) {
            signListMessages = MessageManager.getInstance().getSignStringList("sign_join");
        }
        if (type.equalsIgnoreCase("leave")) {
            signListMessages = MessageManager.getInstance().getSignStringList("sign_leave");
        }
        for (int i = 0; i < signListMessages.size(); i++) {
            for (int line = 0; line < signListMessages.size(); line++) {
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

    @EventHandler
    public void onSignCreation(SignChangeEvent event){
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
            if (event.getLine(0).equals("[Spleef]")){
                if (event.getLine(1) != null && ArenaManager.getInstance().findArena(event.getLine(1)) != null) {
                    if (event.getLine(3).equalsIgnoreCase("[leave]") || event.getLine(3).equalsIgnoreCase("[join]")) {
                        String type = event.getLine(3).toUpperCase();
                        type = type.replaceAll("[\\[\\](){}]","");
                        String key = event.getLine(1).toUpperCase();
                        GameManager gm = ArenaManager.getInstance().findGame(event.getLine(1));
                        List<String> listedSigns = new ArrayList<>();
                        if (signs.contains("Signs." + key)) {
                            listedSigns = signs.getStringList("Signs." + gm.getArena().getName());
                        }
                        listedSigns.add(type + ";" + event.getBlock().getX() + ";" + event.getBlock().getY() + ";" + event.getBlock().getZ() + ";" + event.getBlock().getWorld().getName());
                        signs.set("Signs." + key, listedSigns);
                        MessageManager.getInstance().sendMessage("sign_creation", event.getPlayer());
                        for (int i = 0; i < 4; i++){
                            event.setLine(i, "");
                        }
                        Spleef.main.signs.saveConfig();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event){
        Block block = event.getBlock();
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
            if (block.getType().name().contains("SIGN")) {
                if (signs.contains("Signs")) {
                    for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)) {
                        List<String> listedSigns = new ArrayList<>();
                        if (signs.contains("Signs." + arenaName)) {
                            listedSigns = signs.getStringList("Signs." + arenaName);
                        }
                        for (int i = 0; i < listedSigns.size(); i++) {
                            String[] location = listedSigns.get(i).split(";");
                            int x = Integer.valueOf(location[1]);
                            int y = Integer.valueOf(location[2]);
                            int z = Integer.valueOf(location[3]);
                            World world = Bukkit.getWorld(location[4]);
                            if (world != null) {
                                if (block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
                                    if (event.getPlayer().isSneaking()) {
                                        listedSigns.remove(i);
                                        signs.set("Signs." + arenaName, listedSigns);
                                        Spleef.main.signs.saveConfig();
                                        MessageManager.getInstance().sendMessage("sign_deletion", event.getPlayer());
                                        return;
                                    }
                                    else {
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null && block.getType().name().contains("SIGN") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (signs.contains("Signs")){
                for (String arenaName : signs.getConfigurationSection("Signs").getKeys(false)){
                    if (signs.contains("Signs." + arenaName)){
                        Arena arena = ArenaManager.getInstance().findArena(arenaName);
                        if (arena != null) {
                            GameManager gm = ArenaManager.getInstance().findGame(arenaName);
                            List<String> listedSigns = new ArrayList<>();
                            if (signs.contains("Signs." + arenaName)) {
                                listedSigns = signs.getStringList("Signs." + arenaName);
                            }
                            for (int i = 0; i < listedSigns.size(); i++) {
                                String[] location = listedSigns.get(i).split(";");
                                String type = location[0];
                                int x = Integer.valueOf(location[1]);
                                int y = Integer.valueOf(location[2]);
                                int z = Integer.valueOf(location[3]);
                                World world = Bukkit.getWorld(location[4]);
                                if (world != null) {
                                    if (block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
                                        if (type.equalsIgnoreCase("join")) {
                                            if (!gm.getTotalPlayers().contains(p)) {
                                                gm.addPlayer(p);
                                            }
                                        }
                                        else if (type.equalsIgnoreCase("leave")) {
                                            if (gm.getTotalPlayers().contains(p)) {
                                                gm.removePlayer(p);
                                            }
                                        }
                                        else {
                                            continue;
                                        }
                                    }
                                    else {
                                        continue;
                                    }
                                }
                                else {
                                    continue;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    public void deleteSigns(GameManager gm) {
        if (signs.contains("Signs")) {
            signs.set("Signs." + gm.getArena().getName(), null);
            Spleef.main.signs.saveConfig();
        }
    }
}