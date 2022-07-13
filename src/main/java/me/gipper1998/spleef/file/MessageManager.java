package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    public static void getString(String path, Player player){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        String message = Spleef.main.messages.getConfig().getString(path);
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void getString(String path, int num, Player player){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        String message = Spleef.main.messages.getConfig().getString(path);
        message = message.replaceAll("<prefix>", prefix);
        message = message.replaceAll("<minimum>", Integer.toString(num));
        message = message.replaceAll("<maximum>", Integer.toString(num));
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void getString(String path, String name, Player player){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        String message = Spleef.main.messages.getConfig().getString(path);
        message = message.replaceAll("<prefix>", prefix);
        message = message.replaceAll("<arena>", name);
        message = message.replaceAll("<arenaname>", name);
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String getString(String path){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        String message = Spleef.main.messages.getConfig().getString(path);
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return "";
        }
        message = translateHEX(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void getStringList(String path, Player player){
        List<String> messages = Spleef.main.messages.getConfig().getStringList(path);
        for (String message : messages){
            String prefix = Spleef.main.messages.getConfig().getString("prefix");
            message = message.replaceAll("<prefix>", prefix);
            if (message.isEmpty()){
                return;
            }
            message = translateHEX(message);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void sendMessage(String message, Player player){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return;
        }
        message = translateHEX(message);
        if (player == null){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static List<String> getStringList(String path){
        List<String> messages = Spleef.main.messages.getConfig().getStringList(path);
        List<String> sendMessages = new ArrayList<>();
        for (String message : messages){
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
