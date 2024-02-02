package game.engine.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class PixmapManager {
    private final Map<String, Pixmap> pixmapMap;

    public PixmapManager(){
        pixmapMap = new HashMap<>();
    }

    public void create(String path, Texture texture){
        if (pixmapMap.containsKey(path)) return;

    }
}
