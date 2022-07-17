package me.gipper1998.spleef.setup;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetupStoreItems {

    private ItemStack[] inventory;
    private ItemStack[] armor;
    private Player p;
    private double health;
    private double hunger;
    private float xp;
    private GameMode gamemode;
    private int levels;

    public SetupStoreItems(Player p){
        this.p = p;
        this.inventory = p.getInventory().getContents();
        this.armor = p.getInventory().getArmorContents();
        this.health = p.getHealth();
        this.hunger = p.getFoodLevel();
        this.xp = p.getExp();
        this.gamemode = p.getGameMode();
        this.levels = p.getLevel();
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
        p.setLevel(levels);
    }

}
