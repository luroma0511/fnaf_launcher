package util.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.TextureHandler;

public class MenuUI {
    public final TextureRegion arrow;
    public final TextureRegion play;
    public final TextureRegion options;
    public final TextureRegion checkbox;

    public MenuUI(TextureHandler textureHandler){
        arrow = loadUI(textureHandler, "arrow");
        play = loadUI(textureHandler, "play");
        options = loadUI(textureHandler, "options");
        checkbox = loadUI(textureHandler, "checkbox");
    }

    private TextureRegion loadUI(TextureHandler textureHandler, String path){
        var pixmap = textureHandler.loadImageBuffer("assets/ui", path);
        var texture = new Texture(pixmap);
        return new TextureRegion(texture);
    }

    public void dispose(){
        arrow.getTexture().dispose();
        play.getTexture().dispose();
        options.getTexture().dispose();
        checkbox.getTexture().dispose();
    }
}
