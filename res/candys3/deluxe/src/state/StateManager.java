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
        else win.update();

        if (prevState == state) return;
        if (state == 0) menu.load();
        else if (state == 1) game.load();
        else win.load();
        prevState = state;
    }

    public void render(SpriteBatch batch, Window window, InputManager inputManager){
        ScreenUtils.clear(0, 0, 0, 1);
        FrameBuffer screenBuffer = RenderManager.screenBuffer;

        if (state == 0){
            screenBuffer.begin();
            menu.render(batch, window);
        } else if (state == 1) game.render(batch, window);
        else {
            screenBuffer.begin();
            ScreenUtils.clear(0, 0, 0, 1);
            win.render(batch);
        }
        debug(batch, inputManager);
        FrameBufferManager.end(batch, screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, screenBuffer, true);
        batch.end();
    }

    private void debug(SpriteBatch batch, InputManager inputManager){
        Candys3Deluxe.debugFont.draw(batch,
                "Mouse: " + (int) inputManager.getX() + " | " + (int) inputManager.getY(),
                CameraManager.getX() + 24, CameraManager.getY() + 696);
        Candys3Deluxe.debugFont.draw(batch,
                "Java path: " + JavaInfo.home,
                CameraManager.getX() + 24, CameraManager.getY() + 666);
        Candys3Deluxe.debugFont.draw(batch,
                "Java version: " + JavaInfo.jre,
                CameraManager.getX() + 24, CameraManager.getY() + 636);
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
