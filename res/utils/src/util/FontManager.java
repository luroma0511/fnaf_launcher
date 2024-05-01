package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector4;

public class FontManager {
    private static final GlyphLayout layout = new GlyphLayout();
    private static final ShaderProgram shader;
    private static BitmapFont font;
    private static float outline = 0.5f;
    private static final Vector4 color = new Vector4(0, 0, 0, 1);

    static {
        shader = new ShaderProgram(Gdx.files.local("../utils/shaders/font.vert"), Gdx.files.local("../utils/shaders/font.frag"));
        if (!shader.isCompiled()) Gdx.app.error("shader", "compilation failed:\n" + shader.getLog());
    }

    public static Texture loadTexture(String path){
        Texture texture = new Texture(Gdx.files.local("assets/fonts/" + path + ".png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        return texture;
    }

    public static BitmapFont loadFont(String path, Texture texture){
        BitmapFont font = new BitmapFont(Gdx.files.local("assets/fonts/" + path + ".fnt"), new TextureRegion(texture), false);
        font.setUseIntegerPositions(false);
        return font;
    }

    public static void setText(String text){
        layout.setText(font, text);
    }

    public static void setFont(BitmapFont font) {
        FontManager.font = font;
    }

    public static void setSize(float size){
        font.getData().setScale(size / 32);
    }

    public static void render(SpriteBatch batch, float x, float y) {
        render(batch, false, false, x, y);
    }

    public static void render(SpriteBatch batch, boolean centerX, boolean centerY, float x, float y) {
        batch.setShader(shader);
        shader.setUniformf("outlineLen", outline);
        shader.setUniformf("outlineColor", color);
        if (centerX) x -= layout.width / 2;
        if (centerY) y += layout.height / 2;
        font.draw(batch, layout, x, y);
        batch.setShader(null);
    }

    public static GlyphLayout getLayout(){
        return layout;
    }

    public static void setOutline(float outline) {
        FontManager.outline = outline;
    }

    public static void setColor(float r, float g, float b, float a){
        color.x = r;
        color.y = g;
        color.z = b;
        color.w = a;
    }
}