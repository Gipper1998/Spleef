package me.gipper1998.spleef.setup;

import lombok.Getter;
import me.gipper1998.spleef.file.ArenaManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.utils.ItemBuilder;
import me.gipper1998.spleef.file.MessageManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetupWizard {

    private ArenaSetupTemplate template;

    @Getter
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
    @Getter
    private boolean findMin = false;
    @Getter
    private boolean findMax = false;

    public SetupWizard(Player p, String name){
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

    public void onPlayerInteract(ItemStack item) {
        if (item.equals(itemOne)) {
            exitWizard(false);
            MessageManager.getInstance().sendMessage("exit_wizard", p);
        }
        if (item.equals(itemSix)) {
            MessageManager.getInstance().sendMessage("wizard_maximum_chat", p);
            findMax = true;
        }
        if (item.equals(itemFive)) {
            MessageManager.getInstance().sendMessage("wizard_minimum_chat", p);
            findMin = true;
        }
        if (item.equals(itemTwo)) {
            template.setArena(p.getLocation());
            MessageManager.getInstance().sendMessage("wizard_arena_spawn_set", p);
        }
        if (item.equals(itemFour)) {
            template.setSpectate(p.getLocation());
            MessageManager.getInstance().sendMessage("wizard_spectator_spawn_set", p);
        }
        if (item.equals(itemThree)) {
            template.setLobby(p.getLocation());
            MessageManager.getInstance().sendMessage("wizard_lobby_spawn_set", p);
        }
        if (item.equals(itemSeven)) {
            if (isComplete()) {
                exitWizard(true);
                MessageManager.getInstance().sendMessage("wizard_arena_saved", p);
            }
        }
    }

    public void onChat(String message) {
        if (isNumeric(message)) {
            if (findMin) {
                template.setMinimum(Integer.parseInt(message));
                MessageManager.getInstance().sendNumberMessage("wizard_minimum_set", Integer.parseInt(message), p);
                findMin = false;
                return;
            }
            if (findMax) {
                template.setMaximum(Integer.parseInt(message));
                MessageManager.getInstance().sendNumberMessage("wizard_maximum_set", Integer.parseInt(message), p);
                findMax = false;
                return;
            }
        }
    }

    public void exitWizard(boolean finished){
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

    private boolean isNumeric(String temp){
        try {
            Double.parseDouble(temp);
            return true;
        } catch (NumberFormatException e) {return false;}
    }

    private boolean isComplete(){
        if (template.getArena() == null){
            MessageManager.getInstance().sendMessage("wizard_arena_not_set", p);
            return false;
        }
        else if (template.getSpectate() == null){
            MessageManager.getInstance().sendMessage("wizard_spectator_not_set", p);
            return false;
        }
        else if (template.getMinimum() == 0){
            MessageManager.getInstance().sendMessage("wizard_minimum_not_set", p);
            return false;
        }
        else if (template.getMaximum() == 0){
            MessageManager.getInstance().sendMessage("wizard_maximum_not_set", p);
            return false;
        }
        else {
            return true;
        }
    }

}
