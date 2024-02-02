package game.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<String, Texture> textureMap;

    public TextureManager(){
        textureMap = new HashMap<>();
    }

    public void create(TextureRequest textureRequest){
        String path = textureRequest.getName();
        if (textureMap.containsKey(path)) return;
        Texture texture = new Texture(Gdx.files.internal(path + ".png"));
        textureMap.put(path, texture);
    }

    public Map<String, Texture> getTextureMap() {
        return textureMap;
    }

    public void dispose(){
        for (Texture texture: textureMap.values()) texture.dispose();
    }
}
