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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SignManager implements Listener {

    private BukkitTask task;

    public SignManager(){
        if (task != null){
            task.cancel();
        }
        task = (new BukkitRunnable(){
            public void run(){
                updateSigns();
            }
        }.runTaskTimerAsynchronously(Spleef.main, 0L, 20L));
    }

    private void updateSigns(){
        ConfigurationSection section = Spleef.main.getConfig().getConfigurationSection("Signs");
        if (section == null){
            return;
        }
        Set<String> keys = section.getKeys(false);
        for (String key : keys){
            Arena arena = ArenaManager.findArena(key);
            GameManager gm = ArenaManager.findGame(key);
            if (arena != null){
                List<String> listedSigns = new ArrayList<>();
                if (Spleef.main.signs.getConfig().contains("Signs." + key)){
                    listedSigns = Spleef.main.signs.getConfig().getStringList("Signs." + key);
                }
                for (int i = 0; i < listedSigns.size(); i++){
                    String[] location = listedSigns.get(i).split(";");
                    int x = Integer.valueOf(location[0]);
                    int y = Integer.valueOf(location[1]);
                    int z = Integer.valueOf(location[2]);
                    World world = Bukkit.getWorld(location[3]);
                    if (world != null){
                        if (!world.isChunkLoaded(x >> 4, y >> 4)){
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (block instanceof Sign){
                        Sign sign = (Sign) block.getState();
                        String status = getSignStatus(gm);
                        List<String> signListMessages = MessageManager.getStringList("main_sign");
                        for (int line = 0; line < signListMessages.size(); line++){
                            String currentLine = signListMessages.get(line);
                            currentLine = currentLine.replace("<arenaname>", gm.getArena().getName());
                            currentLine = currentLine.replace("<status>", status);
                            currentLine = currentLine.replace("<ingame>", Integer.toString(gm.getTotalPlayers().size()));
                            currentLine = currentLine.replace("<maximum>", Integer.toString(gm.getArena().getMaximum()));
                            sign.setLine(line, currentLine);
                        }
                        sign.update();
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
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
            if (event.getLine(0).equals("[Spleef]")){
                if (event.getLine(1) != null && ArenaManager.findArena(event.getLine(1)) != null){
                    String key = event.getLine(1).toUpperCase();
                    GameManager gm = ArenaManager.findGame(event.getLine(1));
                    String status = getSignStatus(gm);
                    List<String> signListMessages = MessageManager.getStringList("main_sign");
                    for (int line = 0; line < signListMessages.size(); line++){
                        String currentLine = signListMessages.get(line);
                        currentLine = currentLine.replace("<arenaname>", gm.getArena().getName());
                        currentLine = currentLine.replace("<status>", status);
                        currentLine = currentLine.replace("<ingame>", Integer.toString(gm.getTotalPlayers().size()));
                        currentLine = currentLine.replace("<maximum>", Integer.toString(gm.getArena().getMaximum()));
                        event.setLine(line, currentLine);
                    }
                    List<String> listedSigns = new ArrayList<>();
                    if (Spleef.main.signs.getConfig().contains("Signs." + key)){
                        listedSigns = Spleef.main.signs.getConfig().getStringList("Signs." + gm.getArena().getName());
                    }
                    listedSigns.add(event.getBlock().getX()+";"+event.getBlock().getY()+";"+event.getBlock().getZ()+";"+event.getBlock().getWorld().getName());
                    Spleef.main.signs.getConfig().set("Signs." + key, listedSigns);
                    Spleef.main.signs.saveConfig();
                }
            }
        }
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event){
        if (event.getBlock() instanceof Sign && event.getPlayer().isSneaking()){
            if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spleef.admin")){
                ConfigurationSection section = Spleef.main.signs.getConfig().getConfigurationSection("Signs");
                if (section == null){
                    return;
                }
                Set<String> keys = section.getKeys(false);
                for (String key : keys){
                    List<String> listedSigns = new ArrayList<>();
                    if (Spleef.main.signs.getConfig().contains("Signs." + key)){
                        listedSigns = Spleef.main.signs.getConfig().getStringList("Signs." + key);
                    }
                    for (int i = 0; i < listedSigns.size(); i++){
                        String[] location = listedSigns.get(i).split(";");
                        int x = Integer.valueOf(location[0]);
                        int y = Integer.valueOf(location[1]);
                        int z = Integer.valueOf(location[2]);
                        World world = Bukkit.getWorld(location[3]);
                        if (world != null){
                            if(event.getBlock().getX() == x && event.getBlock().getY() == y && event.getBlock().getZ() == z && world.getName().equals(event.getBlock().getWorld().getName())) {
                                listedSigns.remove(i);
                                Spleef.main.signs.getConfig().set("Signs." + key, listedSigns);
                                Spleef.main.signs.saveConfig();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() instanceof Sign){
            ConfigurationSection section = Spleef.main.signs.getConfig().getConfigurationSection("Signs");
            if (section == null){
                return;
            }
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                Arena arena = ArenaManager.findArena(key);
                GameManager gm = ArenaManager.findGame(key);
                if (arena != null){
                    List<String> listedSigns = new ArrayList<>();
                    if (Spleef.main.signs.getConfig().contains("Signs." + key)){
                        listedSigns = Spleef.main.signs.getConfig().getStringList("Signs." + key);
                    }
                    for (int i = 0; i < listedSigns.size(); i++){
                        String[] location = listedSigns.get(i).split(";");
                        int x = Integer.valueOf(location[0]);
                        int y = Integer.valueOf(location[1]);
                        int z = Integer.valueOf(location[2]);
                        World world = Bukkit.getWorld(location[3]);
                        if (world != null) {
                            if (event.getClickedBlock().getX() == x && event.getClickedBlock().getY() == y && event.getClickedBlock().getZ() == z && world.getName().equals(event.getClickedBlock().getWorld().getName())) {
                                if (gm != null){
                                    gm.addPlayer(event.getPlayer());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}