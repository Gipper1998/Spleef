package me.gipper1998.spleef.setup;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class ArenaSetupTemplate {

    @Setter @Getter
    private String name;

    @Setter @Getter
    private Location arena = null;

    @Setter @Getter
    private Location lobby = null;

    @Setter @Getter
    private Location spectate = null;

    @Setter @Getter
    private int minimum = 0;

    @Setter @Getter
    private int maximum = 0;

    public ArenaSetupTemplate(String name){
        this.name = name;
    }

}
