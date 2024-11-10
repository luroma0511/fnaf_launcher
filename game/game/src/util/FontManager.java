package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private ShaderProgram outlineShader;
    private ShaderProgram shadowShader;
    private final GlyphLayout layout = new GlyphLayout();
    private final Map<String, BitmapFont> fontMap = new HashMap<>();
    private final Vector4 outlineColor = new Vector4(0, 0, 0, 1);
    private float outlineLength = 0.5f;
    private final Vector2 shadowOffset = new Vector2(0, 0);
    private final Vector4 shadowColor = new Vector4(0, 0, 0, 1);
    private float shadowSmoothing = 0.5f;
    private float textureSize;
    private String currentShader = "outline";
    private float x;
    private float y;

    private BitmapFont currentFont;

    public void load(){
        String vert = Loader.loadFile("res/shaders/font.vert");
        String frag = Loader.loadFile("res/shaders/outlineFont.frag");
        outlineShader = initShader(vert, frag);
        frag = Loader.loadFile("res/shaders/shadowFont.frag");
        shadowShader = initShader(vert, frag);
    }

    private ShaderProgram initShader(String vert, String frag){
        var shader = new ShaderProgram(vert, frag);
        if (!shader.isCompiled()) Gdx.app.error("shader", "compilation failed:\n" + shader.getLog());
        return shader;
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

    public void setRelativePosition(float x, float y) {
        setRelativePosition(false, false, x, y);
    }

    public void setPosition(boolean centerX, boolean centerY, float x, float y){
        if (centerX) x -= layout.width / 2;
        if (centerY) y += layout.height / 2;
        this.x = x;
        this.y = y;
    }

    public void setRelativePosition(boolean centerX, boolean centerY, float x, float y){
        if (centerX) x -= layout.width / 2;
        if (centerY) y += layout.height / 2;
        this.x = x + CameraManager.getX();
        this.y = y + CameraManager.getY();
    }

    private void setOutlineShader(SpriteBatch batch){
        batch.setShader(outlineShader);
        outlineShader.setUniformf("outlineLen", outlineLength);
        outlineShader.setUniformf("outlineColor", outlineColor);
    }

    private void setShadowShader(SpriteBatch batch){
        batch.setShader(shadowShader);
        shadowShader.setUniformf("shadowSmoothing", shadowSmoothing);
        shadowShader.setUniformf("shadowColor", shadowColor);
        shadowShader.setUniformf("shadowOffset", shadowOffset);
        shadowShader.setUniformf("textureSize", textureSize);
    }

    public void render(SpriteBatch batch) {
        if (currentShader.equals("outline")) setOutlineShader(batch);
        else setShadowShader(batch);
        currentFont.draw(batch, layout, x, y);
        batch.setShader(null);
    }

    public GlyphLayout getLayout(){
        return layout;
    }

    public void setOutlineLength(float outlineLength) {
        this.outlineLength = outlineLength;
    }

    public void setOutlineColor(Color color){
        setOutlineColor(color.r, color.g, color.b, color.a);
    }

    public void setOutlineColor(float r, float g, float b, float a){
        outlineColor.x = r;
        outlineColor.y = g;

        outlineColor.z = b;
        outlineColor.w = a;
    }

    public void setShadowColor(Color color){
        setShadowColor(color.r, color.g, color.b, color.a);
    }

    public void setShadowColor(float r, float g, float b, float a){
        shadowColor.x = r * 1.25f;
        shadowColor.y = g * 1.25f;
        shadowColor.z = b * 1.25f;
        shadowColor.w = a;
    }

    public void setShadowOffset(float x, float y){
        shadowOffset.x = x;
        shadowOffset.y = y;
    }

    public void setShadowSmoothing(float shadowSmoothing) {
        this.shadowSmoothing = shadowSmoothing;
    }

    public void setTextureSize(float textureSize) {
        this.textureSize = textureSize;
    }

    public void resetOutline(){
        setOutlineColor(0, 0, 0, 1);
        setOutlineLength(0.5f);
    }

    public void resetShadow(){
        setShadowColor(0, 0, 0, 1);
        setShadowOffset(0, 0);
        setShadowSmoothing(0.5f);
    }

    public void setCurrentShader(String currentShader) {
        this.currentShader = currentShader;
    }
}