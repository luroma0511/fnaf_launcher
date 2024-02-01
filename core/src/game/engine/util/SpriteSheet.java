package game.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteSheet {
    private final Texture texture;
    private final short width;

    public SpriteSheet(String name, short width){
        texture = new Texture(Gdx.files.internal(name + ".png"));
        this.width = width;
    }

    public Sprite getRegion(byte fileIndex){
        short x = (short) (fileIndex * width + fileIndex + 1);
//        TextureRegion region = new TextureRegion(texture, x, 0, width, texture.getHeight());
        Sprite sprite = new Sprite(texture, x, 0, width, texture.getHeight());
        if (width == 1024){
            sprite.setSize(1280, 720);
        }
        return sprite;
    }

    public void dispose(){
        texture.dispose();
    }
}
