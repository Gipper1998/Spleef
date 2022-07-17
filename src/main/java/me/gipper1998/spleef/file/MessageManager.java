package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    public static MessageManager mm;

    private FileConfiguration messages;

    public MessageManager(){
        messages = Spleef.main.messages.getConfig();
    }
    public static MessageManager getInstance(){
        if (mm == null){
            mm = new MessageManager();
        }
        return mm;
    }

    public void sendMessage(String path, Player p){
        String message = messages.getString(path);
        if (message.isEmpty()){
            return;
        }
        message = message.replaceAll("<prefix>", getPrefix());
        p.sendMessage(translateHEX(message));
    }

    public void sendNumberMessage(String path, int num, Player p){
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", getPrefix());
        message = message.replaceAll("<minimum>", Integer.toString(num));
        message = message.replaceAll("<maximum>", Integer.toString(num));
        message = message.replaceAll("<time>", Integer.toString(num));
        if (message.isEmpty()){
            return;
        }
        p.sendMessage(translateHEX(message));
    }

    public void sendPlayerNameMessage(String path, Player target, Player p){
        String message = messages.getString(path);
        if (message.isEmpty()){
            return;
        }
        message = message.replaceAll("<prefix>", getPrefix());
        message = message.replaceAll("<playername>", target.getName());
        p.sendMessage(translateHEX(message));
    }

    public void sendStringMessage(String path, String text, Player p) {
        String message = messages.getString(path);
        if (message.isEmpty()){
            return;
        }
        message = message.replaceAll("<prefix>", getPrefix());
        message = message.replaceAll("<arenaname>", text);
        p.sendMessage(translateHEX(message));
    }

    public void sendArenaNameMessage(String path, GameManager gm, Player p){
        String message = messages.getString(path);
        if (message.isEmpty()){
            return;
        }
        message = message.replaceAll("<prefix>", getPrefix());
        message = message.replaceAll("<arenaname>", gm.getArena().getName());
        p.sendMessage(translateHEX(message));
    }

    public void sendConsoleMessage(String path){
        String message = messages.getString(path);
        message = message.replaceAll("<prefix>", getPrefix());
        Bukkit.getConsoleSender().sendMessage(translateHEX(message));
    }

    public void sendCustomConsoleMessage(String message){
        Bukkit.getConsoleSender().sendMessage(translateHEX(message));
    }

    public void sendLeaderboardStringList(Player sender, String wins, String losses, Player target){
        List<String> messageList = messages.getStringList("stats");
        for (String message : messageList){
            if (message.isEmpty()){
                return;
            }
            message = message.replaceAll("<prefix>", getPrefix());
            message = message.replaceAll("<playername>", target.getName());
            message = message.replaceAll("<wins>", wins);
            message = message.replaceAll("<losses>", losses);
            sender.sendMessage(translateHEX(message));
        }
    }

    public void sendStringList(String path, Player p){
        List<String> messageList = messages.getStringList(path);
        for (String message : messageList){
            if (message.isEmpty()){
                return;
            }
            message = message.replaceAll("<prefix>", getPrefix());
            p.sendMessage(translateHEX(message));
        }
    }

    public List<String> getSignStringList(String path){
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

    public String getString(String path){
        String message = messages.getString(path);
        if (message.isEmpty()){
            return "";
        }
        message = message.replaceAll("<prefix>", getPrefix());
        message = translateHEX(message);
        return translateHEX(message);
    }

    private String translateHEX(String message){
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
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private String getPrefix(){
        return messages.getString("prefix");
    }

}
