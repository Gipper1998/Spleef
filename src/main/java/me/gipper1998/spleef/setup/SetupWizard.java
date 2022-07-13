package me.gipper1998.spleef.setup;

import lombok.Getter;
import lombok.Setter;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.item.ItemBuilder;
import me.gipper1998.spleef.file.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetupWizard implements Listener {

    @Setter @Getter
    private List<Player> inSetupWizard = new ArrayList<>();

    private ArenaSetupTemplate template;

    private SetupStoreItems ssi;

    private String CANCEL_WIZARD = "";
    private String SET_MINIMUM = "";
    private String SET_MAXIMUM = "";
    private String SET_ARENA_SPAWN = "";
    private String SET_LOBBY_SPAWN = "";
    private String SET_SPECTATOR_SPAWN = "";
    private String COMPLETE_WIZARD = "";

    private boolean findMin = false;
    private boolean findMax = false;

    public SetupWizard(Player p, String name){
        Bukkit.getPluginManager().registerEvents(this, Spleef.main);
        this.template = new ArenaSetupTemplate(name);
        this.CANCEL_WIZARD = MessageManager.getString("cancel_wizard_item");
        this.SET_MINIMUM = MessageManager.getString("set_minimum_item");
        this.SET_MAXIMUM = MessageManager.getString("set_maximum_item");
        this.SET_ARENA_SPAWN = MessageManager.getString("set_arena_spawn_item");
        this.SET_LOBBY_SPAWN = MessageManager.getString("set_lobby_spawn_item");
        this.SET_SPECTATOR_SPAWN = MessageManager.getString("set_spectator_spawn_item");
        this.COMPLETE_WIZARD = MessageManager.getString("complete_wizard_item");
        this.ssi = new SetupStoreItems(p);
        inSetupWizard.add(p);
        giveItems(p, p.getInventory());
    }

    private void giveItems(Player player, Inventory inventory){
        inventory.setItem(0, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.cancel"), CANCEL_WIZARD).getIs());
        inventory.setItem(6, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.maximum"), SET_MAXIMUM).getIs());
        inventory.setItem(5, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.minimum"), SET_MINIMUM).getIs());
        inventory.setItem(4, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.spectator"), SET_SPECTATOR_SPAWN).getIs());
        inventory.setItem(3, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.lobby"), SET_LOBBY_SPAWN).getIs());
        inventory.setItem(2, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.arena"), SET_ARENA_SPAWN).getIs());
        inventory.setItem(8, new ItemBuilder(ConfigManager.getBlock("setup_wizard_blocks.complete"), COMPLETE_WIZARD).getIs());
        player.updateInventory();
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if (inSetupWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if (inSetupWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event){
        if (inSetupWizard.contains(event.getPlayer())) {
            ItemStack item = event.getItem();
            ItemMeta im = event.getItem().getItemMeta();
            if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.cancel")) && (im.getDisplayName().equals(CANCEL_WIZARD)) && bothClicks(event)) {
                exitWizard(event.getPlayer(), false);
                MessageManager.getString("exit_wizard", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.maximum")) && (im.getDisplayName().equals(SET_MAXIMUM)) && bothClicks(event)) {
                MessageManager.getString("wizard_maximum_chat", event.getPlayer());
                findMax = true;
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.minimum")) && (im.getDisplayName().equals(SET_MINIMUM)) && bothClicks(event)) {
                MessageManager.getString("wizard_minimum_chat", event.getPlayer());
                findMin = true;
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.arena")) && (im.getDisplayName().equals(SET_ARENA_SPAWN)) && bothClicks(event)) {
                template.setArena(event.getPlayer().getLocation());
                MessageManager.getString("wizard_arena_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.spectator")) && (im.getDisplayName().equals(SET_SPECTATOR_SPAWN)) && bothClicks(event)) {
                template.setSpectate(event.getPlayer().getLocation());
                MessageManager.getString("wizard_spectator_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.lobby")) && (im.getDisplayName().equals(SET_LOBBY_SPAWN)) && bothClicks(event)) {
                template.setLobby(event.getPlayer().getLocation());
                MessageManager.getString("wizard_lobby_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("setup_wizard_blocks.complete")) && (im.getDisplayName().equals(COMPLETE_WIZARD)) && bothClicks(event)) {
                if (isComplete(event.getPlayer())) {
                    exitWizard(event.getPlayer(), true);
                    MessageManager.getString("wizard_arena_saved", event.getPlayer());
                }
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (inSetupWizard.contains(event.getPlayer())) {
            if (isNumeric(event.getMessage())) {
                if (findMin) {
                    template.setMinimum(Integer.parseInt(event.getMessage()));
                    MessageManager.getString("wizard_minimum_set", Integer.parseInt(event.getMessage()), event.getPlayer());
                    findMin = false;
                    event.setCancelled(true);
                    return;
                }
                if (findMax) {
                    template.setMaximum(Integer.parseInt(event.getMessage()));
                    MessageManager.getString("wizard_maximum_set", Integer.parseInt(event.getMessage()), event.getPlayer());
                    event.setCancelled(true);
                    findMax = false;
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (inSetupWizard.contains(event.getPlayer())){
            exitWizard(event.getPlayer(), false);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event){
        if (inSetupWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    private void exitWizard(Player p, boolean finished){
        p.getInventory().clear();
        ssi.giveBackItems();
        p.updateInventory();
        if (finished){
            if (template.lobby == null){
                template.lobby = template.arena;
            }
            ArenaManager.createArena(template);
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spleef.main, new Runnable() {
            @Override
            public void run() {
                inSetupWizard.remove(p);
            }
        }, 20L);
    }

    private boolean bothClicks(PlayerInteractEvent event){
        return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isNumeric(String temp){
        try {
            Double.parseDouble(temp);
            return true;
        } catch (NumberFormatException e) {return false;}
    }

    private boolean isComplete(Player player){
        if (template.arena == null){
            MessageManager.getString("wizard_arena_not_set", player);
            return false;
        }
        else if (template.spectate == null){
            MessageManager.getString("wizard_spectator_not_set", player);
            return false;
        }
        else if (template.minimum == 0){
            MessageManager.getString("wizard_minimum_not_set", player);
            return false;
        }
        else if (template.maximum == 0){
            MessageManager.getString("wizard_maximum_not_set", player);
            return false;
        }
        else {
            return true;
        }
    }

}
