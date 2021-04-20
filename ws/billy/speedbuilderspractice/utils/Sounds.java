 

package ws.billy.speedbuilderspractice.utils;

import com.google.common.base.Enums;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum Sounds
{
    BLOCK_NOTE_BLOCK_HAT(new String[] { "block.note.hat", "note.hat" }), 
    BLOCK_NOTE_BLOCK_PLING(new String[] { "block.note.pling", "note.pling" }), 
    BLOCK_WOOD_BREAK(new String[] { "block.wood.break", "dig.wood" }), 
    ENTITY_BLAZE_HURT(new String[] { "entity.blaze.hurt", "mob.blaze.hit" }), 
    ENTITY_BLAZE_SHOOT(new String[] { "entity.blaze.shoot", "mob.ghast.fireball" }), 
    ENTITY_ELDER_GUARDIAN_CURSE(new String[] { "entity.elder_guardian.curse", "mob.guardian.curse" }), 
    ENTITY_PLAYER_LEVELUP(new String[] { "entity.player.levelup", "random.levelup" }), 
    ENTITY_ZOMBIE_VILLAGER_CURE(new String[] { "entity.zombie_villager.cure", "mob.zombie.remedy" });
    
    private String[] deprecatedSounds;
    
    private Sounds(final String[] deprecatedSounds) {
        this.deprecatedSounds = deprecatedSounds;
    }
    
    public void play(final Player player, final float n, final float n2) {
        if (Enums.getIfPresent((Class)Sound.class, "AMBIENT_UNDERWATER_ENTER").isPresent()) {
            player.playSound(player.getLocation(), Sound.valueOf(this.toString()), n, n2);
            return;
        }
        final String[] deprecatedSounds = this.deprecatedSounds;
        for (int length = deprecatedSounds.length, i = 0; i < length; ++i) {
            player.playSound(player.getLocation(), deprecatedSounds[i], n, n2);
        }
    }
}
