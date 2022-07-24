package me.gipper1998.spleef.utils;

import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;

public class TNTBuilder {
    public static TNTBuilder tnt;
    private int delayFuse = 3;
    private int yAbove = 5;

    public static TNTBuilder getInstance(){
        if (tnt == null){
            return new TNTBuilder();
        }
        return tnt;
    }

    public void create(Location location, String name){
        TNTPrimed tntPrimed = location.getWorld().spawn(location.add(0, yAbove, 0), TNTPrimed.class);
        tntPrimed.setCustomName(name);
        tntPrimed.setFuseTicks(delayFuse * 20);
    }

}
