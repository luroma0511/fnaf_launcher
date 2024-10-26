package util.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.TextureHandler;

public class MenuUI {
    public final TextureRegion arrow;
    public final TextureRegion button;
    public final TextureRegion checkbox;

    public MenuUI(TextureHandler textureHandler){
        var pixmap = textureHandler.loadImageBuffer("assets/ui", "arrow");
        var texture = new Texture(pixmap);
        arrow = new TextureRegion(texture);

        pixmap = textureHandler.loadImageBuffer("assets/ui", "button");
        texture = new Texture(pixmap);
        button = new TextureRegion(texture);

        pixmap = textureHandler.loadImageBuffer("assets/ui", "checkbox");
        texture = new Texture(pixmap);
        checkbox = new TextureRegion(texture);
    }

    public void dispose(){
        arrow.getTexture().dispose();
        button.getTexture().dispose();
        checkbox.getTexture().dispose();
    }
}
