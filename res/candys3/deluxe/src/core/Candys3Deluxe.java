package core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import deluxe.GameData;
import state.StateManager;
import util.*;

public class Candys3Deluxe extends ApplicationAdapter {
    public static InputManager inputManager;
    public static StateManager stateManager;

    public static BitmapFont candysFont;
    public static BitmapFont aiFont;
    public static BitmapFont loadFont;
    public static BitmapFont debugFont;
    public static BitmapFont captionFont;

    public static Window window;

    public Candys3Deluxe(int width, int height){
        window = new Window(width, height);
    }

    @Override
    public void create(){
        CameraManager.createCamera(window.getWidth(), window.getHeight());
        inputManager = new InputManager();
        stateManager = new StateManager();
        RenderManager.init();
        Gdx.input.setInputProcessor(inputManager);
        FontManager.generate("candysFont");
        candysFont = FontManager.init(40);
        loadFont = FontManager.init(54);
        aiFont = FontManager.init(80);
        FontManager.dispose();
        FontManager.generate("captionFont");
        captionFont = FontManager.init(15);
        debugFont = FontManager.init(20);
        FontManager.dispose();
    }

    @Override
    public void resize(int width, int height){
        if (CameraManager.getViewport() != null) CameraManager.apply(width, height);
    }

    @Override
    public void render() {
        Time.update();
        inputManager.update();
        inputManager.fullscreen(window);
        stateManager.update(window, inputManager);
        if (RenderManager.requests()) inputManager.setLock();
        RenderManager.viewportAdjust(inputManager);
        stateManager.render(RenderManager.batch, window, inputManager);
        inputManager.reset();
    }

    @Override
    public void dispose () {
        candysFont.dispose();
        aiFont.dispose();
        loadFont.dispose();
        debugFont.dispose();
        captionFont.dispose();
        ImageManager.dispose();
        VideoManager.dispose();
        SoundManager.dispose();
        stateManager.dispose();
        RenderManager.dispose();
    }

    public static void fontAlpha(BitmapFont font, float alpha, boolean tweak){
        if (tweak) alpha = 0.5f + alpha / 2;
        if (GameData.night == 0) font.setColor(1, 0, 0, alpha);
        else font.setColor(0.5f, 0, 1, alpha);
    }

    public static void nightSetColor(SpriteBatch batch, float divider){
        nightSetColor(batch, divider, 1);
    }

    public static void nightSetColor(SpriteBatch batch, float divider, float alpha){
        if (GameData.night == 0) batch.setColor((float) 1 / divider, 0, 0, alpha);
        else batch.setColor(0.5f / divider, 0, (float) 1 / divider, alpha);
    }
}
