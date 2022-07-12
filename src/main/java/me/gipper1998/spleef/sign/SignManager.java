// Special thanks to Ajneb97 for this sign Implementation

package me.gipper1998.spleef.sign;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

    private String LINE_ZERO = "";
    private String LINE_ONE = "";
    private String LINE_TWO = "";
    private String LINE_THREE = "";
    private static BukkitTask task;

    public static void startUpdatingSigns(){
        if (task != null){
            task.cancel();
        }
        task = (new BukkitRunnable(){
            public void run(){

            }
        }.runTaskTimerAsynchronously(Spleef.main, 0L, 20L));
    }

    private void updateSigns(){
        ConfigurationSection section = Spleef.main.getConfig().getConfigurationSection("Signs");
        Set<String> keys = section.getKeys(false);
        for (String key : keys){
            Arena arena = ArenaManager.findArena(key);
            if (arena != null){
                List<String> details = new ArrayList<>();
                if (Spleef.main.signs.getConfig().contains("Signs." + key)){
                    details = Spleef.main.signs.getConfig().getStringList("Signs." + key);
                }
                for (int i = 0; i < details.size(); i++){
                    String[] location = details.get(i).split(";");
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
                        String arenaUpdateLine = "";
                        List<String>
                    }
                }

            }
        }
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent event){
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("spleef.admin")){
            if (event.getLine(0).equals("[Spleef]")){
                String arena = event.getLine(1);
                if (arena != null && ArenaManager.findArena(arena) != null){

                }
            }
        }
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event){
        Player player = event.getPlayer();
        if (event.getBlock() instanceof Sign){
            if (player.isOp() || player.hasPermission("spleef.admin")){

            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() instanceof Sign){

        }
    }
}
