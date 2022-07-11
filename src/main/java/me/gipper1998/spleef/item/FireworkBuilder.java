package me.gipper1998.spleef.item;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkBuilder {

    @Getter
    private Firework fw;
    @Getter
    private FireworkMeta fwm;

    public FireworkBuilder(Location location){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fwm = fw.getFireworkMeta();
        fw.detonate();
    }

    public FireworkBuilder(Location location, int power){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, Color color){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().withColor(color).build());
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, Color color, int effect){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        getEffect(effect, color);
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, Color color, int effect, int type){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        FireworkEffect.Type ft = getType(type);
        if (ft != null) {
            getEffect(effect, color, ft);
        }
        else {
            getEffect(effect, color);
        }
        fw.setFireworkMeta(fwm);
    }

    public void launch(){
        fw.detonate();
    }

    private FireworkEffect.Type getType(int num){
        FireworkEffect.Type ft = null;
        switch (num) {
            case 1:
                ft = FireworkEffect.Type.BALL;
                break;
            case 2:
                ft = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                ft = FireworkEffect.Type.BURST;
                break;
            case 4:
                ft = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                ft = FireworkEffect.Type.STAR;
                break;
            default:
                ft = null;
                break;
        }
        return ft;
    }

    private void getEffect(int num, Color color){
        switch(num){
            case 1:
                fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());
                break;
            case 2:
                fwm.addEffect(FireworkEffect.builder().withColor(color).trail(true).build());
                break;
            case 3:
                fwm.addEffect(FireworkEffect.builder().withColor(color).trail(true).flicker(true).build());
                break;
            default:
                fwm.addEffect(FireworkEffect.builder().withColor(color).build());
                break;
        }
    }

    private void getEffect(int num, Color color, FireworkEffect.Type ft){
        switch(num){
            case 1:
                fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).with(ft).build());
                break;
            case 2:
                fwm.addEffect(FireworkEffect.builder().withColor(color).trail(true).with(ft).build());
                break;
            case 3:
                fwm.addEffect(FireworkEffect.builder().withColor(color).trail(true).flicker(true).with(ft).build());
                break;
            default:
                fwm.addEffect(FireworkEffect.builder().withColor(color).with(ft).build());
                break;
        }
    }

    private Color color (String color){
        return Color.AQUA;
    }
}
