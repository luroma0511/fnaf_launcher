package game.engine.util;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    public Map<String, SpriteSheet> spriteSheetMap;

    public SpriteManager(){
        spriteSheetMap = new HashMap<>();
    }

    public void create(String path, short width){
        if (spriteSheetMap.containsKey(path)) return;
        SpriteSheet sheet = new SpriteSheet(path, width);
        spriteSheetMap.put(path, sheet);
    }

    public void dispose(){
        for (SpriteSheet spriteSheet: spriteSheetMap.values()) spriteSheet.dispose();
    }
}
