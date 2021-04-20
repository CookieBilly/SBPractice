package ws.billy.speedbuilderspractice.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPerfectEvent extends Event {
    private static HandlerList handlers;

    static {
        PlayerPerfectEvent.handlers = new HandlerList();
    }

    private final Player player;
    private final float time;

    public PlayerPerfectEvent(final Player player, final float time) {
        this.player = player;
        this.time = time;
    }

    public Player getPlayer() {
        return this.player;
    }

    public float getTime() {
        return this.time;
    }

    public HandlerList getHandlers() {
        return PlayerPerfectEvent.handlers;
    }
}
