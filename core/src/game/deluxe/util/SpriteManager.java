package game.deluxe.util;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    public Map<String, SpriteSheet> spriteSheetMap = new HashMap<>();

    public SpriteManager(){
        spriteSheetMap = new HashMap<>();
    }

    public void create(String path){
        if (spriteSheetMap.containsKey(path)) return;
        SpriteSheet sheet = new SpriteSheet(path);
        spriteSheetMap.put(path, sheet);
    }
}
