package me.gipper1998.spleef.edit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditStoreItems {
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private Player player;
    private double health;
    private double hunger;
    private float xp;
    private GameMode gamemode;

    public EditStoreItems(Player player){
        this.player = player;
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.health = player.getHealth();
        this.hunger = player.getFoodLevel();
        this.xp = player.getExp();
        this.gamemode = player.getGameMode();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);
        player.updateInventory();
    }

    public void giveBackItems(){
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);
        player.setGameMode(gamemode);
        player.setExp(xp);
        player.setHealth(health);
        player.setFoodLevel((int) hunger);
        player.updateInventory();
    }
}
