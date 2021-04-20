package ws.billy.speedbuilderspractice.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoseEvent extends Event {
    private static HandlerList handlers;

    static {
        PlayerLoseEvent.handlers = new HandlerList();
    }

    private final Player player;

    public PlayerLoseEvent(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return PlayerLoseEvent.handlers;
    }
}
