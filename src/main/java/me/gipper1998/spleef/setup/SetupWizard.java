package me.gipper1998.spleef.setup;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.ArenaManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.utils.ItemBuilder;
import me.gipper1998.spleef.file.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SetupWizard implements Listener {

    private ArenaSetupTemplate template;

    private Player p;

    private String CANCEL_WIZARD = "";
    private String SET_MINIMUM = "";
    private String SET_MAXIMUM = "";
    private String SET_ARENA_SPAWN = "";
    private String SET_LOBBY_SPAWN = "";
    private String SET_SPECTATOR_SPAWN = "";
    private String COMPLETE_WIZARD = "";

    private ItemStack itemOne;
    private ItemStack itemTwo;
    private ItemStack itemThree;
    private ItemStack itemFour;
    private ItemStack itemFive;
    private ItemStack itemSix;
    private ItemStack itemSeven;
    private boolean findMin = false;
    private boolean findMax = false;

    public SetupWizard(Player p, String name){
        Bukkit.getPluginManager().registerEvents(this, Spleef.main);
        this.p = p;
        this.template = new ArenaSetupTemplate(name);
        this.CANCEL_WIZARD = MessageManager.getInstance().getString("cancel_wizard_item");
        this.SET_MINIMUM = MessageManager.getInstance().getString("set_minimum_item");
        this.SET_MAXIMUM = MessageManager.getInstance().getString("set_maximum_item");
        this.SET_ARENA_SPAWN = MessageManager.getInstance().getString("set_arena_spawn_item");
        this.SET_LOBBY_SPAWN = MessageManager.getInstance().getString("set_lobby_spawn_item");
        this.SET_SPECTATOR_SPAWN = MessageManager.getInstance().getString("set_spectator_spawn_item");
        this.COMPLETE_WIZARD = MessageManager.getInstance().getString("complete_wizard_item");
        setupItems();
        giveItems();
        p.setGameMode(GameMode.CREATIVE);
    }

    private void setupItems(){
        this.itemOne =  new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.cancel"), CANCEL_WIZARD).getIs();
        this.itemTwo = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.arena"), SET_ARENA_SPAWN).getIs();
        this.itemThree = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.lobby"), SET_LOBBY_SPAWN).getIs();
        this.itemFour = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.spectator"), SET_SPECTATOR_SPAWN).getIs();
        this.itemFive = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.minimum"), SET_MINIMUM).getIs();
        this.itemSix = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.maximum"), SET_MAXIMUM).getIs();
        this.itemSeven = new ItemBuilder(ConfigManager.getInstance().getBlock("setup_wizard_blocks.complete"), COMPLETE_WIZARD).getIs();
    }

    private void giveItems(){
        p.getInventory().setItem(0, itemOne);
        p.getInventory().setItem(2, itemTwo);
        p.getInventory().setItem(3, itemThree);
        p.getInventory().setItem(4, itemFour);
        p.getInventory().setItem(5, itemFive);
        p.getInventory().setItem(6, itemSix);
        p.getInventory().setItem(8, itemSeven);
        p.updateInventory();
    }

    private boolean containsPlayer(Player p){
        return InSetupWizard.getInstance().getInWizard().containsKey(p);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (containsPlayer(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (containsPlayer(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (containsPlayer(event.getPlayer())) {
            event.setCancelled(true);
            ItemStack item = event.getItem();
            if (item.equals(itemOne) && bothClicks(event)) {
                exitWizard(false);
                MessageManager.getInstance().sendMessage("exit_wizard", event.getPlayer());
            }
            if (item.equals(itemSix) && bothClicks(event)) {
                MessageManager.getInstance().sendMessage("wizard_maximum_chat", event.getPlayer());
                findMax = true;
            }
            if (item.equals(itemFive) && bothClicks(event)) {
                MessageManager.getInstance().sendMessage("wizard_minimum_chat", event.getPlayer());
                findMin = true;
            }
            if (item.equals(itemTwo) && bothClicks(event)) {
                template.setArena(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_arena_spawn_set", event.getPlayer());
            }
            if (item.equals(itemFour) && bothClicks(event)) {
                template.setSpectate(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_spectator_spawn_set", event.getPlayer());
            }
            if (item.equals(itemThree) && bothClicks(event)) {
                template.setLobby(event.getPlayer().getLocation());
                MessageManager.getInstance().sendMessage("wizard_lobby_spawn_set", event.getPlayer());
            }
            if (item.equals(itemSeven) && bothClicks(event)) {
                if (isComplete(event.getPlayer())) {
                    exitWizard(true);
                    MessageManager.getInstance().sendMessage("wizard_arena_saved", event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (containsPlayer(event.getPlayer())) {
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
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if (containsPlayer(event.getPlayer())){
            exitWizard(false);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        if (containsPlayer(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    private void exitWizard(boolean finished){
        p.getInventory().clear();
        p.updateInventory();
        if (finished){
            if (template.getLobby() == null){
                template.setLobby(template.getArena());
            }
            ArenaManager.getInstance().createArena(template);
        }
        InSetupWizard.getInstance().removePlayer(p);
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
        if (template.getArena() == null){
            MessageManager.getInstance().sendMessage("wizard_arena_not_set", player);
            return false;
        }
        else if (template.getSpectate() == null){
            MessageManager.getInstance().sendMessage("wizard_spectator_not_set", player);
            return false;
        }
        else if (template.getMinimum() == 0){
            MessageManager.getInstance().sendMessage("wizard_minimum_not_set", player);
            return false;
        }
        else if (template.getMaximum() == 0){
            MessageManager.getInstance().sendMessage("wizard_maximum_not_set", player);
            return false;
        }
        else {
            return true;
        }
    }

}
