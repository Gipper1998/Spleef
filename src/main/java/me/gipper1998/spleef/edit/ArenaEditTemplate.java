package me.gipper1998.spleef.edit;

import lombok.Getter;
import lombok.Setter;
import me.gipper1998.spleef.arena.Arena;
import org.bukkit.Location;

public class ArenaEditTemplate {

    @Setter @Getter
    public Arena mainArena;

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
