package state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Candys3Deluxe;
import state.Game.Game;
import state.Win.Win;
import state.Menu.Menu;
import util.*;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private final Win win;
    private int state;
    private int prevState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        win = new Win();
        state = 0;
        prevState = -1;
    }

    public void update(Window window, InputManager inputManager){
        if (state == 0) menu.update(window, inputManager);
        else if (state == 1) game.update(window, inputManager);
        else if (state == 2) win.update();

        if (prevState == state) return;
        if (state == 0) menu.load();
        else if (state == 1) game.load();
        else if (state == 2) win.load();
        prevState = state;
    }

    public void render(SpriteBatch batch, Window window, InputManager inputManager){
        ScreenUtils.clear(0, 0, 0, 1);
        FrameBuffer screenBuffer = RenderManager.screenBuffer;

        if (state == 0) menu.render(batch, window);
        else if (state == 1) game.render(batch, window);
        else {
            screenBuffer.begin();
            ScreenUtils.clear(0, 0, 0, 1);
            win.render(batch);
        }
//        debug(batch, inputManager);
        RenderManager.shapeDrawer.setColor(0, 0, 0, 1 - RenderManager.screenAlpha);
        RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);
        FrameBufferManager.end(batch, screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, screenBuffer, true);
        batch.end();
    }

    private void debug(SpriteBatch batch, InputManager inputManager){
        FontManager.setFont(Candys3Deluxe.captionFont);
        FontManager.setSize(18);
        FontManager.setText("Mouse: " + (int) inputManager.getX() + " | " + (int) inputManager.getY());
        FontManager.render(batch, CameraManager.getX() + 16, CameraManager.getY() + 704);
        FontManager.setText("Java version: " + Constants.jre);
        FontManager.render(batch, CameraManager.getX() + 16, CameraManager.getY() + 679);
    }

    public void dispose(){
        game.dispose();
    }

    public int getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
