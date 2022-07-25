package me.gipper1998.spleef.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class GameStoreItems {
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private Player p;
    private double health;
    private double hunger;
    private float xp;

    private int levels;

    private int selectedItemSlot;
    private GameMode gamemode;
    private Location location;

    public GameStoreItems(Player player){
        this.p = player;
        this.inventory = p.getInventory().getContents();
        this.armor = p.getInventory().getArmorContents();
        this.health = p.getHealth();
        this.hunger = p.getFoodLevel();
        this.xp = p.getExp();
        this.gamemode = p.getGameMode();
        this.location = p.getLocation();
        this.selectedItemSlot = player.getInventory().getHeldItemSlot();
        this.levels = p.getLevel();
        if (selectedItemSlot == 8){
            selectedItemSlot = 7;
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setHeldItemSlot(0);
        p.updateInventory();
        p.setLevel(0);
        p.setExp(0);
        for (PotionEffect effect : p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
    }

    public void giveBackItems(){
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(armor);
        p.setGameMode(gamemode);
        p.setExp(xp);
        p.setHealth(health);
        p.getInventory().setHeldItemSlot(selectedItemSlot);
        p.setFoodLevel((int) hunger);
        p.updateInventory();
        p.teleport(location);
        p.setLevel(levels);
        p.setFireTicks(0);
    }
}
