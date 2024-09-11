package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector4;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private final GlyphLayout layout = new GlyphLayout();
    private final ShaderProgram shader;
    private float outline = 0.5f;
    private float x;
    private float y;
    private final Vector4 color = new Vector4(0, 0, 0, 1);
    private final Map<String, BitmapFont> fontMap = new HashMap<>();

    private BitmapFont currentFont;

    {
        shader = new ShaderProgram(Gdx.files.local("game/shaders/font.vert"), Gdx.files.local("game/shaders/font.frag"));
        if (!shader.isCompiled()) Gdx.app.error("shader", "compilation failed:\n" + shader.getLog());
    }

    public void addFont(String name){
        if (fontMap.containsKey(name)) return;
        Texture texture = loadTexture(name);
        BitmapFont font = loadFont(name, texture);
        fontMap.put(name, font);
    }

    public BitmapFont getFont(String name){
        return fontMap.get(name);
    }

    public void setCurrentFont(BitmapFont currentFont) {
        this.currentFont = currentFont;
    }

    public void dispose(){
        for (BitmapFont font: fontMap.values()){
            font.dispose();
        }
        fontMap.clear();
    }

    private Texture loadTexture(String name){
        Texture texture = new Texture(Gdx.files.local("assets/fonts/" + name + ".png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        return texture;
    }

    private BitmapFont loadFont(String name, Texture texture){
        BitmapFont font = new BitmapFont(Gdx.files.local("assets/fonts/" + name + ".fnt"), new TextureRegion(texture), false);
        font.setUseIntegerPositions(false);
        return font;
    }

    public void setText(String text){
        layout.setText(currentFont, text);
    }

    public void setSize(float size){
        currentFont.getData().setScale(size / 32);
    }

    public void setPosition(float x, float y) {
        setPosition(false, false, x, y);
    }

    public void setPosition(boolean centerX, boolean centerY, float x, float y){
        if (centerX) x -= layout.width / 2;
        if (centerY) y += layout.height / 2;
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        batch.setShader(shader);
        shader.setUniformf("outlineLen", outline);
        shader.setUniformf("outlineColor", color);
        currentFont.draw(batch, layout, x, y);
        batch.setShader(null);
    }

    public GlyphLayout getLayout(){
        return layout;
    }

    public void setOutline(float outline) {
        this.outline = outline;
    }

    public void setColor(float r, float g, float b, float a){
        color.x = r;
        color.y = g;
        color.z = b;
        color.w = a;
    }
}