package me.gipper1998.spleef.edit;

import lombok.Getter;
import lombok.Setter;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.item.ItemBuilder;
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

public class EditWizard implements Listener {

    @Setter @Getter
    private List<Player> inEditWizard = new ArrayList<>();

    private ArenaEditTemplate template;

    private Arena prevArena;

    private EditStoreItems esi;

    private String EDIT_NAME = "";
    private String SET_MINIMUM = "";
    private String SET_MAXIMUM = "";
    private String SET_ARENA_SPAWN = "";
    private String SET_LOBBY_SPAWN = "";
    private String SET_SPECTATOR_SPAWN = "";
    private String COMPLETE_WIZARD = "";

    private boolean findMin = false;
    private boolean findMax = false;
    private boolean findNewName = false;

    public EditWizard(Player p, Arena arena){
        Bukkit.getPluginManager().registerEvents(this, Spleef.main);
        this.template = new ArenaEditTemplate(arena);
        this.prevArena = arena;
        this.EDIT_NAME = MessageManager.getInstance().getString("set_name_item");
        this.SET_MINIMUM = MessageManager.getInstance().getString("set_minimum_item");
        this.SET_MAXIMUM = MessageManager.getInstance().getString("set_maximum_item");
        this.SET_ARENA_SPAWN = MessageManager.getInstance().getString("set_arena_spawn_item");
        this.SET_LOBBY_SPAWN = MessageManager.getInstance().getString("set_lobby_spawn_item");
        this.SET_SPECTATOR_SPAWN = MessageManager.getInstance().getString("set_spectator_spawn_item");
        this.COMPLETE_WIZARD = MessageManager.getInstance().getString("complete_wizard_item");
        this.esi = new EditStoreItems(p);
        inEditWizard.add(p);
        giveItems(p, p.getInventory());
    }

    private void giveItems(Player player, Inventory inventory){
        inventory.setItem(0, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.name"), EDIT_NAME).getIs());
        inventory.setItem(6, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.maximum"), SET_MAXIMUM).getIs());
        inventory.setItem(5, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.minimum"), SET_MINIMUM).getIs());
        inventory.setItem(4, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.spectator"), SET_SPECTATOR_SPAWN).getIs());
        inventory.setItem(3, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.lobby"), SET_LOBBY_SPAWN).getIs());
        inventory.setItem(2, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.arena"), SET_ARENA_SPAWN).getIs());
        inventory.setItem(8, new ItemBuilder(ConfigManager.getBlock("edit_wizard_blocks.complete"), COMPLETE_WIZARD).getIs());
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if (inEditWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if (inEditWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event){
        if (inEditWizard.contains(event.getPlayer())) {
            ItemStack item = event.getItem();
            ItemMeta im = event.getItem().getItemMeta();
            if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.name")) && (im.getDisplayName().equals(EDIT_NAME)) && bothClicks(event)) {
                MessageManager.getInstance().sendMessage("wizard_name_chat", event.getPlayer());
                findNewName = true;
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.maximum")) && (im.getDisplayName().equals(SET_MAXIMUM)) && bothClicks(event)) {
                MessageManager.getInstance().sendMessage("wizard_maximum_chat", event.getPlayer());
                findMax = true;
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.minimum")) && (im.getDisplayName().equals(SET_MINIMUM)) && bothClicks(event)) {
                MessageManager.getInstance().sendMessage("wizard_minimum_chat", event.getPlayer());
                findMin = true;
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.arena")) && (im.getDisplayName().equals(SET_ARENA_SPAWN)) && bothClicks(event)) {
                template.setArena(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_arena_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.spectator")) && (im.getDisplayName().equals(SET_SPECTATOR_SPAWN)) && bothClicks(event)) {
                template.setSpectate(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_spectator_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.lobby")) && (im.getDisplayName().equals(SET_LOBBY_SPAWN)) && bothClicks(event)) {
                template.setLobby(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_lobby_spawn_set", event.getPlayer());
                return;
            }
            else if ((item.getType() == ConfigManager.getBlock("edit_wizard_blocks.complete")) && (im.getDisplayName().equals(COMPLETE_WIZARD)) && bothClicks(event)) {
                exitWizard(event.getPlayer());
                MessageManager.getInstance().sendMessage("wizard_arena_saved", event.getPlayer());
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (inEditWizard.contains(event.getPlayer())) {
            if (isNumeric(event.getMessage())) {
                if (findMin) {
                    template.setMinimum(Integer.parseInt(event.getMessage()));
                    MessageManager.getInstance().sendNumberMessage("wizard_minimum_set", Integer.parseInt(event.getMessage()), event.getPlayer());
                    findMin = false;
                    event.setCancelled(true);
                    return;
                }
                if (findMax) {
                    template.setMaximum(Integer.parseInt(event.getMessage()));
                    MessageManager.getInstance().sendNumberMessage("wizard_maximum_set", Integer.parseInt(event.getMessage()), event.getPlayer());
                    event.setCancelled(true);
                    findMax = false;
                    return;
                }
                if (findNewName){
                    template.setName(event.getMessage().toUpperCase());
                    MessageManager.getInstance().sendStringMessage("wizard_name_set", event.getMessage(), event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (inEditWizard.contains(event.getPlayer())){
            exitWizard(event.getPlayer());
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event){
        if (inEditWizard.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    private void exitWizard(Player p){
        p.getInventory().clear();
        esi.giveBackItems();
        p.updateInventory();
        ArenaManager.saveEditedArena(template, prevArena);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spleef.main, new Runnable() {
            @Override
            public void run() {
                inEditWizard.remove(p);
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

}
