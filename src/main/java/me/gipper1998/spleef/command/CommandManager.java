package me.gipper1998.spleef.command;

import me.gipper1998.spleef.arena.Arena;
import me.gipper1998.spleef.file.ArenaManager;
import me.gipper1998.spleef.file.ConfigManager;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.game.GameManager;
import me.gipper1998.spleef.file.MessageManager;
import me.gipper1998.spleef.setup.InSetupWizard;
import me.gipper1998.spleef.setup.SetupWizard;
import me.gipper1998.spleef.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CommandManager implements TabExecutor {

    private Arena arena;
    private GameManager gm;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            MessageManager.getInstance().sendConsoleMessage("no_console");
            return true;
        }
        else {
            Player p = (Player) sender;
            if (args.length == 0) {
                viewCommands(p);
                return false;
            }

            // Create
            if (args[0].equalsIgnoreCase("create")) {
                if (!p.hasPermission("spleef.admin")) {
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                if (args.length < 2) {
                    MessageManager.getInstance().sendMessage("no_name_wizard", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                if (ArenaManager.getInstance().getArenaNames() != null) {
                    if (ArenaManager.getInstance().getArenaNames().contains(name)) {
                        MessageManager.getInstance().sendMessage("wizard_arena_exists", p);
                        return false;
                    }
                }
                if (InSetupWizard.getInstance().addPlayer(p)){
                    MessageManager.getInstance().sendMessage("in_wizard", p);
                    new SetupWizard(p, name);
                }
                return true;
            }

            // Delete
            if (args[0].equalsIgnoreCase("delete")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                if (args.length < 2){
                    MessageManager.getInstance().sendMessage("no_name", p);
                    return false;
                }
                if (name.isEmpty()) {
                    return false;
                }
                arena = ArenaManager.getInstance().findArena(name);
                gm = ArenaManager.getInstance().findGame(name);
                if (arena == null){
                    MessageManager.getInstance().sendArenaNameMessage("arena_does_not_exist", name, p);
                    return false;
                }
                else {
                    ArenaManager.getInstance().deleteArena(name.toUpperCase());
                    MessageManager.getInstance().sendMessage("arena_deleted", p);
                    return true;
                }
            }

            // Disable Arena
            if (args[0].equalsIgnoreCase("disable")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                if (args.length < 2){
                    MessageManager.getInstance().sendMessage("no_name", p);
                    return false;
                }
                if (name.isEmpty()) {
                    return false;
                }
                arena = ArenaManager.getInstance().findArena(name);
                if (arena == null){
                    MessageManager.getInstance().sendArenaNameMessage("arena_does_not_exist", name, p);
                    return false;
                }
                else {
                    ArenaManager.getInstance().disableArena(p, arena);
                    return true;
                }
            }

            // Enable Arena
            if (args[0].equalsIgnoreCase("enable")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                if (args.length < 2){
                    MessageManager.getInstance().sendMessage("no_name", p);
                    return false;
                }
                if (name.isEmpty()) {
                    return false;
                }
                arena = ArenaManager.getInstance().findArena(name);
                if (arena == null){
                    MessageManager.getInstance().sendArenaNameMessage("arena_does_not_exist", name, p);
                    return false;
                }
                else {
                    ArenaManager.getInstance().enableArena(p, arena);
                    return true;
                }
            }

            // Reload Plugin
            if (args[0].equalsIgnoreCase("reload")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                MessageManager.getInstance().reloadMessages();
                ArenaManager.getInstance().reloadArenas();
                InSetupWizard.getInstance().removeAllPlayers();
                ConfigManager.getInstance().reloadConfig();
                PlayerStatManager.getInstance().reloadStats();
                SignManager.getInstance().reloadSigns();
                MessageManager.getInstance().sendMessage("reloaded", p);
                return true;
            }

            // Join
            if (args[0].equalsIgnoreCase("join")) {
                if (args.length < 2){
                    MessageManager.getInstance().sendMessage("no_name", p);
                    return false;
                }
                String name = args[1].toUpperCase();
                if (name.isEmpty()){
                    MessageManager.getInstance().sendMessage("no_name", p);
                    return false;
                }
                arena = ArenaManager.getInstance().findArena(name);
                gm = ArenaManager.getInstance().findGame(name);
                if (arena == null){
                    MessageManager.getInstance().sendArenaNameMessage("arena_does_not_exist", name, p);
                    return false;
                }
                gm = ArenaManager.getInstance().findGame(name);
                gm.addPlayer(p);
                return true;
            }

            // Leave
            if (args[0].equalsIgnoreCase("leave")){
                gm = ArenaManager.getInstance().findPlayerInGame(p);
                if (gm != null) {
                    gm.removePlayer(p);
                    return true;
                }
                else {
                    MessageManager.getInstance().sendMessage("player_not_in_game", p);
                    return false;
                }
            }

            // Set Wins
            if (args[0].equalsIgnoreCase("setWins")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                else {
                    if (args.length < 2) {
                        MessageManager.getInstance().sendMessage("no_player_entry", p);
                        return false;
                    }
                    if (args.length < 3){
                        MessageManager.getInstance().sendMessage("no_number_entry", p);
                        return false;
                    }
                    if (!isNumeric(args[2])){
                        MessageManager.getInstance().sendMessage("no_number_entry", p);
                        return false;
                    }
                    try {
                        OfflinePlayer temp = Bukkit.getOfflinePlayer(PlayerStatManager.getInstance().findPlayer(args[1]));
                        PlayerStatManager.getInstance().setWinPoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        MessageManager.getInstance().sendMessage("stat_set", p);
                        return true;
                    }
                    catch (Exception e){
                        MessageManager.getInstance().sendMessage("player_does_not_exist", p);
                        return false;
                    }
                }
            }

            // Set Losses
            if (args[0].equalsIgnoreCase("setLosses")){
                if (!p.hasPermission("spleef.admin")){
                    MessageManager.getInstance().sendMessage("no_perms", p);
                    return false;
                }
                else {
                    if (args.length < 2) {
                        MessageManager.getInstance().sendMessage("no_player_entry", p);
                        return false;
                    }
                    if (args.length < 3){
                        MessageManager.getInstance().sendMessage("no_number_entry", p);
                        return false;
                    }
                    if (!isNumeric(args[2])){
                        MessageManager.getInstance().sendMessage("no_number_entry", p);
                        return false;
                    }
                    try {
                        OfflinePlayer temp = Bukkit.getOfflinePlayer(PlayerStatManager.getInstance().findPlayer(args[1]));
                        PlayerStatManager.getInstance().setLosePoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        MessageManager.getInstance().sendMessage("stat_set", p);
                        return true;
                    }
                    catch (Exception e){
                        MessageManager.getInstance().sendMessage("player_does_not_exist", p);
                        return false;
                    }
                }
            }

            // See Player Stats
            if (args[0].equalsIgnoreCase("stats")) {
                if (args.length < 2) {
                    int wins = PlayerStatManager.getInstance().getWins(p.getUniqueId());
                    int losses = PlayerStatManager.getInstance().getLosses(p.getUniqueId());
                    statBoard(p, wins, losses, p);
                    return true;
                } else {
                    if (p.hasPermission("spleef.otherstats") || p.hasPermission("spleef.admin")) {
                        try {
                            OfflinePlayer temp = Bukkit.getOfflinePlayer(PlayerStatManager.getInstance().findPlayer(args[1]));
                            int wins = PlayerStatManager.getInstance().getWins(temp.getUniqueId());
                            int losses = PlayerStatManager.getInstance().getLosses(temp.getUniqueId());
                            statBoard(p, wins, losses, temp);
                            return true;
                        } catch (Exception e) {
                            MessageManager.getInstance().sendMessage("player_does_not_exist", p);
                        }
                    }
                    else {
                        MessageManager.getInstance().sendMessage("no_perms", p);
                        return false;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1){
            if (sender.hasPermission("spleef.admin")) {
                arguments.add("create");
                arguments.add("delete");
                arguments.add("setWins");
                arguments.add("setLosses");
                arguments.add("enable");
                arguments.add("disable");
            }
            arguments.add("join");
            arguments.add("leave");
            arguments.add("stats");
            return arguments;
        }
        else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("create")) {
                arguments.add("<type_name>");
                return arguments;
            }
            if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("stats")){
                if (args[0].equalsIgnoreCase("stats") && sender.hasPermission("spleef.otherstats")){
                    return PlayerStatManager.getInstance().getPlayerNames();
                }
                return null;
            }
            if (args[0].equalsIgnoreCase("setWins") || args[0].equalsIgnoreCase("setLosses")){
                if (args.length > 2){
                    arguments.add("<number>");
                    return arguments;
                }
                else {
                    return PlayerStatManager.getInstance().getPlayerNames();
                }
            }
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
                return ArenaManager.getInstance().getArenaNames();
            }
        }
        return null;
    }

    private void statBoard(Player sender, int wins, int losses, OfflinePlayer target){
        MessageManager.getInstance().sendLeaderboardStringList(sender, Integer.toString(wins), Integer.toString(losses), target);
    }

    private void viewCommands(Player p){
        if (p.hasPermission("spleef.admin")){
            MessageManager.getInstance().sendStringList("commands_page_admin", p);
        }
        else {
            MessageManager.getInstance().sendStringList("commands_page_player", p);
        }
        return;
    }

    private boolean isNumeric(String temp){
        try {
            Double.parseDouble(temp);
            return true;
        } catch (NumberFormatException e) {return false;}
    }

}
