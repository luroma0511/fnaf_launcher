package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontManager {
    public static final GlyphLayout layout = new GlyphLayout();
    private static final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();;
    private static FreeTypeFontGenerator generator;

    public static void generateFont(String path){
        generator = new FreeTypeFontGenerator(Gdx.files.absolute(PathConstant.getAssetsPath() + "fonts/" + path + ".ttf"));
    }

    public static BitmapFont initializeFont(int size){
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        return font;
    }

    public static void dispose(){
        generator.dispose();
    }
}
