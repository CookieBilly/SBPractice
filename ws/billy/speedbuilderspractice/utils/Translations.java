

package ws.billy.speedbuilderspractice.utils;

import org.bukkit.ChatColor;
import java.util.HashMap;

public class Translations
{
    public static HashMap<String, String> messages;
    
    public static String translate(final String key) {
        return ChatColor.stripColor((String)Translations.messages.get(key));
    }
    
    static {
        Translations.messages = new HashMap<String, String>();
    }
}
