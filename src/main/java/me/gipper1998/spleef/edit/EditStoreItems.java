package me.gipper1998.spleef.edit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditStoreItems {
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private Player p;
    private double health;
    private double hunger;
    private float xp;
    private GameMode gamemode;

    public EditStoreItems(Player p){
        this.p = p;
        this.inventory = p.getInventory().getContents();
        this.armor = p.getInventory().getArmorContents();
        this.health = p.getHealth();
        this.hunger = p.getFoodLevel();
        this.xp = p.getExp();
        this.gamemode = p.getGameMode();
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(GameMode.CREATIVE);
        p.updateInventory();
    }

    public void giveBackItems(){
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(armor);
        p.setGameMode(gamemode);
        p.setExp(xp);
        p.setHealth(health);
        p.setFoodLevel((int) hunger);
        p.updateInventory();
    }
}
