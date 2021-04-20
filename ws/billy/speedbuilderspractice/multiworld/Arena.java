 

package ws.billy.speedbuilderspractice.multiworld;

import java.util.Iterator;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.utils.GameState;
import java.util.HashMap;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scoreboard.Scoreboard;
import java.util.ArrayList;

public class Arena
{
    public static ArrayList<Arena> arenaObjects;
    public Scoreboard gameScoreboard;
    private String name;
    private int neededPlayers;
    private ArrayList<String> players;
    private int startTime;
    private int startTimerID;
    private float gameStartTime;
    private int gameStartTimerID;
    private int showcaseTime;
    private int showcaseTimerID;
    private float buildTime;
    private int buildTimerID;
    private float judgeTime;
    private int judgeTimerID1;
    private int judgeTimerID2;
    private int judgeTimerID3;
    private int judgeTimerID4;
    private int judgeTimerID5;
    private int judgeTimerID6;
    private ArmorStand judgedPlayerArmorStand;
    private ArrayList<String> unusedTemplates;
    private ArrayList<String> usedTemplates;
    private HashMap<Integer, String> currentBuildBlocks;
    private HashMap<String, Float> playersDoubleJumpCooldowned;
    private HashMap<String, Integer> playerPercent;
    private HashMap<String, Scoreboard> playerStartScoreboard;
    private HashMap<String, String> playersKit;
    private HashMap<String, String> plots;
    private int buildTimeSubtractor;
    private int currentRound;
    private int maxPlayers;
    private Object guardian;
    private String currentBuildDisplayName;
    private String currentBuildRawName;
    private String judgedPlayerName;
    private String firstPlace;
    private String secondPlace;
    private String thirdPlace;
    private GameState gameState;
    
    public Arena(final String name, final int startTime, final int n, final int showcaseTime, final int n2, final int n3, final int neededPlayers) {
        this.players = new ArrayList<String>();
        this.judgedPlayerArmorStand = null;
        this.unusedTemplates = new ArrayList<String>();
        this.usedTemplates = new ArrayList<String>();
        this.currentBuildBlocks = new HashMap<Integer, String>();
        this.playersDoubleJumpCooldowned = new HashMap<String, Float>();
        this.playerPercent = new HashMap<String, Integer>();
        this.playerStartScoreboard = new HashMap<String, Scoreboard>();
        this.playersKit = new HashMap<String, String>();
        this.plots = new HashMap<String, String>();
        this.buildTimeSubtractor = 0;
        this.currentRound = 0;
        this.guardian = null;
        this.currentBuildDisplayName = null;
        this.currentBuildRawName = null;
        this.judgedPlayerName = null;
        this.firstPlace = null;
        this.secondPlace = null;
        this.thirdPlace = null;
        this.name = name;
        this.startTime = startTime;
        this.gameStartTime = (float)n;
        this.showcaseTime = showcaseTime;
        this.buildTime = (float)n2;
        this.judgeTime = (float)n3;
        this.neededPlayers = neededPlayers;
        Arena.arenaObjects.add(this);
    }
    
    public Scoreboard getGameScoreboard() {
        return this.gameScoreboard;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getNeededPlayers() {
        return this.neededPlayers;
    }
    
    public void setNeededPlayers(final int neededPlayers) {
        this.neededPlayers = neededPlayers;
    }
    
    public ArrayList<String> getPlayers() {
        return this.players;
    }
    
    public Object getGuardian() {
        return this.guardian;
    }
    
    public void setGuardian(final Object guardian) {
        this.guardian = guardian;
    }
    
    public int getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final int startTime) {
        this.startTime = startTime;
    }
    
    public int getStartTimerID() {
        return this.startTimerID;
    }
    
    public void setStartTimerID(final int startTimerID) {
        this.startTimerID = startTimerID;
    }
    
    public float getGameStartTime() {
        return this.gameStartTime;
    }
    
    public void setGameStartTime(final float gameStartTime) {
        this.gameStartTime = gameStartTime;
    }
    
    public int getGameStartTimerID() {
        return this.gameStartTimerID;
    }
    
    public void setGameStartTimerID(final int gameStartTimerID) {
        this.gameStartTimerID = gameStartTimerID;
    }
    
    public int getShowCaseTime() {
        return this.showcaseTime;
    }
    
    public void setShowCaseTime(final int showcaseTime) {
        this.showcaseTime = showcaseTime;
    }
    
    public int getShowCaseTimerID() {
        return this.showcaseTimerID;
    }
    
    public void setShowCaseTimerID(final int showcaseTimerID) {
        this.showcaseTimerID = showcaseTimerID;
    }
    
    public float getBuildTime() {
        return this.buildTime;
    }
    
    public void setBuildTime(final float buildTime) {
        this.buildTime = buildTime;
    }
    
    public int getBuildTimerID() {
        return this.buildTimerID;
    }
    
