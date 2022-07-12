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
    private SetupWizard sw;
    private EditWizard ew;
    private GameManager gm;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (sender.hasPermission("spleef.admin")) {
                if (args[0].equalsIgnoreCase("create")) {
                    String name = args[1].toUpperCase();
                    if (args.length < 2){
                        MessageManager.getString("no_name_wizard", player);
                        return false;
                    } else {
                        MessageManager.getString("in_wizard", player);
                        sw = new SetupWizard(player, name);
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];
                    if (args.length < 2){
                        MessageManager.getString("no_name", player);
                        return false;
                    }
                    if (name.isEmpty()) {
                        return false;
                    }
                    arena = ArenaManager.findArena(name.toUpperCase());
                    if (arena == null){
                        MessageManager.getString("arena_does_not_exist", name.toUpperCase(), player);
                        return false;
                    }
                    else {
                        ArenaManager.deleteArena(name.toUpperCase());
                        MessageManager.sendMessage("Should be deleted", player);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("setWins")){
                    if (args[1].isEmpty()){
                        return false;
                    }
                    else {
                        Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                        PlayerStatManager.setWinPoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("setLoses")){
                    if (args[1].isEmpty()){
                        return false;
                    }
                    else {
                        Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                        PlayerStatManager.setLosePoint(temp.getUniqueId(), Integer.parseInt(args[2]));
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")){
                    Spleef.main.config.reloadConfig();
                    Spleef.main.arenas.reloadConfig();
                    Spleef.main.playerStats.reloadConfig();
                    Spleef.main.messages.reloadConfig();
                    ArenaManager.forceQuitArenas();
                    ArenaManager.loadArenas();
                    MessageManager.getString("reloaded", player);
                }
                else if (args[0].equalsIgnoreCase("edit")){
                    if (args.length < 2){
                        MessageManager.getString("no_name", player);
                        return false;
                    }
                    String name = args[1].toUpperCase();
                    arena = ArenaManager.findArena(name);
                    if (name.isEmpty()){
                        MessageManager.getString("no_name", player);
                        return false;
                    }
                    if (arena == null){
                        MessageManager.getString("arena_does_not_exist", name.toUpperCase(), player);
                        return false;
                    }
                    ew = new EditWizard(player, arena);
                    return true;
                }
                else {
                    viewCommands(player);
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("join")) {
                if (args.length < 2){
                    MessageManager.getString("no_name", player);
                    return false;
                }
                String name = args[1].toUpperCase();
                MessageManager.sendMessage(name, player);
                arena = ArenaManager.findArena(name);
                if (name.isEmpty()){
                    MessageManager.getString("no_name", player);
                    return false;
                }
                if (arena == null){
                    MessageManager.getString("arena_does_not_exist", name.toUpperCase(), player);
                    return false;
                }
                gm = ArenaManager.findGame(name);
                gm.addPlayer(player);
                MessageManager.getString("player_success_join", name, player);
                return true;
            }
            else if (args[0].equalsIgnoreCase("leave")){
                List<String> arenas = ArenaManager.getArenaNames();
                for (String arena: arenas){
                    gm = ArenaManager.findGame(arena);
                    if (gm.getPlayersInGame().contains(player)){
                        gm.removePlayer(player);
                        return true;
                    }
                }
                return false;
            }
            else if (args[0].equalsIgnoreCase("stats")){
                if (args[1].isEmpty()){
                    int wins = PlayerStatManager.getWins(player.getUniqueId());
                    int loses = PlayerStatManager.getLosses(player.getUniqueId());
                }
                else {
                    try {
                        Player temp = Bukkit.getPlayer(PlayerStatManager.findPlayer(args[1]));
                        int wins = PlayerStatManager.getWins(temp.getUniqueId());
                        int loses = PlayerStatManager.getLosses(temp.getUniqueId());
                    } catch (Exception e){
                        return false;
                    }
                }
                return true;

            }
            else if (args.length <= 0){
                viewCommands(player);
            }
            else {
                viewCommands(player);
            }
        }
        else {
            MessageManager.sendMessage("<prefix> &cOnly players can use these commands.", null);
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
            }
            firstArguments.add("join");
            firstArguments.add("leave");
            return firstArguments;
        }
        else if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("delete"))) {
            return ArenaManager.getArenaNames();
        }
        else if (args[0].equalsIgnoreCase("create")){
            List<String> secondArguments = new ArrayList<>();
            secondArguments.add("<type_name>");
            return secondArguments;
        }
        return null;
    }

    private void viewCommands(Player player){
        MessageManager.getStringList("commands_page", player);
    }

}
