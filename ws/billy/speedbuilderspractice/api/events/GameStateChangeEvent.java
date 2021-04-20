package ws.billy.speedbuilderspractice.api.events;

import org.bukkit.event.HandlerList;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.event.Event;

public class GameStateChangeEvent extends Event {
    private GameState gameState;
    private int playerCount;
    private static HandlerList handlers;

    public GameStateChangeEvent(final GameState gameState, final int playerCount) {
        this.gameState = gameState;
        this.playerCount = playerCount;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public HandlerList getHandlers() {
        return GameStateChangeEvent.handlers;
    }

    static {
        GameStateChangeEvent.handlers = new HandlerList();
    }
}
