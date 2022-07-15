package me.gipper1998.spleef.command;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.arena.ArenaManager;
import me.gipper1998.spleef.edit.EditWizard;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.setup.SetupWizard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CommandManager implements TabExecutor {

    private Arena arena;
    private GameManager gm;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                viewCommands(p);
                return false;
            }

            // Create
            if (args[0].equalsIgnoreCase("create")) {
                if (!p.hasPermission("spleef.admin")){
                    return false;
                }
                String name = args[1].toUpperCase();
                if (args.length < 2) {
                    MessageManager.sendMessage("no_name_wizard", p);
                    return false;
                } else {
                    MessageManager.sendMessage("in_wizard", p);
                    new SetupWizard(p, name);
                    return true;
                }
            }

            // Delete
            if (args[0].equalsIgnoreCase("delete")){
                if (!p.hasPermission("spleef.admin")){
                    return false;
                }
                String name = args[1].toUpperCase();
                if (args.length < 2){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                if (name.isEmpty()) {
                    return false;
                }
                arena = ArenaManager.findArena(name.toUpperCase());
                if (arena == null){
                    MessageManager.sendMessage("arena_does_not_exist", name.toUpperCase(), p);
                    return false;
                }
                else {
                    ArenaManager.deleteArena(name.toUpperCase());
                    MessageManager.sendMessage("Should be deleted", p);
                    return true;
                }
            }

            // Edit
            if (args[0].equalsIgnoreCase("edit")){
                if (args.length < 2){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                arena = ArenaManager.findArena(name);
                if (name.isEmpty()){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                if (arena == null){
                    MessageManager.sendMessage("arena_does_not_exist", name.toUpperCase(), p);
                    return false;
                }
                new EditWizard(p, arena);
                return true;
            }

            // Set Wins
            if (args[0].equalsIgnoreCase("setWins")){
                if (!p.hasPermission("spleef.admin")){
                    return false;
                }
                else {
                    if (args[1].isEmpty()) {
                        return false;
                    } else {
                        Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                        if (args[2].isEmpty()){
                            return false;
                        }
                        PlayerStatManager.setWinPoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        return true;
                    }
                }
            }

            // Set Losses
            if (args[0].equalsIgnoreCase("setLoses")){
                if (!p.hasPermission("spleef.admin")){
                    return false;
                }
                else {
                    if (args[1].isEmpty()) {
                        return false;
                    } else {
                        Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                        if (args[2].isEmpty()){
                            return false;
                        }
                        PlayerStatManager.setLosePoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        return true;
                    }
                }
            }

            // Reload Plugin
            if (args[0].equalsIgnoreCase("reload")){
                if (!p.hasPermission("spleef.admin")){
                    return false;
                }
                Spleef.main.config.reloadConfig();
                Spleef.main.arenas.reloadConfig();
                Spleef.main.playerStats.reloadConfig();
                Spleef.main.messages.reloadConfig();
                Spleef.main.signs.reloadConfig();
                Spleef.main.signManager.startUpdater();
                ArenaManager.forceQuitArenas();
                ArenaManager.loadArenas();
                MessageManager.sendMessage("reloaded", p);
                return true;
            }

            // Join
            if (args[0].equalsIgnoreCase("join")) {
                if (args.length < 2){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                arena = ArenaManager.findArena(name);
                if (name.isEmpty()){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                if (arena == null){
                    MessageManager.sendMessage("arena_does_not_exist", name.toUpperCase(), p);
                    return false;
                }
                gm = ArenaManager.findGame(name);
                gm.addPlayer(p);
                return true;
            }

            // Leave
            if (args[0].equalsIgnoreCase("leave")){
                if (args.length < 2){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                arena = ArenaManager.findArena(name);
                if (name.isEmpty()){
                    MessageManager.sendMessage("no_name", p);
                    return false;
                }
                if (arena == null){
                    MessageManager.sendMessage("arena_does_not_exist", name.toUpperCase(), p);
                    return false;
                }
                gm = ArenaManager.findGame(name);
                gm.removePlayer(p);
                return true;
            }

            // See Player Stats
            if (args[0].equalsIgnoreCase("stats")) {
                if (args[1].isEmpty()) {
                    int wins = PlayerStatManager.getWins(p.getUniqueId());
                    int losses = PlayerStatManager.getLosses(p.getUniqueId());
                    statBoard(p, wins, losses, p);
                    return true;
                } else {
                    if (p.hasPermission("spleef.otherstats") || p.hasPermission("spleef.admin")) {
                        try {
                            Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                            int wins = PlayerStatManager.getWins(temp.getUniqueId());
                            int losses = PlayerStatManager.getLosses(temp.getUniqueId());
                            statBoard(p, wins, losses, temp);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                }
            }
            return false;
        }
        else {
            MessageManager.sendCustomConsole("<prefix> &cOnly players can use these commands.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            List<String> firstArguments = new ArrayList<>();
            if (sender.hasPermission("spleef.admin")) {
                firstArguments.add("create");
                firstArguments.add("delete");
                firstArguments.add("edit");
            }
            firstArguments.add("join");
            firstArguments.add("leave");
            return firstArguments;
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                List<String> secondArguments = new ArrayList<>();
                secondArguments.add("<type_name>");
                return secondArguments;
            }
            else if (args[0].equalsIgnoreCase("leave")) {
                return null;
            }
            else {
                return ArenaManager.getArenaNames();
            }
        }
        return null;
    }

    private void statBoard(Player sender, int wins, int losses, Player target){
        MessageManager.sendLeaderboardStringList(sender, Integer.toString(wins), Integer.toString(losses), target.getName());
    }

    private void viewCommands(Player p){
        if (p.hasPermission("spleef.admin")){
            MessageManager.sendStringList("commands_page_admin", p);
        }
        else {
            MessageManager.sendStringList("commands_page_player", p);
        }
        return;
    }

}
