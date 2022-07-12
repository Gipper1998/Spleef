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

    public FireworkBuilder(Location location){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fw.detonate();
    }

    public FireworkBuilder(Location location, int power){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, String colorName){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        Color color = color(colorName);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().withColor(color).build());
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, String colorName, int effect){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        Color color = color(colorName);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm = getEffect(fwm, effect, color);
        fw.setFireworkMeta(fwm);
    }

    public FireworkBuilder(Location location, int power, String colorName, int effect, int type){
        this.fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        Color color = color(colorName);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        FireworkEffect.Type ft = getType(type);
        if (ft != null) {
            fwm = getEffect(fwm, effect, color, ft);
        }
        else {
            fwm = getEffect(fwm, effect, color);
        }
        fw.setFireworkMeta(fwm);
    }

    public void addColor(Color color){
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().withColor(color).build());
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

    private FireworkMeta getEffect(FireworkMeta fwm, int num, Color color){
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
        return fwm;
    }

    private FireworkMeta getEffect(FireworkMeta fwm, int num, Color color, FireworkEffect.Type ft){
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
        return fwm;
    }

    private Color color (String color){
        if (color.equalsIgnoreCase("aqua")){
            return Color.AQUA;
        }
        else if (color.equalsIgnoreCase("black")){
            return Color.BLACK;
        }
        else if (color.equalsIgnoreCase("blue")){
            return Color.BLUE;
        }
        else if (color.equalsIgnoreCase("fuchsia")){
            return Color.FUCHSIA;
        }
        else if (color.equalsIgnoreCase("gray")){
            return Color.GRAY;
        }
        else if (color.equalsIgnoreCase("green")){
            return Color.GREEN;
        }
        else if (color.equalsIgnoreCase("lime")){
            return Color.LIME;
        }
        else if (color.equalsIgnoreCase("maroon")){
            return Color.MAROON;
        }
        else if (color.equalsIgnoreCase("navy")){
            return Color.NAVY;
        }
        else if (color.equalsIgnoreCase("olive")){
            return Color.OLIVE;
        }
        else if (color.equalsIgnoreCase("orange")){
            return Color.ORANGE;
        }
        else if (color.equalsIgnoreCase("purple")){
            return Color.PURPLE;
        }
        else if (color.equalsIgnoreCase("red")){
            return Color.RED;
        }
        else if (color.equalsIgnoreCase("silver")){
            return Color.SILVER;
        }
        else if (color.equalsIgnoreCase("teal")){
            return Color.TEAL;
        }
        else if (color.equalsIgnoreCase("white")){
            return Color.WHITE;
        }
        else if (color.equalsIgnoreCase("yellow")){
            return Color.YELLOW;
        }
        else {
            return null;
        }
    }
}
