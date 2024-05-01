package core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import deluxe.GameData;
import state.StateManager;
import util.*;

public class Candys3Deluxe extends ApplicationAdapter {
    public static InputManager inputManager;
    public static StateManager stateManager;

    public static BitmapFont candysFont;
    public static BitmapFont captionFont;

    public static Window window;

    public Candys3Deluxe(int width, int height){
        window = new Window(width, height);
    }

    @Override
    public void create(){
        CameraManager.createCamera(window.width(), window.height());
        inputManager = new InputManager();
        stateManager = new StateManager();
        Gdx.input.setInputProcessor(inputManager);
        Texture texture = FontManager.loadTexture("candysFont");
        candysFont = FontManager.loadFont("candysFont", texture);
        texture = FontManager.loadTexture("captionFont");
        captionFont = FontManager.loadFont("captionFont", texture);
    }

    @Override
    public void resize(int width, int height){
        if (CameraManager.getViewport() != null) CameraManager.apply(width, height);
    }

    @Override
    public void render() {
        Time.update();
        inputManager.fullscreen(window);
        stateManager.update(window, inputManager);
        if (RenderManager.requests()) inputManager.setLock();
        RenderManager.viewportAdjust(inputManager);
        stateManager.render(RenderManager.batch, window, inputManager);
        inputManager.reset();
    }

    @Override
    public void dispose () {
        SoundManager.dispose();
        VideoManager.dispose();
        ImageManager.dispose();
        stateManager.dispose();
        candysFont.dispose();
        captionFont.dispose();
        RenderManager.dispose();
    }

    public static void fontAlpha(BitmapFont font, float alpha, boolean tweak){
        if (tweak) alpha = 0.5f + alpha / 2;
        if (GameData.night == 0) font.setColor(1, 0, 0, alpha);
        else font.setColor(0.5f, 0, 1, alpha);
    }

    public static void setNightColor(SpriteBatch batch, float divider){
        setNightColor(batch, divider, 1);
    }

    public static void setNightColor(SpriteBatch batch, float divider, float alpha){
        if (GameData.night == 0) batch.setColor((float) 1 / divider, 0, 0, alpha);
        else batch.setColor(0.5f / divider, 0, (float) 1 / divider, alpha);
    }
}
