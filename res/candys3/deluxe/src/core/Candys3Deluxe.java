package core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import discord.Discord;
import state.StateManager;
import util.*;

public class Candys3Deluxe extends ApplicationAdapter {
    public static InputManager inputManager;
    public static RenderManager renderManager;
    public static StateManager stateManager;

    public static BitmapFont candysFont;
    public static BitmapFont aiFont;
    public static BitmapFont debugFont;
    public static BitmapFont captionFont;

    private static boolean fullscreen;
    private static Graphics.DisplayMode displayMode;
    public static Window window;

    public Candys3Deluxe(int width, int height){
        window = new Window(width, height);
    }

    @Override
    public void create(){
        Discord.init();
        inputManager = new InputManager();
        stateManager = new StateManager();
        CameraManager.createCamera(window.getWidth(), window.getHeight());
        renderManager = new RenderManager();
        Gdx.input.setInputProcessor(inputManager);
        displayMode = Gdx.graphics.getDisplayMode();
        SoundManager.add("menu");
        SoundManager.add("thunder");

        FontManager.generate("candysFont");
        candysFont = FontManager.init(40);
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
        fullscreen = inputManager.fullscreen(displayMode, fullscreen, window.getWidth(), window.getHeight());
        stateManager.update(window);
        if (renderManager.requests()) inputManager.setLock();
        renderManager.viewportAdjust(inputManager);
        stateManager.render(window);
        inputManager.reset();
        Discord.print();
    }

    @Override
    public void dispose () {
        candysFont.dispose();
        aiFont.dispose();
        debugFont.dispose();
        captionFont.dispose();
        ImageManager.dispose();
        VideoManager.dispose();
        SoundManager.dispose();
        stateManager.dispose();
        renderManager.dispose();
    }
}
