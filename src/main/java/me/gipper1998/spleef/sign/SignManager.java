package me.gipper1998.spleef.sign;

import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class SignManager implements Listener {

    private GameManager gm;
    private List<Sign> signs = new ArrayList<>();

    private String LINE_ZERO = "";
    private String LINE_ONE = "";
    private String LINE_TWO = "";
    private String LINE_THREE = "";

    public SignManager(GameManager gm){
        this.gm = gm;
        this.LINE_ZERO = MessageManager.getSignString(0, "main_sign.0");
        this.LINE_ONE = MessageManager.getSignString(1, "main_sign.1");
        this.LINE_TWO = MessageManager.getSignString(2, "main_sign.2");
        this.LINE_THREE = MessageManager.getSignString(3, "main_sign.3");
    }

    public void registerNewSigns(){
        for (World world : Bukkit.getWorlds()){
            for (Chunk c : world.getLoadedChunks()){
                for (BlockState state : c.getTileEntities()){
                    if (state instanceof Sign){
                        Sign sign = (Sign) state;
                        if (sign.getLine(1).equals(ChatColor.stripColor(gm.getArena().getName()))){
                            if (!signs.contains(sign)){
                                signs.add(sign);
                            }
                        }
                    }
                }
            }
        }
        updateSigns();
    }

    public void stopAllSigns(){
        for (Sign sign : signs){
            sign.setLine(0, LINE_ZERO);
            sign.setLine(1, gm.getArena().getName());
            sign.setLine(2, "");
            sign.setLine(3, "");
            sign.update();
        }
    }

    public void updateSigns(){
        for (Sign sign : signs){
            updateSign(sign);
        }
    }

    private void registerSign(Sign sign){
        if (sign.getLine(1).equals((ChatColor.stripColor(gm.getArena().getName())))){
            if (!signs.contains(sign)){
                signs.add(sign);
            }
        }
        updateSigns();
    }

    private void updateSign(Sign sign){
        if (sign.getChunk().isLoaded()){
            sign.getChunk().load();
        }
        switch (gm.getStatus()){
            case WAIT: {
                break;
            }
            case DELAYSTART: {
                break;
            }
            case GAME: {
                break;
            }
            case WINNER: {
                break;
            }
            case RESTARTING: {
                break;
            }
        }
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event){
        if (event.getPlayer().hasPermission("spleef.admin")){
            if (event.getLine(0).equals("[Spleef]")){
                if (event.getLine(1).equalsIgnoreCase(gm.getArena().getName())){
                    Sign sign = (Sign) event.getBlock().getState();
                    registerSign(sign);
                }
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event){
        if (event.getBlock().getState() instanceof Sign){
            Sign sign = (Sign) event.getBlock().getState();
            if (event.getPlayer().hasPermission("spleef.admin")){
                if (signs.contains(sign)){
                    signs.remove(sign);
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        for (BlockState state : event.getChunk().getTileEntities()){
            if (state instanceof Sign){
                Sign sign = (Sign) state;
                if (sign.getLine(1).equalsIgnoreCase(ChatColor.stripColor(gm.getArena().getName()))){
                    if (!signs.contains(sign)){
                        signs.add(sign);
                        updateSign(sign);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getClickedBlock().getState() instanceof Sign){
            Sign sign = (Sign) event.getClickedBlock().getState();
            registerSign(sign);
            if (sign.getLine(1).equalsIgnoreCase(ChatColor.stripColor(gm.getArena().getName()))){
                gm.addPlayer(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

}