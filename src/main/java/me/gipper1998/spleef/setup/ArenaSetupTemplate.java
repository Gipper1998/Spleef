package me.gipper1998.spleef.setup;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class ArenaSetupTemplate {

    @Setter @Getter
    public String name;

    @Setter @Getter
    public Location arena = null;

    @Setter @Getter
    public Location lobby = null;

    @Setter @Getter
    public Location spectate = null;

    @Setter @Getter
    public int minimum = 0;

    @Setter @Getter
    public int maximum = 0;

    public ArenaSetupTemplate(String name){
        this.name = name;
    }

}
