package ws.billy.speedbuilderspractice.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWinEvent extends Event {
    private static HandlerList handlers;

    static {
        PlayerWinEvent.handlers = new HandlerList();
    }

    private final Player player;

    public PlayerWinEvent(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return PlayerWinEvent.handlers;
    }
}
