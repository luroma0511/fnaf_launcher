package util;

import com.badlogic.gdx.Gdx;
import util.ui.MenuUI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppHandler {
    private final ScheduledExecutorService timer;

    public final Window window;
    private InputManager input;
    private RenderHandler renderHandler;
    private FontManager fontManager;
    public final SoundHandler soundHandler;
    private TextureHandler textureHandler;
    private MenuUI menuUI;

    public AppHandler(int width, int height){
        window = new Window(width, height);
        FrameBufferManager.assignWindow(window);
        timer = Executors.newScheduledThreadPool(1);
        soundHandler = new SoundHandler();
    }

    public void init(){
        timer.scheduleAtFixedRate(Memory::update, 0 ,250, TimeUnit.MILLISECONDS);
        CameraManager.createCamera(window.width(), window.height());
        input = new InputManager();
        Gdx.input.setInputProcessor(input);
        renderHandler = new RenderHandler();
        textureHandler = new TextureHandler();
        fontManager = new FontManager();
        fontManager.load();
        menuUI = new MenuUI(textureHandler);
    }

    public InputManager getInput() {
        return input;
    }

    public RenderHandler getRenderHandler() {
        return renderHandler;
    }

    public TextureHandler getTextureHandler() {
        return textureHandler;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public MenuUI getMenuUI() {
        return menuUI;
    }

    public void dispose(){
        timer.shutdown();
        soundHandler.dispose();
        menuUI.dispose();
        textureHandler.dispose();
        renderHandler.dispose();
        fontManager.dispose();
    }
}
