package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {
    public static final GlyphLayout layout = new GlyphLayout();
    private static final FreeTypeFontParameter params = new FreeTypeFontParameter();
    private static FreeTypeFontGenerator fontGen;

    public static void generate(String path){
        fontGen = new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/" + path + ".ttf"));
    }

    public static BitmapFont init(int size){
        params.size = size;
        BitmapFont font = fontGen.generateFont(params);
        font.setUseIntegerPositions(false);
        return font;
    }

    public static void dispose(){
        fontGen.dispose();
    }
}