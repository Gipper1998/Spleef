package me.gipper1998.spleef.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
public class Arena {

    @Getter
    private String name;

    @Getter
    private Location arena = null;

    @Getter
    private Location lobby = null;

    @Getter
    private Location spectate = null;

    @Getter
    private int minimum = 0;

    @Getter
    private int maximum = 0;

}
