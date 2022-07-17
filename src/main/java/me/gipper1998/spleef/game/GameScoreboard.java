package me.gipper1998.spleef.game;

import me.gipper1998.spleef.file.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboard {

    public static GameScoreboard gsb;

    private ScoreboardManager manager;

    private Scoreboard board;

    private Objective objective;

    private List<String> scoreboardMessages = new ArrayList<>();

    private Score[] scores;

    private int playerNamePlaceholders = 0;

    public GameScoreboard(){
        scoreboardMessages = MessageManager.getInstance().getScoreboardStringList("scoreboard");
        for (String message : scoreboardMessages){
            if (message.contains("<playername")){
                playerNamePlaceholders++;
            }
        }
    }

    public static GameScoreboard getInstance(){
        if (gsb == null){
            gsb = new GameScoreboard();
        }
        return gsb;
    }

    public void createScoreboard(Player p, GameManager gm){
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective("Spleef", "dummy", scoreboardMessages.get(0));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(board);
        updateScoreboard(gm);
    }

    public void updateScoreboard(GameManager gm){
        int time = gm.getCurrentTime();
        int playerIndex = 0;
        for(Player p : gm.getTotalPlayers()){
            for (int i = 0; i < scoreboardMessages.size(); i++){
                if (scoreboardMessages.get(i).contains("<playername>")){
                    if (playerNamePlaceholders <  gm.getPlayersInGame().size()){
                        resetPlayerScores(i);
                        for (int j = 0; j < playerNamePlaceholders; j++) {
                            scores[i] = objective.getScore(scoreboardMessages.get(i).replaceAll("<playername>", gm.getPlayersInGame().get(playerIndex).getName()));
                            playerIndex++;
                        }
                    }
                    else {
                        scores[i] = objective.getScore(scoreboardMessages.get(i).replaceAll("<playername>", gm.getPlayersInGame().get(playerIndex).getName()));
                        playerIndex++;
                    }
                }
                if (scoreboardMessages.get(i).contains("<time>")){
                    scores[i] = objective.getScore(scoreboardMessages.get(i).replaceAll("<time>", Integer.toString(time)));
                }
                scores[i] = objective.getScore(scoreboardMessages.get(i));
            }
        }
    }

    public void removePlayerFromScoreboard(Player p){
        clearLines();
        p.setScoreboard(manager.getNewScoreboard());
    }

    public void clearLines(){
        for (String line : board.getEntries()){
            board.resetScores(line);
        }
    }

    public void resetPlayerScores(int start){
        for (int i = start; i < playerNamePlaceholders; i++){
            board.resetScores(scores[i].getEntry());
        }
    }

}
