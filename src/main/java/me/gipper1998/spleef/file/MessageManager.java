package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        if (message.contains("#")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder stringBuilder = new StringBuilder("");
                for (char c : ch) {
                    stringBuilder.append("&" + c);
                }
                message = message.replace(hexCode, stringBuilder.toString());
                matcher = pattern.matcher(message);
            }
        }
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
        if (message.contains("#")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder stringBuilder = new StringBuilder("");
                for (char c : ch) {
                    stringBuilder.append("&" + c);
                }
                message = message.replace(hexCode, stringBuilder.toString());
                matcher = pattern.matcher(message);
            }
        }
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
        if (message.contains("#")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder stringBuilder = new StringBuilder("");
                for (char c : ch) {
                    stringBuilder.append("&" + c);
                }
                message = message.replace(hexCode, stringBuilder.toString());
                matcher = pattern.matcher(message);
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String getString(String path){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        String message = Spleef.main.messages.getConfig().getString(path);
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return "";
        }
        if (message.contains("#")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder stringBuilder = new StringBuilder("");
                for (char c : ch) {
                    stringBuilder.append("&" + c);
                }
                message = message.replace(hexCode, stringBuilder.toString());
                matcher = pattern.matcher(message);
            }
        }
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
            if (message.contains("#")) {
                Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
                Matcher matcher = pattern.matcher(message);
                while (matcher.find()) {
                    String hexCode = message.substring(matcher.start(), matcher.end());
                    String replaceSharp = hexCode.replace('#', 'x');
                    char[] ch = replaceSharp.toCharArray();
                    StringBuilder stringBuilder = new StringBuilder("");
                    for (char c : ch) {
                        stringBuilder.append("&" + c);
                    }
                    message = message.replace(hexCode, stringBuilder.toString());
                    matcher = pattern.matcher(message);
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void sendMessage(String message, Player player){
        String prefix = Spleef.main.messages.getConfig().getString("prefix");
        message = message.replaceAll("<prefix>", prefix);
        if (message.isEmpty()){
            return;
        }
        if (message.contains("#")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexCode = message.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder stringBuilder = new StringBuilder("");
                for (char c : ch) {
                    stringBuilder.append("&" + c);
                }
                message = message.replace(hexCode, stringBuilder.toString());
                matcher = pattern.matcher(message);
            }
        }
        if (player == null){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

}
