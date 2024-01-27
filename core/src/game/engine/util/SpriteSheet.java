package game.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheet {
    private final Texture texture;
    private final short width;

    public SpriteSheet(String name, short width){
        texture = new Texture(Gdx.files.internal(name + ".png"));
        this.width = width;
    }

    public TextureRegion getRegion(byte fileIndex){
        short x = (short) (fileIndex * width + fileIndex + 1);
        return new TextureRegion(texture, x, 0, width, texture.getHeight());
    }

    public void dispose(){
        texture.dispose();
    }
}
