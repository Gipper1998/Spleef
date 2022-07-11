package me.gipper1998.spleef.softdepend;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gipper1998.spleef.Spleef;
import me.gipper1998.spleef.file.PlayerStatManager;
import me.gipper1998.spleef.leaderboard.LeaderboardManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIManager extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "spleef";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gipper1998";
    }

    @Override
    public @NotNull String getVersion() {
        return Spleef.main.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String identifier){
        if (p == null){
            return "";
        }
        if (identifier.equals("wins")){
            return Integer.toString(PlayerStatManager.getWins(p.getUniqueId()));
        }
        else if (identifier.equals("losses")){
            return Integer.toString(PlayerStatManager.getLosses(p.getUniqueId()));
        }
        else if (identifier.contains("top")){
            String[] temp = identifier.split("_");
            String type = temp[1];
            String dataType = temp[2];
            String position = temp[3];
            if (type.equals("wins")){
                if (dataType.equals("player")){
                    return LeaderboardManager.winsPositionName(Integer.parseInt(position));
                }
                else if (dataType.equals("amount")){
                    return LeaderboardManager.winsPositionPoints(Integer.parseInt(position));
                }
                else {
                    return null;
                }
            }
            else if (type.equals("losses")) {
                if (dataType.equals("player")) {
                    return LeaderboardManager.lossesPositionName(Integer.parseInt(position));
                } else if (dataType.equals("amount")) {
                    return LeaderboardManager.lossesPositionPoints(Integer.parseInt(position));
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
