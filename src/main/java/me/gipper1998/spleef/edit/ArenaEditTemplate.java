package me.gipper1998.spleef.edit;

import lombok.Getter;
import lombok.Setter;
import me.gipper1998.spleef.arena.Arena;
import org.bukkit.Location;

public class ArenaEditTemplate {

    @Setter @Getter
    private Arena mainArena;

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

    public ArenaEditTemplate(Arena arena){
        this.mainArena = arena;
        this.name = arena.getName();
        this.arena = arena.getArena();
        this.lobby = arena.getLobby();
        this.spectate = arena.getSpectate();
        this.maximum = arena.getMaximum();
        this.minimum = arena.getMinimum();
    }

}
