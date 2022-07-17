package me.gipper1998.spleef.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionBuilder {

    private PotionEffectType effect;
    private int duration;
    private int amp;

    public PotionBuilder(PotionEffectType effect, int duration, int amp){
        this.effect = effect;
        this.duration = duration;
        this.amp = amp;
    }

    public void addPotionEffect(Player p){
        p.addPotionEffect(new PotionEffect(effect, duration, amp));
    }
}
