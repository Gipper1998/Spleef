package me.gipper1998.spleef.utils;

import lombok.Getter;
import me.gipper1998.spleef.file.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    @Getter
    private ItemStack is;
    @Getter
    private ItemMeta im;

    public ItemBuilder(Material material, String name){
        this.is = new ItemStack(material);
        this.im = is.getItemMeta();
        im.setDisplayName(name);
        im.setUnbreakable(true);
        is.setItemMeta(im);
    }

    public ItemBuilder(Material material, String name, int num){
        this.is = new ItemStack(material, num);
        this.im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        MessageManager.getInstance().sendCustomConsoleMessage("Should be given");
    }

    // TNT
    public ItemBuilder(Player player, String name){
        Location location = player.getLocation();
        location.setY(player.getLocation().getY() + 5);
        TNTPrimed TNT = location.getWorld().spawn(location, TNTPrimed.class);
        TNT.setCustomName(name);
        TNT.setFuseTicks(200);
    }

}
