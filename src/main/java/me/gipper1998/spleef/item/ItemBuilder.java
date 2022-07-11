package me.gipper1998.spleef.item;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    @Getter
    private ItemStack is;

    public ItemBuilder(Material material, String name){
        this.is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setUnbreakable(true);
        is.setItemMeta(im);
    }

    public ItemBuilder(Material material, String name, int num){
        this.is = new ItemStack(material, num);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setUnbreakable(true);
        is.setItemMeta(im);
    }
}
