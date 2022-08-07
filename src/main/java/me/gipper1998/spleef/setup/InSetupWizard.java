package me.gipper1998.spleef.setup;

import lombok.Getter;
import me.gipper1998.spleef.utils.ItemStoreManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InSetupWizard implements Listener {

    public static InSetupWizard isw;

    @Getter
    private HashMap<SetupWizard, ItemStoreManager> inWizard = new HashMap<>();

    private List<Player> playersInWizard = new ArrayList<>();

    public static InSetupWizard getInstance(){
        if (isw == null){
            isw = new InSetupWizard();
        }
        return isw;
    }

    public boolean addPlayer(Player p, String name){
        if (!playersInWizard.contains(p)){
            ItemStoreManager ism = new ItemStoreManager(p);
            SetupWizard sm = new SetupWizard(p, name);
            inWizard.put(sm, ism);
            playersInWizard.add(p);
            return true;
        }
        return false;
    }

    public void removePlayer(Player p) {
        if (playersInWizard.contains(p)) {
            for (Map.Entry<SetupWizard, ItemStoreManager> entry : inWizard.entrySet()) {
                SetupWizard sm = entry.getKey();
                if (inWizard.containsKey(sm)) {
                    ItemStoreManager ism = inWizard.get(sm);
                    ism.giveBackItems();
                    inWizard.remove(sm);
                    playersInWizard.remove(p);
                    return;
                }
            }
        }
    }

    public void removeEverybody(){
        for (Player p : playersInWizard){
            removePlayer(p);
        }
    }

    private boolean bothClicks(PlayerInteractEvent event){
        return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (playersInWizard.contains(event.getPlayer())) {
            event.setCancelled(true);
            for (Map.Entry<SetupWizard, ItemStoreManager> entry : inWizard.entrySet()) {
                if (entry.getKey().getP().equals(event.getPlayer()) && bothClicks(event)) {
                    event.setCancelled(true);
                    entry.getKey().onPlayerInteract(event.getItem());
                }
            }
        }
    }

    @EventHandler
    public void onPlayChat(AsyncPlayerChatEvent event){
        if (playersInWizard.contains(event.getPlayer())) {
            for (Map.Entry<SetupWizard, ItemStoreManager> entry : inWizard.entrySet()) {
                if (entry.getKey().getP().equals(event.getPlayer())) {
                    if (entry.getKey().isFindMax() || entry.getKey().isFindMin()) {
                        entry.getKey().onChat(event.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (playersInWizard.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event){
        if (playersInWizard.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event){
        if (playersInWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        if (playersInWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (playersInWizard.contains(event.getPlayer())){
            for (Map.Entry<SetupWizard, ItemStoreManager> entry : inWizard.entrySet()) {
                if (entry.getKey().getP().equals(event.getPlayer())) {
                    entry.getKey().exitWizard(false);
                }
            }
        }
    }
}
