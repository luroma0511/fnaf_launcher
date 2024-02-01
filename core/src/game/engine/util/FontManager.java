package game.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontManager {
    private final BitmapFont candysFont;
    private final BitmapFont captionFont;

    public FontManager(){
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/candysFont.ttf"));
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/captionFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;

        captionFont = generator2.generateFont(parameter);
        captionFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 40;
        candysFont = generator1.generateFont(parameter);
        candysFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator1.dispose();
        generator2.dispose();
    }

    public void dispose(){
        candysFont.dispose();
        captionFont.dispose();
    }

    public BitmapFont getCandysFont() {
        return candysFont;
    }

    public BitmapFont getCaptionFont() {
        return captionFont;
    }
}
