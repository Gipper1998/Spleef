package me.gipper1998.spleef.setup;

import lombok.Getter;
import me.gipper1998.spleef.utils.ItemStoreManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class InSetupWizard {

    public static InSetupWizard isw;

    @Getter
    private HashMap<Player, ItemStoreManager> inWizard = new HashMap<>();

    public static InSetupWizard getInstance(){
        if (isw == null){
            isw = new InSetupWizard();
        }
        return isw;
    }

    public boolean addPlayer(Player p){
        if (!inWizard.containsKey(p)){
            inWizard.put(p, new ItemStoreManager(p));
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player p){
        if (inWizard.containsKey(p)){
            ItemStoreManager ism = inWizard.get(p);
            ism.giveBackItems();
            inWizard.remove(p);
            return true;
        }
        return false;
    }
}
