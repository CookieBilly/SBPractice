 

package ws.billy.speedbuilderspractice.bungeecord.managers;

import org.bukkit.block.Block;
import org.bukkit.entity.Guardian;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NMSManager
{
    void showActionBar(final Player p0, final String p1);
    
    void showParticleEffect1(final Player p0, final Location p1, final float p2, final float p3, final float p4, final float p5, final int p6);
    
    void showParticleEffect2(final Player p0, final Location p1, final float p2, final float p3, final float p4, final float p5, final int p6);
    
    void showTitle(final Player p0, final int p1, final int p2, final int p3, final String p4, final String p5);
    
    Object setGuardianTarget(final Guardian p0, final int p1);
    
    void setPlayerVisibility(final Player p0, final Player p1, final boolean p2);
    
    void updateBlockConnections(final Block p0);
}
