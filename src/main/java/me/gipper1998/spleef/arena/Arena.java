package me.gipper1998.spleef.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class Arena {

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

    public Arena(int minimum, int maximum, String name, Location arena, Location lobby, Location spectate){
        this.spectate = spectate;
        this.arena = arena;
        this.lobby = lobby;
        this.name = name;
        this.maximum = maximum;
        this.minimum = minimum;
    }
}
