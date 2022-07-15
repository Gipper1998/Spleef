package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    public static void sendMessage(String path, Player p){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix =messages.getString("prefix");
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(String path, int num, Player p){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix = messages.getString("prefix");
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", prefix);
        message = message.replaceAll("<minimum>", Integer.toString(num));
        message = message.replaceAll("<maximum>", Integer.toString(num));
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(String path, String name, Player p){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix = messages.getString("prefix");
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", prefix);
        message = message.replaceAll("<arena>", name);
        message = message.replaceAll("<arenaname>", name);
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendConsoleMessage(String path){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix = messages.getString("prefix");
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", prefix);
        message = translateHEX(message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendCustomConsole(String message){
        message = translateHEX(message);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendLeaderboardStringList(Player sender, String wins, String losses, String target){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix = messages.getString("prefix");
        List<String> messageList = messages.getStringList("stats");
        for (String message : messageList){
            if (message.isEmpty()){
                return;
            }
            message = message.replaceAll("<prefix>", prefix);
            message = message.replaceAll("<playername>", target);
            message = message.replaceAll("<wins>", wins);
            message = message.replaceAll("<losses>", losses);
            message = translateHEX(message);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void sendStringList(String path, Player p){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        List<String> messageList = messages.getStringList(path);
        for (String message : messageList){
            String prefix = Spleef.main.messages.getConfig().getString("prefix");
            message = message.replaceAll("<prefix>", prefix);
            if (message.isEmpty()){
                return;
            }
            message = translateHEX(message);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static List<String> getStringList(String path){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        List<String> messageList = messages.getStringList(path);
        List<String> sendMessages = new ArrayList<>();
        for (String message : messageList){
            String prefix = Spleef.main.messages.getConfig().getString("prefix");
            message = message.replaceAll("<prefix>", prefix);
            if (message.isEmpty()){
                return null;
            }
            message = translateHEX(message);
            sendMessages.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        return sendMessages;
    }

    public static String getString(String path){
        FileConfiguration messages = Spleef.main.messages.getConfig();
        String prefix = messages.getString("prefix");
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return "";
        }
        message = translateHEX(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String translateHEX(String message){
        final char colorChar = ChatColor.COLOR_CHAR;
        Pattern HEX = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = HEX.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }
        return matcher.appendTail(buffer).toString();
    }
}