    public void setBuildTimerID(final int buildTimerID) {
        this.buildTimerID = buildTimerID;
    }
    
    public float getJudgeTime() {
        return this.judgeTime;
    }
    
    public void setJudgeTime(final float judgeTime) {
        this.judgeTime = judgeTime;
    }
    
    public int getJudgeTimerID1() {
        return this.judgeTimerID1;
    }
    
    public void setJudgeTimerID1(final int judgeTimerID1) {
        this.judgeTimerID1 = judgeTimerID1;
    }
    
    public int getJudgeTimerID2() {
        return this.judgeTimerID2;
    }
    
    public void setJudgeTimerID2(final int judgeTimerID2) {
        this.judgeTimerID2 = judgeTimerID2;
    }
    
    public int getJudgeTimerID3() {
        return this.judgeTimerID3;
    }
    
    public void setJudgeTimerID3(final int judgeTimerID3) {
        this.judgeTimerID3 = judgeTimerID3;
    }
    
    public int getJudgeTimerID4() {
        return this.judgeTimerID4;
    }
    
    public void setJudgeTimerID4(final int judgeTimerID4) {
        this.judgeTimerID4 = judgeTimerID4;
    }
    
    public int getJudgeTimerID5() {
        return this.judgeTimerID5;
    }
    
    public void setJudgeTimerID5(final int judgeTimerID5) {
        this.judgeTimerID5 = judgeTimerID5;
    }
    
    public int getJudgeTimerID6() {
        return this.judgeTimerID6;
    }
    
    public void setJudgeTimerID6(final int judgeTimerID6) {
        this.judgeTimerID6 = judgeTimerID6;
    }
    
    public String getJudgedPlayerName() {
        return this.judgedPlayerName;
    }
    
    public void setJudgedPlayerName(final String judgedPlayerName) {
        this.judgedPlayerName = judgedPlayerName;
    }
    
    public ArmorStand getJudgedPlayerArmorStand() {
        return this.judgedPlayerArmorStand;
    }
    
    public void setJudgedPlayerArmorStand(final ArmorStand judgedPlayerArmorStand) {
        this.judgedPlayerArmorStand = judgedPlayerArmorStand;
    }
    
    public ArrayList<String> getUnusedTemplates() {
        return this.unusedTemplates;
    }
    
    public ArrayList<String> getUsedTemplates() {
        return this.usedTemplates;
    }
    
    public HashMap<Integer, String> getCurrentBuildBlocks() {
        return this.currentBuildBlocks;
    }
    
    public HashMap<String, Float> getPlayersDoubleJumpCooldowned() {
        return this.playersDoubleJumpCooldowned;
    }
    
    public HashMap<String, Integer> getPlayerPercent() {
        return this.playerPercent;
    }
    
    public HashMap<String, Scoreboard> getPlayerStartScoreboard() {
        return this.playerStartScoreboard;
    }
    
    public HashMap<String, String> getPlayersKit() {
        return this.playersKit;
    }
    
    public HashMap<String, String> getPlots() {
        return this.plots;
    }
    
    public int getBuildTimeSubtractor() {
        return this.buildTimeSubtractor;
    }
    
    public void setBuildTimeSubtractor(final int buildTimeSubtractor) {
        this.buildTimeSubtractor = buildTimeSubtractor;
    }
    
    public int getCurrentRound() {
        return this.currentRound;
    }
    
    public void setCurrentRound(final int currentRound) {
        this.currentRound = currentRound;
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public String getCurrentBuildDisplayName() {
        return this.currentBuildDisplayName;
    }
    
    public void setCurrentBuildDisplayName(final String currentBuildDisplayName) {
        this.currentBuildDisplayName = currentBuildDisplayName;
    }
    
    public String getCurrentBuildRawName() {
        return this.currentBuildRawName;
    }
    
    public void setCurrentBuildRawName(final String currentBuildRawName) {
        this.currentBuildRawName = currentBuildRawName;
    }
    
    public String getFirstPlace() {
        return this.firstPlace;
    }
    
    public void setFirstPlace(final String firstPlace) {
        this.firstPlace = firstPlace;
    }
    
    public String getSecondPlace() {
        return this.secondPlace;
    }
    
    public void setSecondPlace(final String secondPlace) {
        this.secondPlace = secondPlace;
    }
    
    public String getThirdPlace() {
        return this.thirdPlace;
    }
    
    public void setThirdPlace(final String thirdPlace) {
        this.thirdPlace = thirdPlace;
    }
    
    public GameState getGameState() {
        return this.gameState;
    }
    
    public void setGameState(final GameState gameState) {
        this.gameState = gameState;
    }
    
    public void sendMessage(final String s) {
        final Iterator<String> iterator = this.players.iterator();
        while (iterator.hasNext()) {
            Bukkit.getPlayer((String)iterator.next()).sendMessage(s);
        }
    }
    
    static {
        Arena.arenaObjects = new ArrayList<Arena>();
    }
}
